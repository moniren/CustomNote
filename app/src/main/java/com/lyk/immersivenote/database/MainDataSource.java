package com.lyk.immersivenote.database;

/**
 * Created by John on 2015/8/17.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;

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

    public static synchronized void createMainTable(SQLiteDatabase database) {
        MainDataSource.database = database;
        MainTable.create(MainDataSource.database);
        Log.i(LOGTAG, "MainTable has been created");
    }

    public static synchronized  Cursor getWholeCursor(){
        return DataAccessWrapper.queryDB(database,
                MainTable.TABLE_MAIN, allColumns, null, null, null,
                null, ORDER_BY_TIME);
    }

    /*
     * The following getWhole... methods will return an ArrayList containing the
     * item of the column as indicated by the name of the method 
     * Notes: 
     * 1. The list contains only the data for the alert items that are not acknowledged
     * except for the getWholeAcknowledged() 
     * 2. For the lists that contain several column items, the items are put together into one string and use
     * a "!" to separate them
     */
    public static synchronized ArrayList<String> getWholeIDList() {
        ArrayList<String> idList = new ArrayList<String>();
        Cursor cursor = DataAccessWrapper.queryDB(database,
                MainTable.TABLE_MAIN, allColumns, null, null, null,
                null, ORDER_BY_TIME);
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    idList.add(cursor.getString(0));
                    cursor.moveToNext();
                }
            } else
                Log.i(LOGTAG, "Cursor is empty!");
        } else
            Log.i(LOGTAG, "Cursor is null!");

        return idList;
    }

    public static synchronized ArrayList<String> getWholeDataCList() {
        ArrayList<String> alertDataCList = new ArrayList<String>();
        Cursor cursor = DataAccessWrapper.queryDB(database,
                MainTable.TABLE_MAIN, allColumns, null, null, null,
                null, ORDER_BY_TIME);
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {

                    alertDataCList.add(cursor.getString(5));
                    cursor.moveToNext();
                }
            } else
                Log.i(LOGTAG, "Cursor is empty!");
        } else
            Log.i(LOGTAG, "Cursor is null!");

        return alertDataCList;
    }

    public static synchronized boolean checkAlertAcknowledged(Context ctx,
                                                              long rowId) {
        String where = "\"" + MainTable.COLUMN_ID + "\" = " + rowId;
        Cursor cursor = DataAccessWrapper.queryDB(database,
                MainTable.TABLE_MAIN, allColumns, where, null, null, null,
                null);
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                if (cursor.getString(7).equals("true")) {
                    return true;
                } else {
                    return false;
                }
            } else {
                Log.i(LOGTAG, "Cursor is empty!");
                return false;
            }
        } else {
            Log.i(LOGTAG, "Cursor is null!");
            return false;
        }

    }

    public static synchronized int getTableSize() {
        int size = 0;
        Cursor cursor = DataAccessWrapper.queryDB(database,
                MainTable.TABLE_MAIN, allColumns, null, null, null, null,
                ORDER_BY_TIME);
        if (cursor != null) {
            size = cursor.getCount();
        } else
            Log.i(LOGTAG, "Cursor is null!");

        return size;
    }

    public static synchronized void updateNote(Context ctx, long rowId,
                                                   ContentValues values) {
        DataAccessWrapper.update(database, ctx, MainTable.TABLE_MAIN, rowId,
                values);
    }

    public static synchronized void insertNote(Context ctx,
                                                ContentValues values) {
        DataAccessWrapper.insert(database, ctx, MainTable.TABLE_MAIN,
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

}
