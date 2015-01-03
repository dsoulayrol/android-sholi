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
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

import de.greenrobot.dao.query.LazyList;
import de.greenrobot.dao.query.QueryBuilder;
import name.soulayrol.rhaa.sholi.data.Action;
import name.soulayrol.rhaa.sholi.data.model.Checkable;
import name.soulayrol.rhaa.sholi.data.model.Item;
import name.soulayrol.rhaa.sholi.data.model.ItemDao;
import name.soulayrol.rhaa.widget.InterceptorFrameLayout;


public class CheckingFragment extends AbstractListFragment implements
        InterceptorFrameLayout.Listener, SharedPreferences.OnSharedPreferenceChangeListener {

    private ListView _listView;

    private InterceptorFrameLayout _interceptor;

    private Map<Integer, String> _defaultActionClassNames;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_checking, container, false);
        registerForContextMenu(view);

        // It is too early to call getListView here, so we fetch the view from its ID.
        _listView = (ListView) view.findViewById(android.R.id.list);
        _interceptor = (InterceptorFrameLayout) view;

        // Preload default actions, since getResources cannot be called when the fragment
        // is detached, from onSharedPreferenceChanged. (See #13)
        _defaultActionClassNames = new HashMap<Integer, String>();
        _defaultActionClassNames.put(R.string.settings_checking_fling_to_left_default_value,
                        getResources().getString(
                                R.string.settings_checking_fling_to_left_default_value));
        _defaultActionClassNames.put(R.string.settings_checking_fling_to_right_default_value,
                        getResources().getString(
                                R.string.settings_checking_fling_to_right_default_value));

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        sharedPref.registerOnSharedPreferenceChangeListener(this);

        configureGesture(InterceptorFrameLayout.Gesture.FLING_TO_LEFT, sharedPref);
        configureGesture(InterceptorFrameLayout.Gesture.FLING_TO_RIGHT, sharedPref);
        configureGesture(InterceptorFrameLayout.Gesture.SINGLE_TAP, (String) null);

        _interceptor.startInterception(this);

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
                execute(new Action.CheckAll());
                return true;
            case R.id.action_uncheck_all:
                execute(new Action.UncheckAll());
                return true;
            case R.id.action_remove_checked:
                execute(new Action.RemoveChecked());
                return true;
            case R.id.action_empty:
                execute(new Action.Empty());
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

    @Override
    public void execute(Action action) {
        if (action == null)
            getActivity().openContextMenu(_listView);
        else
            action.proceed(this);
    }

    public void onActionDone(Action action) {
        if (action.isSuccessful()) {
            String description = action.getDescription();
            getAdapter().setLazyList(createList(getActivity()));
            if (description != null)
                Toast.makeText(getActivity(), description, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(SettingsActivity.KEY_CHECKING_FLING_LEFT_ACTION)) {
            configureGesture(InterceptorFrameLayout.Gesture.FLING_TO_LEFT, sharedPreferences);
        } else if (key.equals(SettingsActivity.KEY_CHECKING_FLING_RIGHT_ACTION)) {
            configureGesture(InterceptorFrameLayout.Gesture.FLING_TO_RIGHT, sharedPreferences);
        }
    }

    private void configureGesture(InterceptorFrameLayout.Gesture gesture, SharedPreferences sharedPreferences) {
        String className = null;

        switch (gesture) {
            case FLING_TO_LEFT:
                className = sharedPreferences.getString(SettingsActivity.KEY_CHECKING_FLING_LEFT_ACTION,
                        _defaultActionClassNames.get(R.string.settings_checking_fling_to_left_default_value));
                break;
            case FLING_TO_RIGHT:
                className = sharedPreferences.getString(SettingsActivity.KEY_CHECKING_FLING_RIGHT_ACTION,
                        _defaultActionClassNames.get(R.string.settings_checking_fling_to_right_default_value));
                break;
        }
        configureGesture(gesture, className);
    }

    private void configureGesture(InterceptorFrameLayout.Gesture gesture, String className) {
        try {
            if (className == null)
                _interceptor.configure(gesture, null);
            else
                _interceptor.configure(gesture, (Action) Class.forName(className).newInstance());
        } catch (Throwable t) {
            _interceptor.ignore(gesture);
        }
    }
}
