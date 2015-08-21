package com.lyk.immersivenote.database;

/**
 * Created by John on 2015/8/17.
 */

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

public class DataAccessManager {

    public static final String TAG = "DAM_SERVICE";

    // send a notification when there's a row updated
    public static void NotifyUpdate(Context ctx, String tableName, long id, ContentValues newValues) {
        Log.i(TAG, "Broadcasting the update in table:" + tableName + " of id:" + id);

        Intent intent = new Intent();
        String action = "com.thalesgroup.android.echo.db.update." + tableName + "." + id;
        intent.setAction(action);
        LocalBroadcastManager.getInstance(ctx).sendBroadcast(intent);
    }

    // send a notification when there's a row removed
    public static void NotifyRemove(Context ctx, String tableName, long id) {
        Log.i(TAG, "Broadcasting the removal in table:" + tableName + " of id:" + id);

        Intent intent = new Intent();
        String action = "com.lyk.immersivenote.database.remove." + tableName;
        intent.setAction(action);
        LocalBroadcastManager.getInstance(ctx).sendBroadcast(intent);
    }

    public static void NotifyInsert(Context ctx, String tableName, long rowId, Object key) {
        Log.i(TAG, "Broadcasting the insertion in table:" + tableName
                + " on row: " + rowId + " with key: " + key);

        Intent intent = new Intent();
        String action = "com.lyk.immersivenote.database.insert." + tableName;
        intent.setAction(action);
        intent.putExtra("row", rowId);
        putKey(intent, key);

        LocalBroadcastManager.getInstance(ctx).sendBroadcast(intent);
    }

    private static void putKey(Intent intent, Object key) {
        if (intent != null && key != null) {
            Log.i(TAG, "putKey with key type : " + key.getClass().getName());

            // add the key according to its database type (supported are String, Boolean and Numeric as Long)
            if (String.class.isAssignableFrom(key.getClass())) {
                intent.putExtra("key", (String) key);
            } else if (Boolean.class.isAssignableFrom(key.getClass())) {
                intent.putExtra("key", (Boolean) key);
            } else if (Long.class.isAssignableFrom(key.getClass())) {
                intent.putExtra("key", (Long) key);
            }
        }
    }
}