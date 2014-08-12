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

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.dao.query.QueryBuilder;
import name.soulayrol.rhaa.sholi.CheckingFragment;
import name.soulayrol.rhaa.sholi.R;
import name.soulayrol.rhaa.sholi.SettingsActivity;
import name.soulayrol.rhaa.sholi.data.model.Checkable;
import name.soulayrol.rhaa.sholi.data.model.Item;
import name.soulayrol.rhaa.sholi.data.model.ItemDao;

public abstract class Action {

    public abstract static class TestableAction extends Action {

        protected abstract String getConfigurationKey();

        protected abstract int getMessageId();

        @Override
        public void proceed(final CheckingFragment fragment) {
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(fragment.getActivity());
            if (sharedPref.getBoolean(getConfigurationKey(), false)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(fragment.getActivity());
                builder.setIcon(android.R.drawable.ic_dialog_alert)
                        .setMessage(getMessageId())
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                TestableAction.super.proceed(fragment);
                            }
                        })
                        .setNegativeButton(android.R.string.no, null).create().show();
            } else
                super.proceed(fragment);
        }
    }

    public static class CheckAll extends Action {

        @Override
        public boolean doProceed(CheckingFragment fragment) {
            int items = updateItems(fragment, Checkable.CHECKED, Checkable.UNCHECKED);
            if (items != 0) {
                _description = fragment.getResources().getQuantityString(
                        R.plurals.action_check, items, items);
            }
            return items != 0;
        }
    }

    public static class UncheckAll extends Action {

        @Override
        public boolean doProceed(CheckingFragment fragment) {
            int items = updateItems(fragment, Checkable.UNCHECKED, Checkable.CHECKED);
            if (items != 0) {
                _description = fragment.getResources().getQuantityString(
                        R.plurals.action_uncheck, items, items);
            }
            return items != 0;
        }
    }

    public static class CheckOrUncheckAll extends Action {
        @Override
        public boolean doProceed(CheckingFragment fragment) {
            Action action;
            if (getNbItemsByStatus(fragment, Checkable.UNCHECKED) > 0)
                action = new CheckAll();
            else
                action = new UncheckAll();
            return delegate(fragment, action);
        }
    }

    public static class UncheckOrCheckAll extends Action {
        @Override
        public boolean doProceed(CheckingFragment fragment) {
            Action action;
            if (getNbItemsByStatus(fragment, Checkable.CHECKED) > 0)
                action = new UncheckAll();
            else
                action = new CheckAll();
            return delegate(fragment, action);
        }
    }

    public static class RemoveChecked extends TestableAction {

        @Override
        protected String getConfigurationKey() {
            return SettingsActivity.KEY_CONFIRM_REMOVE_CHECKED_ACTION;
        }

        protected int getMessageId() {
            return R.string.dialog_checking_confirm_remove_checked;
        }

        @Override
        public boolean doProceed(CheckingFragment fragment) {
            int items = updateItems(fragment, Checkable.OFF_LIST, Checkable.CHECKED);
            if (items != 0) {
                _description = fragment.getResources().getQuantityString(
                        R.plurals.action_remove, items, items);
            }
            return items != 0;
        }
    }

    public static class Empty extends TestableAction {

        @Override
        protected String getConfigurationKey() {
            return SettingsActivity.KEY_CONFIRM_REMOVE_ALL_ACTION;
        }

        @Override
        protected int getMessageId() {
            return R.string.dialog_checking_confirm_remove_all;
        }

        @Override
        public boolean doProceed(CheckingFragment fragment) {
            int items = updateAllItems(fragment, Checkable.OFF_LIST);
            if (items != 0) {
                _description = fragment.getResources().getQuantityString(
                        R.plurals.action_remove, items, items);
            }
            return items != 0;
        }
    }

    protected String _description;

    protected boolean _success;

    public void proceed(final CheckingFragment fragment) {
        _success = doProceed(fragment);
        fragment.onActionDone(this);
    }

    public abstract boolean doProceed(CheckingFragment fragment);

    public String getDescription() {
        return _description;
    }

    public boolean isSuccessful() {
        return _success;
    }

    protected boolean delegate(CheckingFragment fragment, Action action) {
        _success = action.doProceed(fragment);
        _description = action.getDescription();

        return _success;
    }

    protected int updateAllItems(CheckingFragment fragment, int status) {
        return updateItems(fragment, status, -1);
    }

    protected int updateItems(CheckingFragment fragment, final int status, final int prevStatus) {
        AbstractLazyListAdapter adapter = fragment.getAdapter();
        List<Item> items = new ArrayList<Item>();
        Item item;

        for (int i = 0; i < adapter.getCount(); ++i) {
            item = (Item) adapter.getItem(i);
            if (prevStatus == -1 || item.getStatus() == prevStatus) {
                item.setStatus(status);
                items.add(item);
            }
        }

        fragment.getSession().getItemDao().updateInTx(items);
        return items.size();
    }

    protected long getNbItemsByStatus(CheckingFragment fragment, int status) {
        QueryBuilder builder = fragment.getSession().getItemDao().queryBuilder();
        return builder.where(ItemDao.Properties.Status.eq(status)).buildCount().count();
    }
}
