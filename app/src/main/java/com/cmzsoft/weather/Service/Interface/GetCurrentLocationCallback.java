package com.cmzsoft.weather.Service.Interface;

import com.cmzsoft.weather.Model.LocationWeatherModel;

public interface GetCurrentLocationCallback {
    void onLocationReceived(LocationWeatherModel address);

    void onError(Exception e);
}