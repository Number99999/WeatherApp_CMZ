package com.boom.weather.Model

import com.google.android.gms.maps.model.LatLng

data class CurLocationModel(
    var latLng: LatLng,
    var title: String
)