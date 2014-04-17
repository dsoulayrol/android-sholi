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
package name.soulayrol.rhaa.sholi.data;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import de.greenrobot.dao.query.LazyList;
import name.soulayrol.rhaa.sholi.data.model.PersistentObject;


public abstract class AbstractLazyListAdapter<T extends PersistentObject> extends BaseAdapter {

    private LazyList<T> _lazyList;
    private Context _context;
    private int _textSize;

    public AbstractLazyListAdapter(Context context, LazyList<T> lazyList, int textSize) {
        this._lazyList = lazyList;
        this._context = context;
        _textSize = textSize;
    }

    public LazyList<T> getLazyList() {
        return _lazyList;
    }

    public void setLazyList(LazyList<T> list) {
    if (list != _lazyList) {
        _lazyList.close();
        _lazyList = list;
        notifyDataSetChanged();
    }
}
    public int getTextSize() {
        return _textSize;
    }

    public void setTextSize(int textSize) {
        _textSize = textSize;
    }

    @Override
    public int getCount() {
        if (_lazyList != null) {
            return _lazyList.size();
        } else {
            return 0;
        }
    }

    @Override
    public Object getItem(int position) {
        if (_lazyList != null) {
            return _lazyList.get(position);
        } else {
            return null;
        }
    }

    @Override
    public long getItemId(int position) {
        if (_lazyList != null) {
            T item = _lazyList.get(position);
            if (item != null)
                return item.getId();
        }
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (_lazyList == null)
            throw new IllegalStateException("Empty adapter");

        T item = _lazyList.get(position);
        if (item == null) {
            throw new IllegalStateException("Item at position " + position + " is null");
        }

        View v;
        if (convertView == null) {
            v = newView(_context, item, parent);
        } else {
            v = convertView;
        }
        bindView(v, _context, item);
        return v;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    public abstract View newView(Context context, T item, ViewGroup parent);

    public abstract void bindView(View view, Context context, T item);
}
