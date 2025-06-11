package com.cmzsoft.weather.Service;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.cmzsoft.weather.Model.LocationWeatherModel;

import java.util.ArrayList;
import java.util.List;

public class LocationWeatherService extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "WeatherCMZ.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_NAME = "location_weather";

    public LocationWeatherService(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        this.createTableLocationWeatherIfNotExist(db);
    }

    private void createTableLocationWeatherIfNotExist(SQLiteDatabase db) {
        String createTable = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME +
                " (" + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "name TEXT, "
                + "latitude REAL, "
                + "longitude REAL, "
                + "isDefault INTEGER,"
                + "weather TEXT, "
                + "fullPathLocation TEXT)";
        db.execSQL(createTable);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public boolean checkIsExistLocationInDb(LocationWeatherModel lw) {
        SQLiteDatabase db = this.getWritableDatabase();
        String checkQuery = "SELECT * FROM " + TABLE_NAME + " WHERE fullPathLocation = ?";
        Cursor cursor = db.rawQuery(checkQuery, new String[]{lw.getName()});
        if (cursor.getCount() > 0) {
            cursor.close();
            db.close();
            return true;
        }
        return false;
    }

    public List<LocationWeatherModel> getAllLocationWeather() {
        List<LocationWeatherModel> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM " + TABLE_NAME;
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToFirst()) {
            do {
                LocationWeatherModel lw = new LocationWeatherModel();
                lw.setId(cursor.getLong(cursor.getColumnIndexOrThrow("id")));
                lw.setName(cursor.getString(cursor.getColumnIndexOrThrow("name")));
                lw.setLatitude(cursor.getDouble(cursor.getColumnIndexOrThrow("latitude")));
                lw.setLongitude(cursor.getDouble(cursor.getColumnIndexOrThrow("longitude")));
                lw.setWeather(cursor.getString(cursor.getColumnIndexOrThrow("weather")));
                lw.setFullPathLocation(cursor.getString(cursor.getColumnIndexOrThrow("fullPathLocation")));
                list.add(lw);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return list;
    }

    public boolean insertOrUpdateLocationWeather(LocationWeatherModel lw) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("id", lw.getId());
        values.put("name", lw.getName());
        values.put("latitude", lw.getLatitude());
        values.put("longitude", lw.getLongitude());
        values.put("weather", lw.getWeather());
        values.put("fullPathLocation", lw.getFullPathLocation());
        long result = db.insert("location_weather", null, values);
        db.close();
        return result != -1;
    }

    public boolean changeDefaultLocation() {
        SQLiteDatabase db = this.getWritableDatabase();
        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE isDefault=1";
        Cursor cursor = db.rawQuery(sql, null);
        LocationWeatherModel lw = new LocationWeatherModel();
        lw.setId(cursor.getLong(cursor.getColumnIndexOrThrow("id")));
        lw.setName(cursor.getString(cursor.getColumnIndexOrThrow("name")));
        lw.setLatitude(cursor.getDouble(cursor.getColumnIndexOrThrow("latitude")));
        lw.setLongitude(cursor.getDouble(cursor.getColumnIndexOrThrow("longitude")));
        lw.setWeather(cursor.getString(cursor.getColumnIndexOrThrow("weather")));
        lw.setFullPathLocation(cursor.getString(cursor.getColumnIndexOrThrow("fullPathLocation")));
        lw.setIsDefaultLocation(cursor.getInt(cursor.getColumnIndexOrThrow("isDefault")));
        lw.setIsDefaultLocation(0);
        cursor.close();
        sql = "insert "
    }
}
