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

import android.app.ListFragment;
import android.app.LoaderManager;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import name.soulayrol.rhaa.sholi.data.Sholi;


public abstract class AbstractListFragment extends ListFragment implements
        LoaderManager.LoaderCallbacks<Cursor> {

    /**
     * The columns we are interested in.
     */
    protected static final String[] PROJECTION = new String[]{
            Sholi.Item._ID,
            Sholi.Item.KEY_NAME,
            Sholi.Item.KEY_STATUS
    };

    /**
     * The common order used to present the items in a list.
     */
    protected static final String ORDER = Sholi.Item.KEY_NAME + " ASC";

    private ContentResolver _content;

    private Adapter _adapter;

    private String _defaultItemSize;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        _content = getActivity().getContentResolver();
        _defaultItemSize = getResources().getString(R.string.settings_items_size_default_value);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        _adapter = new Adapter(getActivity());

        setHasOptionsMenu(true);
        setListAdapter(_adapter);
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        _adapter._textSize = Integer.valueOf(sharedPref.getString(
                SettingsActivity.KEY_LIST_ITEM_SIZE, _defaultItemSize));
        _adapter.notifyDataSetChanged();
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Uri uri = ContentUris.withAppendedId(Sholi.Item.CONTENT_URI, id);
        updateItem(_content.query(uri, PROJECTION, null, null, null), uri);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        _adapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        _adapter.swapCursor(null);
    }

    protected class Adapter extends SimpleCursorAdapter {

        private int _textSize;

        public Adapter(Context context) {
            super(context, R.layout.list_item, null,
                    new String[]{Sholi.Item.KEY_NAME}, new int[]{
                    android.R.id.text1}, 0);
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            TextView item = (TextView) view.findViewById(R.id.item_name);
            item.setText(cursor.getString(cursor.getColumnIndex(Sholi.Item.KEY_NAME)));
            item.setTextSize(_textSize);
            switch (cursor.getInt(cursor.getColumnIndex(Sholi.Item.KEY_STATUS))) {
                case Sholi.Item.OFF_LIST:
                    item.setPaintFlags(item.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
                    item.setTextColor(Color.GRAY);
                    break;
                case Sholi.Item.UNCHECKED:
                    item.setPaintFlags(item.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
                    item.setTextColor(Color.GREEN);
                    break;
                case Sholi.Item.CHECKED:
                    item.setPaintFlags(item.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    item.setTextColor(Color.WHITE);
                    break;
            }
        }

        @Override
        public CharSequence convertToString(Cursor cursor) {
            return cursor.getString(cursor.getColumnIndex(Sholi.Item.KEY_NAME));
        }
    }

    protected Adapter getAdapter() {
        return _adapter;
    }

    protected ContentResolver getContent() {
        return _content;
    }

    protected abstract void updateItem(Cursor c, Uri uri);
}
