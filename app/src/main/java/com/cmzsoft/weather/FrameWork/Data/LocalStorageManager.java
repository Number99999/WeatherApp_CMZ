package com.cmzsoft.weather.FrameWork.Data;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;


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

    public static void saveJsonObjectToFile(Context context, String fileName, JSONObject jsonObject) {
        try (FileOutputStream fos = context.openFileOutput(fileName, Context.MODE_PRIVATE)) {
            fos.write(jsonObject.toString().getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static JSONObject readJsonObjectFromFile(Context context, String fileName) {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(context.openFileInput(fileName), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            return new JSONObject(sb.toString());
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}
