/*
 * ShoLi, a simple tool to produce short (shopping) lists.
 *
 * Copyright (C) 2013,2014,2015  David Soulayrol
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

import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import de.greenrobot.dao.query.LazyList;
import name.soulayrol.rhaa.sholi.R;
import name.soulayrol.rhaa.sholi.SettingsActivity;
import name.soulayrol.rhaa.sholi.data.model.Checkable;
import name.soulayrol.rhaa.sholi.data.model.DaoMaster;
import name.soulayrol.rhaa.sholi.data.model.DaoSession;
import name.soulayrol.rhaa.sholi.data.model.Item;


public class Operations {

    private static final String TAG = "db";

    private static final String DATABASE_NAME = "sholi.db";

    // TODO: This is arbitrary and should match the maximum database item's name field
    private static final int MAX_SERIALIZED_LENGTH = 256;

    private static SQLiteDatabase _database;

    public static DaoSession openSession(Context context) {
        if (_database == null) {
            DaoMaster.OpenHelper helper = new RegularOpenHelper(context);
            _database = helper.getWritableDatabase();
        }
        DaoMaster daoMaster = new DaoMaster(_database);
        return daoMaster.newSession();
    }

    public static void serialize(Context context, LazyList<Item> items, StringBuilder builder) {
        Map<Integer, String> map = buildMapping(context);
        for (Item item: items) {
            builder.append(map.get(item.getStatus()));
            builder.append(item.getName()).append('\n');
        }
    }

    public static List<Item> deserialize(Context context, String data) {
        Map<Integer, String> map = buildMapping(context);
        List<Item> items = new ArrayList<Item>();
        Scanner scanner = new Scanner(data);

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            if (line.length() <= MAX_SERIALIZED_LENGTH) {
                for (Map.Entry e: map.entrySet()) {
                    if (line.startsWith((String) e.getValue())) {
                        items.add(new Item(
                                null, line.substring(((String) e.getValue()).length()).trim(),
                                (Integer) e.getKey()));
                        break;
                    }
                }
            }
        }

        return items;
    }

    public static class RegularOpenHelper extends DaoMaster.OpenHelper {
        public RegularOpenHelper(Context context) {
            super(context, DATABASE_NAME, null);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            if (oldVersion == 1 && newVersion == 2)
                Log.i(TAG, "Upgraded schema to version 2. Introducing greenDAO.");
            else
                Log.w(TAG, "Unsupported upgrade from version " + oldVersion + " to " + newVersion);
        }
    }

    private static Map<Integer, String> buildMapping(Context context) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        Map<Integer, String> map = new HashMap<Integer, String>();

        map.put(Checkable.CHECKED, sharedPref.getString(
                SettingsActivity.KEY_IMPORT_SYMBOL_CHECKED,
                context.getResources().getString(R.string.setting_import_default_value)));
        map.put(Checkable.UNCHECKED, sharedPref.getString(
                SettingsActivity.KEY_IMPORT_SYMBOL_UNCHECKED,
                context.getResources().getString(R.string.setting_import_default_value)));
        map.put(Checkable.OFF_LIST, sharedPref.getString(
                SettingsActivity.KEY_IMPORT_SYMBOL_OFF_LIST,
                context.getResources().getString(R.string.setting_import_default_value)));

        return map;
    }
}
