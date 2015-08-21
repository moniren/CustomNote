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

public class NoteDataSource {
    private static final String LOGTAG = "MAIN_DATA_SOURCE";
    // need to use "" to surround the column name otherwise it will be treated
    // as a string, use DESC so the newest will be on top

    // the SQL "where" condition to filter the not acknowledged alerts, use ''
    // to surround the string
//    private static final String null = "\""
//            + NoteTable.COLUMN_ACKNOWLEDGED + "\" = 'false'";
    public static SQLiteDatabase database;

    private NoteDataSource() {
    }

    private static final String[] allColumns = {NoteTable.COLUMN_ID,
            NoteTable.COLUMN_PAGE_NO, NoteTable.COLUMN_LINE_NO,
            NoteTable.COLUMN_BITMAP,NoteTable.COLUMN_TYPE};

    public static synchronized void createNoteTable(SQLiteDatabase database,String tableName) {
        NoteDataSource.database = database;
        NoteTable.create(NoteDataSource.database,tableName);
        Log.i(LOGTAG, "NoteTable has been created");
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
    public static synchronized ArrayList<String> getWholeIDList(String tableName) {
        ArrayList<String> idList = new ArrayList<String>();
        Cursor cursor = DataAccessWrapper.queryDB(database,
                tableName, allColumns, null, null, null,
                null, null);
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

    public static synchronized ArrayList<String> getWholeDataCList(String tableName) {
        ArrayList<String> alertDataCList = new ArrayList<String>();
        Cursor cursor = DataAccessWrapper.queryDB(database,
                tableName, allColumns, null, null, null,
                null, null);
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
                                                              long rowId,String tableName) {
        String where = "\"" + NoteTable.COLUMN_ID + "\" = " + rowId;
        Cursor cursor = DataAccessWrapper.queryDB(database,
               tableName, allColumns, where, null, null, null,
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

    public static synchronized int getTableSize(String tableName) {
        int size = 0;
        Cursor cursor = DataAccessWrapper.queryDB(database,
                tableName, allColumns, null, null, null, null,
                null);
        if (cursor != null) {
            size = cursor.getCount();
        } else
            Log.i(LOGTAG, "Cursor is null!");

        return size;
    }

    public static synchronized void updateOldAlert(Context ctx, long rowId,
                                                   ContentValues values,String tableName) {
        DataAccessWrapper.update(database, ctx, tableName, rowId,
                values);
    }

    public static synchronized void insertAlert(Context ctx,
                                                ContentValues values,String tableName) {
        DataAccessWrapper.insert(database, ctx, tableName,
                NoteTable.COLUMN_ID, values);
    }

    public static synchronized void removeAlert(Context ctx, long rowId,String tableName) {
        if (ctx != null && rowId != -1) {
            DataAccessWrapper.remove(NoteDataSource.database, ctx,
                    tableName, rowId);
        } else {
            throw new IllegalArgumentException(
                    "Context is null or rowId is incorrect");
        }
    }

}
