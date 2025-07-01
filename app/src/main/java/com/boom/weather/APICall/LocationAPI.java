package com.boom.weather.APICall;

public class LocationAPI {

    private static LocationAPI intance;

    public static LocationAPI getInstance() {
        if (intance == null) intance = new LocationAPI();
        return intance;
    }

    private void getCurrenLocation() {

    }
}
