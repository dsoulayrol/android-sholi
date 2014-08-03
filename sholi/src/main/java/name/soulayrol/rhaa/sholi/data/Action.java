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

import java.util.ArrayList;
import java.util.List;

import name.soulayrol.rhaa.sholi.CheckingFragment;
import name.soulayrol.rhaa.sholi.R;
import name.soulayrol.rhaa.sholi.data.model.Checkable;
import name.soulayrol.rhaa.sholi.data.model.Item;

public abstract class Action {

    public static class CheckAll extends Action {

        @Override
        public boolean proceed(CheckingFragment fragment) {
            int items = updateAllItems(fragment, Checkable.CHECKED);
            if (items != 0) {
                _description = fragment.getResources().getQuantityString(
                        R.plurals.action_check, items, items);
            }
            return items != 0;
        }
    }

    public static class UncheckAll extends Action {

        @Override
        public boolean proceed(CheckingFragment fragment) {
            int items = updateAllItems(fragment, Checkable.UNCHECKED);
            if (items != 0) {
                _description = fragment.getResources().getQuantityString(
                        R.plurals.action_uncheck, items, items);
            }
            return items != 0;
        }
    }

    public static class RemoveChecked extends Action {

        @Override
        public boolean proceed(CheckingFragment fragment) {
            int items = updateAllItems(fragment, Checkable.OFF_LIST, Checkable.CHECKED);
            if (items != 0) {
                _description = fragment.getResources().getQuantityString(
                        R.plurals.action_remove, items, items);
            }
            return items != 0;
        }
    }

    public static class Empty extends Action {

        @Override
        public boolean proceed(CheckingFragment fragment) {
            int items = updateAllItems(fragment, Checkable.OFF_LIST);
            if (items != 0) {
                _description = fragment.getResources().getQuantityString(
                        R.plurals.action_remove, items, items);
            }
            return items != 0;
        }
    }

    protected String _description;

    public abstract boolean proceed(CheckingFragment fragment);

    public String getDescription() {
        return _description;
    }

    protected int updateAllItems(CheckingFragment fragment, int status) {
        return updateAllItems(fragment, status, -1);
    }

    protected int updateAllItems(CheckingFragment fragment, final int status, final int prev_status) {
        AbstractLazyListAdapter adapter = fragment.getAdapter();
        List<Item> items = new ArrayList<Item>();
        Item item;

        for (int i = 0; i < adapter.getCount(); ++i) {
            item = (Item) adapter.getItem(i);
            if (prev_status == -1 || item.getStatus() == prev_status) {
                item.setStatus(status);
                items.add(item);
            }
        }

        fragment.getSession().getItemDao().updateInTx(items);
        return items.size();
    }
}
