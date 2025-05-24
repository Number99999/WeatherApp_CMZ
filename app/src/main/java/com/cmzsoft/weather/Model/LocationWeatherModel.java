package com.cmzsoft.weather.Model;

public class LocationWeatherModel {
    private long id;
    private String name;
    private double latitude;
    private double longitude;
    private String weather;
    private String fullPathLocation;

    // Constructors
    public LocationWeatherModel() {
    }

    public LocationWeatherModel(long id, String name, double latitude, double longitude, String weather, String fullPathLocation) {
        this.id = id;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.weather = weather;
        this.fullPathLocation = fullPathLocation;
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
}
