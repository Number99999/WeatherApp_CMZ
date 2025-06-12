package com.cmzsoft.weather.Model;

import androidx.annotation.NonNull;

public class LocationWeatherModel {
    private long id = -1;
    private String name;
    private double latitude;
    private double longitude;
    private String weather;
    private String fullPathLocation;
    private int isDefaultLocation;

    // Constructors
    public LocationWeatherModel() {
    }

    public LocationWeatherModel(int id, String name, double latitude, double longitude, String weather, String fullPathLocation, int isDefaultLocation) {
        this.id = id;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.weather = weather;
        this.fullPathLocation = fullPathLocation;
        this.isDefaultLocation = isDefaultLocation;
    }

    // Getter & Setter
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getWeather() {
        return weather;
    }

    public void setWeather(String weather) {
        this.weather = weather;
    }

    public String getFullPathLocation() {
        return fullPathLocation;
    }

    public void setFullPathLocation(String fullPathLocation) {
        this.fullPathLocation = fullPathLocation;
    }

    public void setIsDefaultLocation(int isUsedLocation) {
        this.isDefaultLocation = isUsedLocation;
    }

    public boolean getIsDefaultLocation() {
        return this.isDefaultLocation == 1;
    }

    @NonNull
    @Override
    public String toString() {
        return this.name + " " + this.latitude + " " + this.longitude + " " + this.weather + " " + this.fullPathLocation + " " + this.isDefaultLocation;
    }
}
