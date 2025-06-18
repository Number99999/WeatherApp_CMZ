package com.cmzsoft.weather.Model;

import androidx.annotation.NonNull;

public class LocationWeatherModel {
    private long id = -1;
    private String timeZone = "GMT+0";
    private String name = "";
    private double latitude = 0;
    private double longitude = 0;
    private String weather = "";
    private String fullPathLocation = "";
    private int isDefaultLocation = 0;
    private boolean isEdit = false;

    // Constructors
    public LocationWeatherModel() {
    }

    public LocationWeatherModel(int id, String name, double latitude, double longitude, String weather, String fullPathLocation, int isDefaultLocation) {
        this.id = id;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.weather = weather;
        this.fullPathLocation = handleFullPath(fullPathLocation);
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
        return handleFullPath(fullPathLocation);
    }

    public void setFullPathLocation(String fullPathLocation) {
        this.fullPathLocation = handleFullPath(fullPathLocation);
    }

    private String handleFullPath(String fullPathLocation) {
        String[] list = fullPathLocation.split(",");
        for (int i = list.length >= 3 ? list.length - 3 : 0; i < list.length; i++) {
            list[i] = list[i].replaceAll("[0-9]", "");

        }
        StringBuilder s = new StringBuilder(list[0]);
        for (int i = 1; i < list.length; i++)
            s.append(",").append(list[i]);
        return s.toString();
    }

    public void setIsDefaultLocation(int isUsedLocation) {
        this.isDefaultLocation = isUsedLocation;
    }

    public int getIsDefaultLocation() {
        return this.isDefaultLocation;
    }

    public void setIdEdit(boolean b) {
        isEdit = b;
    }

    public boolean getIsEdit() {
        return isEdit;
    }

    public String getTimeZone() {
        return this.timeZone;
    }

    public void setTimeZone(String s) {
        this.timeZone = s;
    }

    @NonNull
    @Override
    public String toString() {
        return this.id + " " + this.name + " " + this.latitude + " " + this.longitude + " " + this.weather + " " + this.fullPathLocation + " " + this.isDefaultLocation;
    }
}
