package com.cmzsoft.weather.Model

data class DataWeatherPerHourModel(
    var timeEpoch: Long,
    var time: String,
    var tempC: Int,
    var isDay: Boolean,
    var iconCode: Int,
    var winDir: String,
    var winSpeed: Float
)