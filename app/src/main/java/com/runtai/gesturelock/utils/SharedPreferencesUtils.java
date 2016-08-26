package com.runtai.gesturelock.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SharedPreferencesUtils {
	private SharedPreferences sp;
	private static String shareName = "config";

	public SharedPreferencesUtils(Context context) {
		this(context, shareName);
	}

	private SharedPreferencesUtils(Context context, String shareName) {
		sp = context.getSharedPreferences(shareName, Context.MODE_PRIVATE);
	}
	
	public String get(String key) {
		return sp.getString(key, "");
	}
	
	public void put(String key, String value) {
		Editor edit = sp.edit();
		if (edit != null) {
			edit.putString(key, value);
			edit.commit();
		}
	}
	
	public boolean get(String key, boolean defValue) {
		return sp.getBoolean(key, defValue);
	}
	
	public void put(String key, boolean value) {
		Editor edit = sp.edit();
		if (edit != null) {
			edit.putBoolean(key, value);
			edit.commit();
		}
	}
}
