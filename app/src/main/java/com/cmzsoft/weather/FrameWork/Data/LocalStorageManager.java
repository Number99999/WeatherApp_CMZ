package com.cmzsoft.weather.FrameWork.Data;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;


public class LocalStorageManager {
    private static final String PREF_NAME = "com.cmz.weather";
    private static SharedPreferences _prefs;

    public static void init(Context appContext) {
        _prefs = appContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static void putString(String key, String value) {
        _prefs.edit().putString(key, value).apply();
    }

    public static String getString(String key) {
        return _prefs.getString(key, null);
    }

    public static void putObject(String key, Object object) {
        String json = new Gson().toJson(object);
        _prefs.edit().putString(key, json).apply();
    }

    public static <T> T getObject(String key, Class<T> clazz) throws IllegalAccessException, InstantiationException {
        String json = _prefs.getString(key, null);
        return (json == null) ? clazz.newInstance() : new Gson().fromJson(json, clazz);
    }

    public static void remove(String key) {
        _prefs.edit().remove(key).apply();
    }

    public static void clearAll(Context context) {
        _prefs.edit().clear().apply();
    }
}
