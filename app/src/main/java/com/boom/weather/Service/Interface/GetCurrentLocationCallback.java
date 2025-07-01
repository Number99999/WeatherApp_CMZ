package com.boom.weather.Service.Interface;

import com.boom.weather.Model.LocationWeatherModel;

public interface GetCurrentLocationCallback {
    void onLocationReceived(LocationWeatherModel address);

    void onError(Exception e);
}