package com.cmzsoft.weather.Service;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;

import androidx.annotation.RequiresPermission;
import androidx.core.content.ContextCompat;

import com.cmzsoft.weather.Model.LocationWeatherModel;
import com.cmzsoft.weather.Service.Interface.GetCurrentLocationCallback;
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

    @RequiresPermission(allOf = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION})
    public static void getCurrentLocation(final GetCurrentLocationCallback callback) {
        if (checkPermissionLocation()) {
            FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(appContext);
            fusedLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        double latitude = location.getLatitude();
                        double longitude = location.getLongitude();
                        Geocoder geocoder = new Geocoder(appContext);
                        try {
                            List<Address> addressList = geocoder.getFromLocation(latitude, longitude, 5);
                            assert addressList != null;
                            Address add = addressList.get(0);
                            String[] ls = add.getAddressLine(0).split(",");
                            String nameAdd = "";
                            if (ls.length >= 3) nameAdd = ls[ls.length - 3];
                            else nameAdd = ls[0];
                            LocationWeatherModel lc = new LocationWeatherModel(0, nameAdd, add.getLatitude(), add.getLongitude(), null, add.getAddressLine(0), 0);
                            callback.onLocationReceived(lc);
                        } catch (IOException e) {
                            callback.onError(e);
                        }
                    } else callback.onError(new Exception("Location is null"));
                }
            });
        }
    }

    public static LocationWeatherModel getLocationFromLatLon(double lat, double lon) {
        try {
            Geocoder geocoder = new Geocoder(appContext);
            List<Address> list = geocoder.getFromLocation(lat, lon, 5);
            assert list != null;
            Address add = list.get(0);
            String[] ls = add.getAddressLine(0).split(",");
            String nameAdd = "";
            nameAdd = ls.length >= 3 ? ls[ls.length - 3] : ls[0];
            return new LocationWeatherModel(0, nameAdd, add.getLatitude(), add.getLongitude(), null, add.getAddressLine(0), 0);
        } catch (Exception e) {
            return null;
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