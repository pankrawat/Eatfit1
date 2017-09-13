package com.eatfit.helper;

import android.content.Context;
import android.content.SharedPreferences;

import com.eatfit.constants.Constants;


/**
 * Created by Abhinav Suman on 6/17/2016.
 */
public class SharedPreference {
    private static SharedPreference pref;
    public Context context;
    public SharedPreferences.Editor editor;
    private SharedPreferences sharedPreference;

    private SharedPreference(Context ctx) {
        this.context = ctx;
        sharedPreference = ctx.getSharedPreferences(Constants.PreferenceName, Context.MODE_PRIVATE);
        editor = sharedPreference.edit();
    }

    public static SharedPreference getInstance(Context ctx) {
        if (pref == null) {
            pref = new SharedPreference(ctx);
        }
        return pref;
    }

    public void putString(String key, String value) {
        editor.putString(key, value);
        editor.commit();
    }

    public void putBoolean(String key, boolean value) {
        editor.putBoolean(key, value);
        editor.commit();
    }

    public void putInteger(String key, int value) {
        editor.putInt(key, value);
        editor.commit();
    }

    public void putFloat(String key, Float value) {
        editor.putFloat(key, value);
        editor.commit();
    }

    public String getString(String key, String defValue) {
        return sharedPreference.getString(key, defValue);
    }

    public boolean getBoolean(String key, boolean defValue) {
        return sharedPreference.getBoolean(key, defValue);
    }

    public int getInteger(String key, int defValue) {
        return sharedPreference.getInt(key, defValue);
    }

    public Float getFloat(String key, Float defValue) {
        return sharedPreference.getFloat(key, defValue);
    }

    public void deletePreference() {
        editor.clear();
        editor.commit();
    }

    public void deletePreference(String key) {
        editor.remove(key);
        editor.commit();
    }
}
