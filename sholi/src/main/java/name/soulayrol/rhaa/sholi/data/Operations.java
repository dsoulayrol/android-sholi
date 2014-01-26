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

import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class Operations {

    // TODO: This is arbitrary and should match the maximum database item's name field
    private static final int MAX_SERIALIZED_LENGTH = 256;

    public static class TransientItem {
        public TransientItem(String name, int status) {
            this.name = name;
            this.status = status;
        }

        public String name;
        public int status;
    }

    public static void serialize(Cursor c, StringBuilder builder) {
        c.moveToPosition(-1);
        int idx = c.getColumnIndex(Sholi.Item.KEY_NAME);
        while (c.moveToNext()) {
            switch (c.getInt(c.getColumnIndex(Sholi.Item.KEY_STATUS))) {
                case Sholi.Item.CHECKED:
                    builder.append('+');
                    break;
                case Sholi.Item.UNCHECKED:
                    builder.append('-');
                    break;
                default:
                    builder.append('*');
                    break;
            }
            builder.append(c.getString(idx)).append('\n');
        }
    }

    public static List<TransientItem> deserialize(String data) {
        List<TransientItem> items = new ArrayList<TransientItem>();
        Scanner scanner = new Scanner(data);

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            if (line.length() <= MAX_SERIALIZED_LENGTH) {
                if (line.startsWith("+"))
                    items.add(new TransientItem(line.substring(1).trim(), Sholi.Item.CHECKED));
                else if (line.startsWith("-"))
                    items.add(new TransientItem(line.substring(1).trim(), Sholi.Item.UNCHECKED));
                else if (line.startsWith("*"))
                    items.add(new TransientItem(line.substring(1).trim(), Sholi.Item.OFF_LIST));
            }
        }

        return items;
    }
}
