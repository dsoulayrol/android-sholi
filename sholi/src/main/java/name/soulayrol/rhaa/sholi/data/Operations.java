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
package name.soulayrol.rhaa.sholi.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import de.greenrobot.dao.query.LazyList;
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

    public static void serialize(LazyList<Item> items, StringBuilder builder) {
        for (Item item: items) {
            switch (item.getStatus()) {
                case Checkable.CHECKED:
                    builder.append('+');
                    break;
                case Checkable.UNCHECKED:
                    builder.append('-');
                    break;
                default:
                    builder.append('*');
                    break;
            }
            builder.append(item.getName()).append('\n');
        }
    }

    public static List<Item> deserialize(String data) {
        List<Item> items = new ArrayList<Item>();
        Scanner scanner = new Scanner(data);

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            if (line.length() <= MAX_SERIALIZED_LENGTH) {
                if (line.startsWith("+"))
                    items.add(new Item(null, line.substring(1).trim(), Checkable.CHECKED));
                else if (line.startsWith("-"))
                    items.add(new Item(null, line.substring(1).trim(), Checkable.UNCHECKED));
                else if (line.startsWith("*"))
                    items.add(new Item(null, line.substring(1).trim(), Checkable.OFF_LIST));
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

}
