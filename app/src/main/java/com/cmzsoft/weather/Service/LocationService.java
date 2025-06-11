package com.cmzsoft.weather.Service;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

public class LocationService {

    private static LocationService instance = null;
    private static FusedLocationProviderClient fusedLocationClient;
    private static Context appContext;

    private LocationService() {
        initLocationClient();
    }

    public static LocationService getInstance(Context context) {
        if (instance == null) {
            appContext = context.getApplicationContext();
            instance = new LocationService();
        }
        return instance;
    }

    private static void initLocationClient() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(appContext);
    }

    public static void init(Context appContext) {
        LocationService.appContext = appContext;
    }

    public static void getCurrentLocation() {
        if (ContextCompat.checkSelfPermission(appContext, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(appContext, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            System.out.println("app have permission from user");
        }
    }
}