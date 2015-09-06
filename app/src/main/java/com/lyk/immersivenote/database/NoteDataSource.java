package com.lyk.immersivenote.database;

/**
 * Created by John on 2015/8/17.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;

import com.lyk.immersivenote.datamodel.SignatureViewModel;
import com.lyk.immersivenote.notepad.CursorHolder;
import com.lyk.immersivenote.notepad.SignatureView;
import com.lyk.immersivenote.utils.Base64Uti;

import java.util.ArrayList;

public class NoteDataSource {
    private static final String LOGTAG = "NOTE_DATA_SOURCE";
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

    public static synchronized void createNoteTable(String tableName) {
        NoteTable.create(NoteDataSource.database,tableName);
    }

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
            }
        }

        return idList;
    }


    public static synchronized ArrayList<SignatureViewModel> getSignaturesForPage(String tableName, int pageNumber,Context context, CursorHolder cursorHolder, int lineHeight){
        String where = "\"" + NoteTable.COLUMN_PAGE_NO + "\" = " + pageNumber;
        ArrayList<SignatureViewModel> signatureList = new ArrayList<>();
        Cursor cursor = DataAccessWrapper.queryDB(database,
                tableName, allColumns, where, null, null, null,
                null);
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    SignatureViewModel tempSig = null;
                    int type = cursor.getInt(cursor.getColumnIndex(NoteTable.COLUMN_TYPE));
                    int lineNumber = cursor.getInt(cursor.getColumnIndex(NoteTable.COLUMN_LINE_NO));
                    Bitmap bitmap = null;
                    if(type==SignatureView.SPACE){
                        tempSig = new SignatureViewModel(context,type,lineNumber,pageNumber,cursorHolder,lineHeight);
                    }
                    else if (type == SignatureView.IMAGE){
                        bitmap = Base64Uti.decodeBase64(cursor.getString(cursor.getColumnIndex(NoteTable.COLUMN_BITMAP)));
                        tempSig = new SignatureViewModel(context,type,lineNumber,pageNumber,cursorHolder,bitmap);
                    }
                    signatureList.add(tempSig);
                    cursor.moveToNext();
                }
            }
        }
        return signatureList;
    }

    public static synchronized int getMaxPageNumber(String tableName){
        Cursor cursor = DataAccessWrapper.queryDB(database, tableName, new String[]{"MAX(" + NoteTable.COLUMN_PAGE_NO + ")"}, null, null, null, null, null);
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                //must use a constant 0 here
                return cursor.getInt(0);
            } else {
                return -1;
            }
        } else {
            return -1;
        }
    }

    public static synchronized int getTableSize(String tableName) {
        int size = 0;
        Cursor cursor = DataAccessWrapper.queryDB(database,
                tableName, allColumns, null, null, null, null,
                null);
        if (cursor != null) {
            size = cursor.getCount();
        }

        return size;
    }


    public static synchronized void insertNoteTable(Context ctx,
                                                    ContentValues values, String tableName) {
        DataAccessWrapper.insert(database, ctx, tableName,
                NoteTable.COLUMN_ID, values);
    }

    public static synchronized void removeNoteTable(String tableName) {
        DataAccessWrapper.removeTable(database, tableName);
    }

    public static void setDatabase(SQLiteDatabase database){
        NoteDataSource.database = database;
    }

}
