package com.cmzsoft.weather.Service;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;

import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.IOException;
import java.util.List;

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
            FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(appContext);
            fusedLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        double latitude = location.getLatitude();
                        double longitude = location.getLongitude();
                        Geocoder geocoder = new Geocoder(appContext);
                        try {
                            List<Address> addressList = geocoder.getFromLocation(latitude, longitude, 1);
                            assert addressList != null;
                            System.out.println("ebhehehehe " + addressList.toString());
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            });
        }
    }

    public static List<Address> getLocationFromName(String s) throws IOException {
        Geocoder g = new Geocoder(appContext);
        return g.getFromLocationName(s, 5);
    }

    public static boolean checkPermissionLocation() {
        return ContextCompat.checkSelfPermission(appContext, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(appContext, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }
}