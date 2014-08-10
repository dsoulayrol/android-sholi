/*
 * ShoLi, a simple tool to produce short lists.
 * Copyright (C) 2014  David Soulayrol
 *
 * ShoLi is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ShoLi is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package name.soulayrol.rhaa.widget;

import android.content.Context;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.ViewConfiguration;
import android.widget.FrameLayout;

import java.util.HashMap;
import java.util.Map;

import name.soulayrol.rhaa.sholi.data.Action;


/**
 * A FrameLayout able to intercept multi-touch gestures.
 */
public class InterceptorFrameLayout extends FrameLayout {

    public enum Gesture {
        SINGLE_TAP,
        FLING_TO_LEFT,
        FLING_TO_RIGHT,
    }

    public interface Listener {
        public void execute(Action action);
    }

    Listener _listener;

    private Map<Gesture, Action> _actions;

    private int _touchSlope;

    private int _minFlingVelocity;

    /**
     * The information detailing the current gesture.
     *
     * If this object is null, then no gesture has been detected
     * and the event shall be dispatched downward.
     */
    private EventTracker _eventTracker;

    public InterceptorFrameLayout(Context context, android.util.AttributeSet set) {
        super(context, set);

        ViewConfiguration vc = ViewConfiguration.get(context);

        _actions = new HashMap<Gesture, Action>();
        _touchSlope = vc.getScaledTouchSlop();
        _minFlingVelocity = vc.getScaledMinimumFlingVelocity();
    }

    public void startInterception(Listener listener) {
        _listener = listener;
    }

    public void configure(Gesture gesture, Action action) {
        _actions.put(gesture, action);
    }

    public void ignore(Gesture gesture) {
        _actions.remove(gesture);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (_listener == null)
            return false;

        switch (event.getActionMasked()) {
            // Always handle the case of the touch gesture being complete.
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                _eventTracker = null;
                break;

            case MotionEvent.ACTION_POINTER_DOWN:
                _eventTracker = new EventTracker(event);
                break;
        }

        return _eventTracker != null;
    }

    public boolean onTouchEvent(MotionEvent event) {
        if (_eventTracker != null) {
            _eventTracker = _eventTracker.update(event);
        }
        return _eventTracker != null;
    }

    private void execute(Gesture gesture) {
        if (_actions.containsKey(gesture))
            _listener.execute(_actions.get(gesture));
    }

    private class EventTracker {

        private int _id;

        private float _startX;

        private float _startY;

        private VelocityTracker _vTracker;

        public EventTracker(MotionEvent event) {
            final int idx = event.getActionIndex();
            _startX = event.getX(idx);
            _startY = event.getY(idx);
            _id = event.getPointerId(idx);
            _vTracker = VelocityTracker.obtain();
            _vTracker.addMovement(event);
        }

        public EventTracker update(MotionEvent event) {
            switch (event.getActionMasked()) {
                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_UP:
                    return null;
                case MotionEvent.ACTION_POINTER_UP:
                    _vTracker.addMovement(event);
                    handleGesture(event);
                    return null;
                case MotionEvent.ACTION_MOVE:
                    _vTracker.addMovement(event);
                    break;
            }
            return this;
        }

        private void handleGesture(MotionEvent event) {
            int i = event.findPointerIndex(_id);
            int dx = (int) (event.getX(i) - _startX);
            int dy = (int) (event.getY(i) - _startY);
            if (Math.abs(dx) < _touchSlope && Math.abs(dy) < _touchSlope) {
                execute(Gesture.SINGLE_TAP);
            } else if (Math.abs(dy) < Math.abs(dx / 5)) {
                _vTracker.computeCurrentVelocity(1000);
                if (Math.abs(_vTracker.getXVelocity()) > _minFlingVelocity) {
                    execute((dx < 0)? Gesture.FLING_TO_LEFT: Gesture.FLING_TO_RIGHT);
                }
            }
            _vTracker.recycle();
            _vTracker = null;
        }
    }
}
