/*
 * ShoLi, a simple tool to produce short (shopping) lists.
 *
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

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        if (savedInstanceState == null && findViewById(R.id.container) != null) {
            getFragmentManager().beginTransaction().add(R.id.container, new CheckingFragment()).commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_about:
                displayAboutDialog();
                return true;
            case R.id.action_data_overview:
                startActivity(new Intent(this, DataOverviewActivity.class));
                return true;
            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigateUp() {
        getFragmentManager().popBackStack();
        getActionBar().setDisplayHomeAsUpEnabled(false);
        return true;
    }

    private void displayAboutDialog() {
        View view = getLayoutInflater().inflate(R.layout.about_dlg, null, false);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        try {
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), 0);
            TextView text = (TextView) view.findViewById(R.id.version_text);
            text.setText(getString(R.string.dialog_about_version,
                    info.versionName, info.versionCode));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        builder.setIcon(android.R.drawable.ic_dialog_info)
                .setTitle(R.string.dialog_about_title)
                .setView(view)
                .setNeutralButton(android.R.string.ok, null)
                .create()
                .show();
    }
}
