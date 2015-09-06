package com.lyk.immersivenote.database;

/**
 * Created by John on 2015/8/17.
 */

import android.database.sqlite.SQLiteDatabase;

public class MainTable {
    // Database table, the columns are created in the same order
    public static final String TABLE_MAIN = "main";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_TITLE = "Title";
    public static final String COLUMN_TIME = "Time";
    public static final String COLUMN_BACKGROUND = "Background";
    public static final String COLUMN_ENCRYPTED = "Encrypted";

    // Database creation SQL statement
    private static final String TABLE_COLUMNS = " (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
            + COLUMN_TITLE + " TEXT, " + COLUMN_TIME
            + " TEXT, " + COLUMN_BACKGROUND + " TEXT, "+COLUMN_ENCRYPTED+ " TEXT " + " )";

    public static void create(SQLiteDatabase database) {
        DataAccessWrapper.create(database, TABLE_MAIN, TABLE_COLUMNS);
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion,
                                 int newVersion) {
        DataAccessWrapper.upgrade(database, TABLE_MAIN, TABLE_COLUMNS, oldVersion,
                newVersion);
    }

    public static void removeTable(SQLiteDatabase db) {
        DataAccessWrapper.removeTable(db, TABLE_MAIN);
    }

    public static void clearTable(SQLiteDatabase db) {
        DataAccessWrapper.clearTable(db, TABLE_MAIN);
    }
}