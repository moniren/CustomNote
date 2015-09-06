package com.lyk.immersivenote.database;

/**
 * Created by John on 2015/8/17.
 */
import android.database.sqlite.SQLiteDatabase;

public class NoteTable {
    // Database table, the columns are created in the same order
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_PAGE_NO = "PageNumber";
    public static final String COLUMN_LINE_NO = "LineNumber";
    public static final String COLUMN_BITMAP = "Bitmap";
    public static final String COLUMN_TYPE = "Type";

    // Database creation SQL statement
    private static final String TABLE_COLUMNS = " (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
            + COLUMN_PAGE_NO + " INTEGER, " + COLUMN_LINE_NO
            + " INTEGER, " + COLUMN_BITMAP + " TEXT, " +COLUMN_TYPE+" TEXT "+ " )";

    public static void create(SQLiteDatabase database,String tableName) {
        DataAccessWrapper.create(database, tableName, TABLE_COLUMNS);
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion,
                                 int newVersion,String tableName) {
        DataAccessWrapper.upgrade(database, tableName, TABLE_COLUMNS, oldVersion,
                newVersion);
    }

    public static void removeTable(SQLiteDatabase db,String tableName) {
        DataAccessWrapper.removeTable(db, tableName);
    }

    public static void clearTable(SQLiteDatabase db,String tableName) {
        DataAccessWrapper.clearTable(db, tableName);
    }
}