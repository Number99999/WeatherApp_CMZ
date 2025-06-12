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
        String createTable = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "name TEXT, "
                + "latitude REAL, "
                + "longitude REAL, "
                + "isDefault INTEGER, "  // Thêm trường isDefault
                + "weather TEXT, "
                + "fullPathLocation TEXT"
                + ")";

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
            db.close();
            return true;
        }
        return false;
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
        if (lw.getId() != 0) values.put("id", lw.getId());
        values.put("name", lw.getName());
        values.put("latitude", lw.getLatitude());
        values.put("longitude", lw.getLongitude());
        values.put("weather", lw.getWeather());
        values.put("fullPathLocation", lw.getFullPathLocation());
        long result = db.insert("location_weather", null, values);
        db.close();
        return result != -1;
    }

    public boolean changeDefaultLocation(LocationWeatherModel wModel) {
        SQLiteDatabase db = this.getWritableDatabase();

        db.beginTransaction();

        try {
            String sql = "SELECT * FROM " + TABLE_NAME + " WHERE isDefault=1";
            Cursor cursor = db.rawQuery(sql, null);

            if (cursor != null && cursor.moveToFirst()) {
                // Tạo một đối tượng LocationWeatherModel từ dữ liệu trong cursor
                LocationWeatherModel lw = new LocationWeatherModel();
                lw.setId(cursor.getLong(cursor.getColumnIndexOrThrow("id")));
                lw.setName(cursor.getString(cursor.getColumnIndexOrThrow("name")));
                lw.setLatitude(cursor.getDouble(cursor.getColumnIndexOrThrow("latitude")));
                lw.setLongitude(cursor.getDouble(cursor.getColumnIndexOrThrow("longitude")));
                lw.setWeather(cursor.getString(cursor.getColumnIndexOrThrow("weather")));
                lw.setFullPathLocation(cursor.getString(cursor.getColumnIndexOrThrow("fullPathLocation")));
                lw.setIsDefaultLocation(0); // Đặt lại isDefaultLocation là 0 cho bản ghi cũ

                String updateSql = "UPDATE " + TABLE_NAME + " SET isDefault = 0 WHERE id = ?";
                db.execSQL(updateSql, new Object[]{lw.getId()});  // Thực hiện câu lệnh UPDATE để thay đổi isDefaultLocation của bản ghi cũ
                cursor.close();
            } else {
                cursor.close();
                db.endTransaction();
                return false;
            }

            boolean insertOrUpdateSuccess = this.insertOrUpdateLocationWeather(wModel);

            if (insertOrUpdateSuccess) {
                db.setTransactionSuccessful();
            }

            db.endTransaction();
            return insertOrUpdateSuccess;
        } catch (Exception e) {
            e.printStackTrace();
            db.endTransaction();
            return false;
        } finally {
            db.close();
        }
    }
}
