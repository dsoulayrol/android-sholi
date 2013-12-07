/*
 * ShoLi, a simple tool to produce short (shopping) lists.
 * Copyright (C) 2013  David Soulayrol
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

import android.app.FragmentTransaction;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;


import name.soulayrol.rhaa.sholi.data.Sholi;


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
                updateAllItems(getAdapter().getCursor(), Sholi.Item.CHECKED);
                return true;
            case R.id.action_uncheck_all:
                updateAllItems(getAdapter().getCursor(), Sholi.Item.UNCHECKED);
                return true;
            case R.id.action_empty:
                updateAllItems(getAdapter().getCursor(), Sholi.Item.OFF_LIST);
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
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        StringBuilder buffer = new StringBuilder();
        String[] args = new String[] {
                String.valueOf(Sholi.Item.CHECKED),
                String.valueOf(Sholi.Item.UNCHECKED) };

        buffer.append(Sholi.Item.KEY_STATUS);
        buffer.append("=? OR ");
        buffer.append(Sholi.Item.KEY_STATUS);
        buffer.append("=?");

        return new CursorLoader(getActivity(), Sholi.Item.CONTENT_URI,
                PROJECTION, buffer.toString(), args, ORDER);
    }

    @Override
    protected void updateItem(Cursor c, Uri uri) {
        ContentValues values = new ContentValues();
        c.moveToFirst();
        switch (c.getInt(c.getColumnIndex(Sholi.Item.KEY_STATUS))) {
            case Sholi.Item.OFF_LIST:
                // Should not happen.
                break;
            case Sholi.Item.UNCHECKED:
                values.put(Sholi.Item.KEY_STATUS, Sholi.Item.CHECKED);
                break;
            case Sholi.Item.CHECKED:
                values.put(Sholi.Item.KEY_STATUS, Sholi.Item.UNCHECKED);
                break;
        }

        if (values != null) {
            getContent().update(uri, values, null, null);
            getAdapter().notifyDataSetChanged();
        }
    }

    protected void updateAllItems(Cursor c, int status) {
        if (c.getCount() > 0)
        {
            StringBuilder buffer = new StringBuilder();
            ContentValues values = new ContentValues();

            values.put(Sholi.Item.KEY_STATUS, status);

            buffer.append("_ID IN (");
            c.moveToPosition(-1);
            while (c.moveToNext()) {
                buffer.append(c.getLong(0));
                if (!c.isLast())
                    buffer.append(",");
            }
            buffer.append(")");

            getContent().update(Sholi.Item.CONTENT_URI, values, buffer.toString(), null);
            getAdapter().notifyDataSetChanged();
        }
    }
}
