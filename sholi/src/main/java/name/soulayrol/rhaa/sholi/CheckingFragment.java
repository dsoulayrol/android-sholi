/*
 * ShoLi, a simple tool to produce short lists.
 * Copyright (C) 2013,2014  David Soulayrol
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
package name.soulayrol.rhaa.sholi;

import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import de.greenrobot.dao.query.LazyList;
import de.greenrobot.dao.query.QueryBuilder;
import name.soulayrol.rhaa.sholi.data.Action;
import name.soulayrol.rhaa.sholi.data.model.Checkable;
import name.soulayrol.rhaa.sholi.data.model.Item;
import name.soulayrol.rhaa.sholi.data.model.ItemDao;


public class CheckingFragment extends AbstractListFragment {

    private ListView _listView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final GestureDetector detector = new GestureDetector(getActivity(), new GestureListener());
        View view = inflater.inflate(R.layout.fragment_checking, container, false);
        registerForContextMenu(view);

        // It is too early to call getListView here, so we fetch the view from its ID.
        _listView = (ListView) view.findViewById(android.R.id.list);
        _listView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return detector.onTouchEvent(event);
            }
        });
        return view;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.checking_context, menu);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.checking, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_check_all:
                doAction(new Action.CheckAll());
                return true;
            case R.id.action_uncheck_all:
                doAction(new Action.UncheckAll());
                return true;
            case R.id.action_remove_checked:
                doAction(new Action.RemoveChecked());
                return true;
            case R.id.action_empty:
                doAction(new Action.Empty());
                return true;
        }
        return super.onContextItemSelected(item);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_edit:
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.container, new EditFragment());
                transaction.addToBackStack(null);
                transaction.commit();
                return true;
            case R.id.action_menu:
                getActivity().openContextMenu(_listView);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected LazyList<Item> createList(Context context) {
        QueryBuilder builder = getSession().getItemDao().queryBuilder();
        builder.where(builder.or(ItemDao.Properties.Status.eq(Checkable.CHECKED),
                ItemDao.Properties.Status.eq(Checkable.UNCHECKED)));
        builder.orderAsc(ItemDao.Properties.Name);
        return builder.listLazy();
    }

    @Override
    protected void updateItem(Item item) {
        switch (item.getStatus()) {
            case Checkable.OFF_LIST:
                // Should not happen.
                break;
            case Checkable.UNCHECKED:
                item.setStatus(Checkable.CHECKED);
                break;
            case Checkable.CHECKED:
                item.setStatus(Checkable.UNCHECKED);
                break;
        }

        getSession().getItemDao().update(item);
        getAdapter().notifyDataSetChanged();
    }

    private void doAction(Action action) {
        if (action.proceed(this))
            getAdapter().setLazyList(createList(getActivity()));
    }

    private class GestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public void onLongPress(MotionEvent e) {
            CheckingFragment.this.getActivity().openContextMenu(_listView);
        }

        @Override
        public boolean onFling(MotionEvent start, MotionEvent end,
                float velocityX, float velocityY) {
            // TODO: identify the direction
            // TODO: Check the event large enough
            //float range = end.getAxisValue(MotionEvent.AXIS_X) - start.getAxisValue(MotionEvent.AXIS_X);
            if (end.getEventTime() - end.getDownTime() < 1000)
                doAction(new Action.RemoveChecked());
            return true;
        }
    }
}
