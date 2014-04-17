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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.dao.query.LazyList;
import de.greenrobot.dao.query.QueryBuilder;
import name.soulayrol.rhaa.sholi.data.model.Checkable;
import name.soulayrol.rhaa.sholi.data.model.Item;
import name.soulayrol.rhaa.sholi.data.model.ItemDao;


public class CheckingFragment extends AbstractListFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_checking, container, false);

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.checking, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_check_all:
                updateAllItems(Checkable.CHECKED);
                return true;
            case R.id.action_uncheck_all:
                updateAllItems(Checkable.UNCHECKED);
                return true;
            case R.id.action_remove_checked:
                updateAllItems(Checkable.OFF_LIST, Checkable.CHECKED);
                return true;
            case R.id.action_empty:
                updateAllItems(Checkable.OFF_LIST);
                return true;
            case R.id.action_edit:
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.container, new EditFragment());
                transaction.addToBackStack(null);
                transaction.commit();
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

    protected void updateAllItems(int status) {
        updateAllItems(status, -1);
    }

    protected void updateAllItems(final int status, final int prev_status) {
        List<Item> items = new ArrayList<Item>();
        Item item = null;

        for (int i = 0; i < getAdapter().getCount(); ++i) {
            item = (Item) getAdapter().getItem(i);
            if (prev_status == -1 || item.getStatus() == prev_status) {
                item.setStatus(status);
                items.add(item);
            }
        }

        getSession().getItemDao().updateInTx(items);
        getAdapter().setLazyList(createList(getActivity()));
    }
}
