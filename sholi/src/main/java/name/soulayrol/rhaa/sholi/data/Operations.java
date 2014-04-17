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

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import de.greenrobot.dao.query.LazyList;
import name.soulayrol.rhaa.sholi.data.model.Checkable;
import name.soulayrol.rhaa.sholi.data.model.DaoMaster;
import name.soulayrol.rhaa.sholi.data.model.DaoSession;
import name.soulayrol.rhaa.sholi.data.model.Item;


public class Operations {

    // TODO: This is arbitrary and should match the maximum database item's name field
    private static final int MAX_SERIALIZED_LENGTH = 256;

    private static SQLiteDatabase _database;

    public static DaoSession openSession(Context context) {
        if (_database == null) {
            DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(context, "sholi-db", null);
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
}
