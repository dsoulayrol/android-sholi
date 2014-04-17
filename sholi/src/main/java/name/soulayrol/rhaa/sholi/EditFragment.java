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

import android.app.ActionBar;
import android.content.Context;
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

import de.greenrobot.dao.query.LazyList;
import de.greenrobot.dao.query.QueryBuilder;
import name.soulayrol.rhaa.sholi.data.model.Checkable;
import name.soulayrol.rhaa.sholi.data.model.Item;
import name.soulayrol.rhaa.sholi.data.model.ItemDao;


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
                getAdapter().setLazyList(createList(getActivity()));
            }
        });

        _newItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (addItem(_newItemEdit.getText().toString().trim()) != 0)
                    _newItemEdit.setText("");
                getAdapter().setLazyList(createList(getActivity()));
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
    protected LazyList<Item> createList(Context context) {
        QueryBuilder builder = getSession().getItemDao().queryBuilder();
        Editable editable = null;
        LazyList<Item> list = null;

        if (_newItemEdit != null) {
            editable = _newItemEdit.getEditableText();
            String constraint = editable.toString().trim();
            if (constraint != null && !constraint.isEmpty()) {
                builder.where(ItemDao.Properties.Name.like(constraint));
            }
        }

        list = builder.orderAsc(ItemDao.Properties.Name).listLazy();

        if (editable != null && _newItemButton != null) {
            int visibility = _newItemButton.getVisibility();
            boolean doShow = list.size() == 0 && editable.length() > 0;
            // Only call setVisibility when necessary.
            if (visibility == View.GONE && doShow)
                _newItemButton.setVisibility(View.VISIBLE);
            else if (visibility == View.VISIBLE && !doShow)
                _newItemButton.setVisibility(View.GONE);
        }

        return list;
    }

    @Override
    protected void updateItem(Item item) {
        switch (item.getStatus()) {
            case Checkable.OFF_LIST:
                item.setStatus(Checkable.UNCHECKED);
                break;
            case Checkable.UNCHECKED:
            case Checkable.CHECKED:
                item.setStatus(Checkable.OFF_LIST);
                break;
        }

        getSession().getItemDao().update(item);
        getAdapter().notifyDataSetChanged();
    }

    private long addItem(String name) {
        return getSession().getItemDao().insert(new Item(null, name, Checkable.UNCHECKED));
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
                getSession().runInTx(new Runnable() {
                    @Override
                    public void run() {
                        for (long id : getListView().getCheckedItemIds())
                            getSession().getItemDao().deleteByKey(id);
                    }
                });
                getAdapter().setLazyList(createList(getActivity()));
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
