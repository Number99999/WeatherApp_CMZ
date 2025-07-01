package com.boom.weather.Service;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.boom.weather.Model.LocationWeatherModel;

import java.util.List;

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

    public void loadAllLocationInDb() {
        List<LocationWeatherModel> l = locationWeatherService.getAllLocationWeather();
        for (int i = 0; i < l.size(); i++) {
            System.out.println("LocationWeatherModel: " + l.get(i).toString());
        }
    }
}
