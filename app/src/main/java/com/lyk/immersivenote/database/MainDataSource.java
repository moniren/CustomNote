package com.lyk.immersivenote.database;

/**
 * Created by John on 2015/8/17.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class MainDataSource {
    private static final String LOGTAG = "MAIN_DATA_SOURCE";
    // need to use "" to surround the column name otherwise it will be treated
    // as a string, use DESC so the newest will be on top
    private static final String ORDER_BY_TIME = "datetime(\""
            + MainTable.COLUMN_TIME + "\") DESC";
    // the SQL "where" condition to filter the not acknowledged alerts, use ''
    // to surround the string
//    private static final String null = "\""
//            + MainTable.COLUMN_ACKNOWLEDGED + "\" = 'false'";
    public static SQLiteDatabase database;

    private MainDataSource() {
    }

    private static final String[] allColumns = {MainTable.COLUMN_ID,
            MainTable.COLUMN_TITLE, MainTable.COLUMN_TIME,
            MainTable.COLUMN_BACKGROUND};

    public static synchronized void createMainTable() {
        MainTable.create(MainDataSource.database);
    }

    public static synchronized  Cursor getWholeCursor(){
        return DataAccessWrapper.queryDB(database,
                MainTable.TABLE_MAIN, allColumns, null, null, null,
                null, ORDER_BY_TIME);
    }

    public static synchronized int getTableSize() {
        int size = 0;
        Cursor cursor = DataAccessWrapper.queryDB(database,
                MainTable.TABLE_MAIN, allColumns, null, null, null, null,
                ORDER_BY_TIME);
        if (cursor != null) {
            size = cursor.getCount();
        }

        return size;
    }

    public static synchronized  String getCircleBg(Context ctx, long rowId){
        String color = null;
        Cursor cursor = DataAccessWrapper.queryDB(database,
                MainTable.TABLE_MAIN, allColumns, null, null, null, null,
                ORDER_BY_TIME);
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                color = cursor.getString(cursor.getColumnIndex(MainTable.COLUMN_BACKGROUND));
            }
        }
        return color;
    }

    public static synchronized void updateNote(Context ctx, long rowId,
                                                   ContentValues values) {
        DataAccessWrapper.update(database, ctx, MainTable.TABLE_MAIN, rowId,
                values);
    }

    public static synchronized long insertNote(Context ctx,
                                                ContentValues values) {
        return DataAccessWrapper.insert(database, ctx, MainTable.TABLE_MAIN,
                MainTable.COLUMN_ID, values);
    }

    public static synchronized void removeNote(Context ctx, long rowId) {
        if (ctx != null && rowId != -1) {
            DataAccessWrapper.remove(MainDataSource.database, ctx,
                    MainTable.TABLE_MAIN, rowId);
        } else {
            throw new IllegalArgumentException(
                    "Context is null or rowId is incorrect");
        }
    }

    public static void setDatabase(SQLiteDatabase database){
        MainDataSource.database = database;
    }

}
