package com.boom.weather.Service;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

import com.boom.weather.Model.LocationWeatherModel;

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
        String createTable = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" + "id INTEGER PRIMARY KEY AUTOINCREMENT, " + "name TEXT, " + "latitude REAL, " + "longitude REAL, " + "isDefault INTEGER, "  // Thêm trường isDefault
                + "weather TEXT, " + "fullPathLocation TEXT" + ")";

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

        Cursor cursor = db.rawQuery(checkQuery, new String[]{lw.getFullPathLocation()});
        if (cursor.getCount() > 0) {
            cursor.close();
            return true;
        }
        return false;
    }

    public boolean deleteItemInTable(LocationWeatherModel lw) {
        SQLiteDatabase db = this.getWritableDatabase();
        String checkQuery = "DELETE FROM " + TABLE_NAME + " WHERE fullPathLocation = ?";
        SQLiteStatement stmt = db.compileStatement(checkQuery);
        stmt.bindString(1, lw.getFullPathLocation());
        long result = stmt.executeUpdateDelete();
        return result > 0;
    }

    public LocationWeatherModel getDefaultLocationWeather() {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE isDefault=1";
        Cursor cursor = db.rawQuery(sql, null);
        if (!cursor.moveToNext()) return null;
        LocationWeatherModel lw = new LocationWeatherModel(cursor.getInt(cursor.getColumnIndexOrThrow("id")), cursor.getString(cursor.getColumnIndexOrThrow("name")), cursor.getDouble(cursor.getColumnIndexOrThrow("latitude")), cursor.getDouble(cursor.getColumnIndexOrThrow("longitude")), cursor.getString(cursor.getColumnIndexOrThrow("weather")), cursor.getString(cursor.getColumnIndexOrThrow("fullPathLocation")), cursor.getInt(cursor.getColumnIndexOrThrow("isDefault")));
        cursor.close();
        return lw;
    }

    public List<LocationWeatherModel> getAllLocationWeather() {
        List<LocationWeatherModel> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM " + TABLE_NAME + " ORDER BY isDefault DESC ";
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
                lw.setIsDefaultLocation((int) cursor.getLong(cursor.getColumnIndexOrThrow("isDefault")));
                list.add(lw);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return list;
    }

    public boolean insertLocationWeather(LocationWeatherModel lw) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        if (lw.getId() != 0) values.put("id", lw.getId());
        values.put("name", lw.getName());
        values.put("latitude", lw.getLatitude());
        values.put("longitude", lw.getLongitude());
        values.put("weather", lw.getWeather());
        values.put("fullPathLocation", lw.getFullPathLocation());
        long result = db.insert("location_weather", null, values);
        return result != -1;
    }

    public boolean updateLocation(LocationWeatherModel lwModel) {
        SQLiteDatabase db = this.getWritableDatabase();
        String updateQuery = "UPDATE " + TABLE_NAME + " SET " +
                "name = ?, " +
                "latitude = ?, " +
                "longitude = ?, " +
                "weather = ?, " +
                "fullPathLocation = ?, " +
                "isDefault = ? " +
                "WHERE fullPathLocation = ?";

        SQLiteStatement stmt = db.compileStatement(updateQuery);
        stmt.bindString(1, lwModel.getName());
        stmt.bindDouble(2, lwModel.getLatitude());
        stmt.bindDouble(3, lwModel.getLongitude());
        stmt.bindString(4, lwModel.getWeather() == null ? "" : lwModel.getWeather());
        stmt.bindString(5, lwModel.getFullPathLocation() == null ? "" : lwModel.getFullPathLocation());
        stmt.bindLong(6, lwModel.getIsDefaultLocation());
        stmt.bindString(7, lwModel.getFullPathLocation());

        long result = stmt.executeUpdateDelete();

        return result > 0;
    }

    public boolean changeDefaultLocation(LocationWeatherModel wModel) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            db.beginTransaction();
            String updateQuery = "UPDATE " + TABLE_NAME + " SET isDefault = 0 WHERE isDefault = 1";
            db.execSQL(updateQuery);
            boolean insertOrUpdateSuccess = false;
            wModel.setIsDefaultLocation(1);
            boolean isExist = checkIsExistLocationInDb(wModel);
            if (!isExist) {
                insertOrUpdateSuccess = this.insertLocationWeather(wModel);
            } else {
                insertOrUpdateSuccess = updateLocation(wModel);
            }

            if (insertOrUpdateSuccess) {
                db.setTransactionSuccessful();
            }
            db.endTransaction();
            return insertOrUpdateSuccess;
        } catch (Exception e) {
            e.printStackTrace();
            db.endTransaction();
            return false;
        }
    }

    public boolean setDontDefaultAll() {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            String updateQuery = "UPDATE " + TABLE_NAME + " SET isDefault = 0 WHERE isDefault = 1";
            db.execSQL(updateQuery);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
