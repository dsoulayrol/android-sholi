/*
 * ShoLi, a simple tool to produce short (shopping) lists.
 *
 * Copyright (C) 2014,2015  David Soulayrol
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

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.widget.Toast;

import java.util.HashSet;
import java.util.Set;


public class SettingsFragment extends PreferenceFragment
        implements SharedPreferences.OnSharedPreferenceChangeListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preferences);
        PreferenceManager.setDefaultValues(getActivity(), R.xml.preferences, false);

        Preference.OnPreferenceChangeListener c = new Preference.OnPreferenceChangeListener() {
            public boolean onPreferenceChange(Preference preference, Object value) {
                return checkImportMarkers(preference.getKey(), (String)value);
            }
        };

        findPreference(SettingsActivity.KEY_IMPORT_SYMBOL_CHECKED).setOnPreferenceChangeListener(c);
        findPreference(SettingsActivity.KEY_IMPORT_SYMBOL_UNCHECKED).setOnPreferenceChangeListener(c);
        findPreference(SettingsActivity.KEY_IMPORT_SYMBOL_OFF_LIST).setOnPreferenceChangeListener(c);
    }

    @Override
    public void onResume() {
        super.onResume();

        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(getActivity());

        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);

        updatePreferenceSummary(sharedPreferences, SettingsActivity.KEY_CHECKING_FLING_LEFT_ACTION);
        updatePreferenceSummary(sharedPreferences, SettingsActivity.KEY_CHECKING_FLING_RIGHT_ACTION);
        updatePreferenceSummary(sharedPreferences, SettingsActivity.KEY_LIST_ITEM_SIZE);
        updatePreferenceSummary(sharedPreferences, SettingsActivity.KEY_IMPORT_SYMBOL_CHECKED);
        updatePreferenceSummary(sharedPreferences, SettingsActivity.KEY_IMPORT_SYMBOL_UNCHECKED);
        updatePreferenceSummary(sharedPreferences, SettingsActivity.KEY_IMPORT_SYMBOL_OFF_LIST);
        updatePreferenceSummary(sharedPreferences, SettingsActivity.KEY_IMPORT_MERGE_POLICY);
    }

    @Override
    public void onPause() {
        super.onPause();

        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        updatePreferenceSummary(sharedPreferences, key);
    }

    private boolean checkImportMarkers(String key, String value) {
        boolean result = false;

        if (!value.trim().isEmpty()) {
            // Check all markers are different.
            SharedPreferences sharedPreferences =
                    PreferenceManager.getDefaultSharedPreferences(getActivity());
            Set<String> set = new HashSet<String>();

            set.add(value);
            if (!key.equals(SettingsActivity.KEY_IMPORT_SYMBOL_CHECKED))
                set.add(sharedPreferences.getString(SettingsActivity.KEY_IMPORT_SYMBOL_CHECKED, ""));
            if (!key.equals(SettingsActivity.KEY_IMPORT_SYMBOL_UNCHECKED))
                set.add(sharedPreferences.getString(SettingsActivity.KEY_IMPORT_SYMBOL_UNCHECKED, ""));
            if (!key.equals(SettingsActivity.KEY_IMPORT_SYMBOL_OFF_LIST))
                set.add(sharedPreferences.getString(SettingsActivity.KEY_IMPORT_SYMBOL_OFF_LIST, ""));

            result = (set.size() == 3);
        }

        if (!result)
            Toast.makeText(getActivity(),
                    getResources().getString(R.string.fragment_settings_marker_error),
                    Toast.LENGTH_SHORT).show();

        return result;
    }

    private void updatePreferenceSummary(SharedPreferences sharedPreferences, String key) {
        if (SettingsActivity.isListPreference(key)) {
            ListPreference p = (ListPreference) findPreference(key);
            p.setSummary(p.getEntry());
        } else if (SettingsActivity.KEY_IMPORT_SYMBOL_CHECKED.equals(key)
                || SettingsActivity.KEY_IMPORT_SYMBOL_UNCHECKED.equals(key)
                || SettingsActivity.KEY_IMPORT_SYMBOL_OFF_LIST.equals(key)) {
            Preference p = findPreference(key);
            p.setSummary(sharedPreferences.getString(key, ""));
        }
    }
}
