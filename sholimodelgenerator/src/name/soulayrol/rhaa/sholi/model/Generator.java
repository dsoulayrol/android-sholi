/*
 * ShoLi, a simple tool to produce short lists.
 * Copyright (C) 2014  David Soulayrol
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
package name.soulayrol.rhaa.sholi.model;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Property;
import de.greenrobot.daogenerator.Schema;
import de.greenrobot.daogenerator.ToMany;

/**
* Generates entities and DAOs for ShoLi.
*/
public class Generator {

    private static final String GEN_PATH = "../sholi/src-gen/main/java/";

    private static final int SCHEMA_VERSION = 2;

    public static void main(String[] args) throws Exception {
        Schema schema = new Schema(SCHEMA_VERSION, "name.soulayrol.rhaa.sholi.data.model");

        addItem(schema);

        new DaoGenerator().generateAll(schema, GEN_PATH);
    }

    private static void addItem(Schema schema) {
        // Table and column names are overriden to match the ones set
        // by the Provider used in previous version.
        Entity item = schema.addEntity("Item");
        item.setTableName("items");
        item.implementsInterface("name.soulayrol.rhaa.sholi.data.model.PersistentObject");
        item.implementsInterface("name.soulayrol.rhaa.sholi.data.model.Checkable");
        item.addIdProperty().autoincrement();
        item.addStringProperty("name").columnName("item").unique().notNull();
        item.addIntProperty("status").columnName("status");
    }
}
