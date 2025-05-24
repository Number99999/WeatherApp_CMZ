package com.cmzsoft.weather.DatabaService;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class DatabaseService {
    private static DatabaseService instance;
    private SQLiteDatabase database;
    private LocationWeatherService locationWeatherService;

    private DatabaseService(Context context) {
        locationWeatherService = new LocationWeatherService(context);
        database = locationWeatherService.getWritableDatabase();
    }

    public static synchronized DatabaseService getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseService(context.getApplicationContext());
        }
        return instance;
    }

    public LocationWeatherService getLocationWeatherService() {
        return locationWeatherService;
    }

    public SQLiteDatabase getDatabase() {
        return database;
    }

    public void close() {
        if (locationWeatherService != null) {
            locationWeatherService.close();
        }
        instance = null;
    }
}
