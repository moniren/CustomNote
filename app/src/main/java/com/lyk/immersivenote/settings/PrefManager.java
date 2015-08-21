package com.lyk.immersivenote.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public abstract class PrefManager {
	//keys
	public static final String NOTE_PAGE_WIDTH = "NotePageWidth";
	public static final String NOTE_PAGE_HEIGHT = "NotePageHeight";
	public static final String NOTE_LINE_HEIGHT = "NoteLineHeight";
	public static final String SCREEN_HEIGHT = "ScreenHeight";
	
	public static void setStringPreference(String key,String info, Context context){
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString(key, info);
		editor.commit();
	}
	
	public static String getStringPreference(String key,Context context){
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
		return settings.getString(key, null);
	}
	
	
	public static void setIntPreference(String key,int info, Context context){
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
		SharedPreferences.Editor editor = settings.edit();
		editor.putInt(key, info);
		editor.commit();
	}
	
	public static int getIntPreference(String key,Context context){
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
		return settings.getInt(key, -1);
	}
}
