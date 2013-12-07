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
package name.soulayrol.rhaa.sholi.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;


public class Provider extends ContentProvider {

    private static final String TAG = Provider.class.getName();

    private static final int TYPE_ITEMS = 0;
    private static final int TYPE_ITEM_ID = 1;

    private static final UriMatcher __uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        __uriMatcher.addURI(Sholi.AUTHORITY, Sholi.Item.TABLE, TYPE_ITEMS);
        __uriMatcher.addURI(Sholi.AUTHORITY, Sholi.Item.TABLE + "/#", TYPE_ITEM_ID);
    }

    private SQLiteDatabase _db;

    @Override
    public boolean onCreate() {
        DbHelper helper = new DbHelper(getContext());
        _db = helper.getWritableDatabase();
        return (_db != null);
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(Sholi.Item.TABLE);

        switch (__uriMatcher.match(uri)) {
            case TYPE_ITEMS:
                break;
            case TYPE_ITEM_ID:
                qb.appendWhere(Sholi.Item._ID + "=" + uri.getPathSegments().get(1));
                break;
            default:
                throw new IllegalArgumentException("unsupported URI " + uri);
        }

        Cursor c = qb.query(_db, projection, selection, selectionArgs, null, null, sortOrder);
        c.setNotificationUri(getContext().getContentResolver(), uri);

        return c;
    }

    @Override
    public String getType(Uri uri) {
        switch (__uriMatcher.match(uri)) {
            case TYPE_ITEMS:
                return "vnd.android.cursor.dir/vnd.name.soulayrol.rhaa.sholi.item";
            case TYPE_ITEM_ID:
                return "vnd.android.cursor.item/vnd.name.soulayrol.rhaa.bosco.item";
            default:
                throw new IllegalArgumentException("unsupported URI " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        Uri newUri = null;

        try {
            long id = _db.insertOrThrow(Sholi.Item.TABLE, null, values);
            if (id > 0) {
                newUri = uri.buildUpon().appendPath(String.valueOf(id)).build();
                getContext().getContentResolver().notifyChange(newUri, null);
            }
        } catch (SQLiteConstraintException e) {
            Log.w(Sholi.TAG, "Item '" + values.getAsString(Sholi.Item.KEY_NAME)
                    + "' already exists in database");
        }
        return newUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        String segment;
        int count;

        switch (__uriMatcher.match(uri)) {
            case TYPE_ITEMS:
                count = _db.delete(Sholi.Item.TABLE, selection, selectionArgs);
                break;
            case TYPE_ITEM_ID:
                segment = uri.getPathSegments().get(1);
                count = _db.delete(Sholi.Item.TABLE, Sholi.Item._ID + "=" + segment
                        + (!TextUtils.isEmpty(selection) ? " and ("
                        + selection + ")" : ""), selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("unsupported URI " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        String segment;
        int count;

        switch (__uriMatcher.match(uri)) {
            case TYPE_ITEMS:
                count = _db.update(Sholi.Item.TABLE, values, selection, selectionArgs);
                break;
            case TYPE_ITEM_ID:
                segment = uri.getPathSegments().get(1);
                count = _db.update(Sholi.Item.TABLE, values, Sholi.Item._ID + "=" + segment
                        + (!TextUtils.isEmpty(selection) ? " and ("
                        + selection + ")" : ""), selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("unsupported URI " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    private class DbHelper extends SQLiteOpenHelper {

        private static final String DB_FILE_NAME = "sholi.db";
        private static final int DB_FILE_VERSION = 1;

        public DbHelper(Context context) {
            super(context, DB_FILE_NAME, null, DB_FILE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("create table " + Sholi.Item.TABLE + "("
                    + Sholi.Item._ID + " integer primary key autoincrement, "
                    + Sholi.Item.KEY_NAME + " text unique not null,"
                    + Sholi.Item.KEY_STATUS + " integer)");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        }
    }
}
