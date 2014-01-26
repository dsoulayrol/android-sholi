/*
 * ShoLi, a simple tool to produce short (shopping) lists.
 *
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

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;


public class DataOverviewActivity extends Activity {

    private static final String TAG_IMPORT_DIALOG = "import_dialog";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        String type = intent.getType();

        if (savedInstanceState == null && findViewById(R.id.container) != null) {
            getFragmentManager().beginTransaction().add(
                    R.id.container, new DataOverviewFragment()).commit();
        }

        if (Intent.ACTION_SEND.equals(intent.getAction()) && type != null) {
            if ("text/plain".equals(type)) {
                handleImport(intent.getStringExtra(Intent.EXTRA_TEXT));

                // Deactivate the intent so that import is only achieved once,
                // even if this activity is rebuilt (on screen rotation, or whatever).
                intent.setAction("");
            }
        }
    }

    void handleImport(String data) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        if (getFragmentManager().findFragmentByTag(TAG_IMPORT_DIALOG) == null) {
            ImportFragment.newInstance(data).show(transaction, TAG_IMPORT_DIALOG);
        }
    }
}
