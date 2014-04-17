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

 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package name.soulayrol.rhaa.sholi;

import android.app.ListFragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ListView;

import de.greenrobot.dao.query.LazyList;
import name.soulayrol.rhaa.sholi.data.AbstractLazyListAdapter;
import name.soulayrol.rhaa.sholi.data.ItemLazyListAdapter;
import name.soulayrol.rhaa.sholi.data.Operations;
import name.soulayrol.rhaa.sholi.data.model.DaoSession;
import name.soulayrol.rhaa.sholi.data.model.Item;


public abstract class AbstractListFragment extends ListFragment {

    private DaoSession _session;

    private AbstractLazyListAdapter _adapter;

    private String _defaultItemSize;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        _defaultItemSize = getResources().getString(R.string.settings_items_size_default_value);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        _session = Operations.openSession(getActivity());
        _adapter = new ItemLazyListAdapter(getActivity(), createList(getActivity()), 0);

        setHasOptionsMenu(true);
        setListAdapter(_adapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        _adapter.setTextSize(Integer.valueOf(sharedPref.getString(
                SettingsActivity.KEY_LIST_ITEM_SIZE, _defaultItemSize)));
        _session.clear();
        _adapter.setLazyList(createList(getActivity()));
    }

    @Override
    public void onDestroy() {
        getAdapter().setLazyList(null);
        super.onDestroy();
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        updateItem((Item) _adapter.getItem(position));
    }

    protected DaoSession getSession() {
        return _session;
    }

    protected AbstractLazyListAdapter getAdapter() {
        return _adapter;
    }

    protected abstract LazyList<Item> createList(Context context);

    protected abstract void updateItem(Item item);
}
