package com.cmzsoft.weather.Model

import com.google.android.gms.maps.model.LatLng

data class LocationInMapModel(
    val loc: LatLng, val title: String, val detail: String
)