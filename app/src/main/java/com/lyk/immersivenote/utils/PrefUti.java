package com.lyk.immersivenote.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.preference.PreferenceManager;

import java.util.Locale;

public abstract class PrefUti {
	//keys
	public static final String NOTE_PAGE_WIDTH = "NotePageWidth";
	public static final String NOTE_PAGE_HEIGHT = "NotePageHeight";
	public static final String NOTE_LINE_HEIGHT = "NoteLineHeight";
	public static final String SCREEN_HEIGHT = "ScreenHeight";

	public static final String CUSTOM_LOCALE = "CustomLocale";
	
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

	public static void updateLocale(String abbrev,Activity activity){
		Locale locale = new Locale(abbrev);
		if(abbrev.equals("zh_CN")){
			locale = Locale.SIMPLIFIED_CHINESE;
		}
		else if (abbrev.equals("zh_TW")){
			locale = Locale.TRADITIONAL_CHINESE;
		}
		Locale.setDefault(locale);
		Configuration config = new Configuration();
		config.locale = locale;
		activity.getApplicationContext().getResources().updateConfiguration(config, null);
	}
}
