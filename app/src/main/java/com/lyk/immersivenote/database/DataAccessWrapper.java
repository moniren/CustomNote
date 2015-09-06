package com.lyk.immersivenote.database;

/**
 * Created by John on 2015/8/17.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;


public class DataAccessWrapper {
    private static String TAG = "DATA_ACCESS_WRAPPER";
    // table creation SQL statement
    private static String TABLE_CREATE = "CREATE TABLE IF NOT EXISTS ";
    // table removal SQL statement
    private static String TABLE_DROP = "DROP TABLE IF EXISTS ";
    private static String TABLE_QUERY = "";

    // Create a table
    public static synchronized void create(SQLiteDatabase db, String TABLE_NAME, String TABLE_COLUMNS) {
        TABLE_QUERY = TABLE_CREATE + TABLE_NAME + TABLE_COLUMNS;
        // Authorise foreign key
        db.execSQL("PRAGMA foreign_keys=ON;");
        db.execSQL(TABLE_QUERY);
    }

    // Remove a table
    public static synchronized void removeTable(SQLiteDatabase db, String TABLE_NAME) {
        TABLE_QUERY = TABLE_DROP + TABLE_NAME;
        db.execSQL(TABLE_QUERY);
    }

    // Remove all rows in a table
    public static synchronized void clearTable(SQLiteDatabase db, String TABLE_NAME) {
        db.delete(TABLE_NAME, null, null);
    }

    // Remove the old table and create the new version of the table
    public static synchronized void upgrade(SQLiteDatabase db, String TABLE_NAME, String TABLE_COLUMNS, int oldVersion, int newVersion) {
        db.execSQL(TABLE_DROP + TABLE_NAME);
        create(db, TABLE_NAME, TABLE_COLUMNS);
    }

    // Get all values from a table
    public static synchronized Cursor getFromDB(SQLiteDatabase database, String TABLE_NAME, String[] allColumns) {
        Cursor cursor = null;
        try {
            cursor = database.query(TABLE_NAME, allColumns, null, null, null, null, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cursor;
    }

    /**
     * Query the given table, returning a Cursor over the result set.
     *
     * @param database      The Database object to execute the query on.
     * @param TABLE_NAME    The table name to compile the query against.
     * @param allColumns    A list of which columns to return. Passing null will return
     *                      all columns, which is discouraged to prevent reading data from
     *                      storage that isn't going to be used.
     * @param where         A filter declaring which rows to return, formatted as an SQL
     *                      WHERE clause (excluding the WHERE itself). Passing null will
     *                      return all rows for the given table.
     * @param selectionArgs You may include ?s in selection, which will be replaced by
     *                      the values from selectionArgs, in order that they appear in
     *                      the selection. The values will be bound as Strings.
     * @param groupBy       A filter declaring how to group rows, formatted as an SQL
     *                      GROUP BY clause (excluding the GROUP BY itself). Passing null
     *                      will cause the rows to not be grouped.
     * @param having        A filter declare which row groups to include in the cursor,
     *                      if row grouping is being used, formatted as an SQL HAVING
     *                      clause (excluding the HAVING itself). Passing null will cause
     *                      all row groups to be included, and is required when row
     *                      grouping is not being used.
     * @param orderBy       How to order the rows, formatted as an SQL ORDER BY clause
     *                      (excluding the ORDER BY itself). Passing null will use the
     *                      default sort order, which may be unordered.
     * @return
     */
    public static synchronized Cursor queryDB(SQLiteDatabase database, String TABLE_NAME, String[] allColumns, String where, String[] selectionArgs, String groupBy, String having, String orderBy) {
        Cursor cursor = null;
        try {
            cursor = database.query(TABLE_NAME, allColumns, where, selectionArgs, groupBy, having, orderBy);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cursor;
    }

    public static synchronized Cursor getColumnValue(SQLiteDatabase database, String TABLE_NAME, long rowId, String col) {
        Cursor cursor = null;
        String where = "_id = " + rowId;
        try {
            String[] column = {col};
            cursor = database.query(TABLE_NAME, column, where, null, null, null, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cursor;
    }

    // Insert a row in a table
    public static synchronized long insert(SQLiteDatabase database, Context ctx, String TABLE_NAME, String PRIMARY_KEY, ContentValues values) {
        long insertId = -1;
        try {
            insertId = database.insertOrThrow(TABLE_NAME, null, values);

            // check if the PRIMARY_KEY is null
            Object key = null;
            if (PRIMARY_KEY != null) {
                key = values.get(PRIMARY_KEY);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return insertId;
    }

    // Update a row knowing the row ID
    public static synchronized void update(SQLiteDatabase database, Context ctx, String TABLE_NAME, long rowId, ContentValues newValues) {
        String where = "_id = " + rowId;
        try {
            database.update(TABLE_NAME, newValues, where, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // TODO where is checking for _id where some tables may not have this column
    // _id is also not the row Id, it should be named appropriately
    // Remove a row knowing the row ID
    public static synchronized void remove(SQLiteDatabase database, Context ctx, String TABLE_NAME, long rowId) {
        String where = "_id = " + rowId;
        String col = "_id";
        try {
            //We check that the value is in the table before removing it.
            long numRows = DatabaseUtils.longForQuery(database, "SELECT COUNT(*) FROM " + TABLE_NAME, null);
            Cursor c = getColumnValue(database, TABLE_NAME, rowId, col);
            String[] id = {col};
            Cursor cursor = queryDB(database, TABLE_NAME, id, null, null, null, null, null);
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                cursor.moveToNext();
            }
            if (c != null) {
                c.close();
                database.delete(TABLE_NAME, where, null);
                numRows = DatabaseUtils.longForQuery(database, "SELECT COUNT(*) FROM " + TABLE_NAME, null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
