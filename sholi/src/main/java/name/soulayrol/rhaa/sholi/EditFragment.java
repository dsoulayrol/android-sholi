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

import android.app.ActionBar;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import name.soulayrol.rhaa.sholi.data.Sholi;


public class EditFragment extends AbstractListFragment {

    private static final String BUNDLE_KEY_FILTER = "filter";

    private Button _newItemButton;

    private TextView _newItemEdit;

    private boolean _editMode = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_edit, container, false);

        _newItemButton = (Button) view.findViewById(R.id.list_btn);
        _newItemEdit = (EditText) view.findViewById(R.id.list_edit);

        _newItemEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                Bundle b = new Bundle();
                b.putString(BUNDLE_KEY_FILTER, editable.toString());
                getLoaderManager().restartLoader(0, b, EditFragment.this);
            }
        });

        _newItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (addItem(_newItemEdit.getText().toString().trim()) != null)
                    _newItemEdit.setText("");
            }
        });

        // It is too early to call getListView here, so we fetch the view from its ID.
        ListView listView = (ListView) view.findViewById(android.R.id.list);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        listView.setMultiChoiceModeListener(new SelectionModeHandler());

        ActionBar bar = getActivity().getActionBar();
        bar.setDisplayHomeAsUpEnabled(true);
        bar.setSubtitle(R.string.fragment_edit_title);

        return view;
    }

    @Override
    public void onDestroy() {
        getActivity().getActionBar().setSubtitle(null);
        super.onDestroy();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        StringBuilder buffer = null;
        String[] args = null;
        String constraint = null;

        if (bundle != null)
            constraint = bundle.getString(BUNDLE_KEY_FILTER);

        if (constraint != null) {
            buffer = new StringBuilder();
            buffer.append("UPPER(");
            buffer.append(Sholi.Item.KEY_NAME);
            buffer.append(") GLOB ?");
            args = new String[] { "*" + constraint.toUpperCase() + "*" };
        }

        return new CursorLoader(getActivity(), Sholi.Item.CONTENT_URI,
                PROJECTION, buffer == null ? null : buffer.toString(), args, ORDER);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        super.onLoadFinished(cursorLoader, cursor);

        Editable editable = _newItemEdit.getEditableText();
        int visibility = _newItemButton.getVisibility();
        boolean doShow = cursor.getCount() == 0 && editable.length() > 0;

        // Only call setVisibility when necessary.
        if (visibility == View.GONE && doShow)
            _newItemButton.setVisibility(View.VISIBLE);
        else if (visibility == View.VISIBLE && !doShow)
            _newItemButton.setVisibility(View.GONE);
    }

    @Override
    protected void updateItem(Cursor c, Uri uri) {
        ContentValues values = new ContentValues();
        c.moveToFirst();
        switch (c.getInt(c.getColumnIndex(Sholi.Item.KEY_STATUS))) {
            case Sholi.Item.OFF_LIST:
                values.put(Sholi.Item.KEY_STATUS, Sholi.Item.UNCHECKED);
                break;
            case Sholi.Item.UNCHECKED:
            case Sholi.Item.CHECKED:
                values.put(Sholi.Item.KEY_STATUS, Sholi.Item.OFF_LIST);
                break;
        }

        if (values != null) {
            getContent().update(uri, values, null, null);
            getAdapter().notifyDataSetChanged();
        }
    }

    private Uri addItem(String name) {
        Uri uri;
        ContentValues values = new ContentValues();

        values.put(Sholi.Item.KEY_NAME, name);
        values.put(Sholi.Item.KEY_STATUS, Sholi.Item.UNCHECKED);
        uri = getContent().insert(Sholi.Item.CONTENT_URI, values);
        return uri;
    }

    private class SelectionModeHandler implements ListView.MultiChoiceModeListener {

        @Override
        public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
            int count = getListView().getCheckedItemCount();
            switch (count) {
                case 0:
                    mode.setSubtitle(null);
                    break;
                default:
                    mode.setSubtitle(getResources().getQuantityString(
                            R.plurals.selectedItems, count, count));
                    break;
            }
        }

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            MenuInflater inflater = getActivity().getMenuInflater();
            inflater.inflate(R.menu.list_select, menu);
            mode.setTitle(R.string.fragment_edit_selection_mode_title);
            if (_editMode)
                _newItemEdit.setVisibility(View.GONE);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return true;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
            case R.id.action_erase:
                long[] ids = getListView().getCheckedItemIds();
                int count = getListView().getCheckedItemCount();
                StringBuilder buffer = new StringBuilder();
                buffer.append("_ID IN (");

                for (int i = 0; i < count; ++i) {
                    buffer.append(ids[i]);
                    if (i < count - 1)
                        buffer.append(",");
                }
                buffer.append(")");
                getContent().delete(Sholi.Item.CONTENT_URI, buffer.toString(), null);
                mode.finish();
                break;
            }
            return true;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            if (_editMode)
                _newItemEdit.setVisibility(View.VISIBLE);
        }
    }
}
