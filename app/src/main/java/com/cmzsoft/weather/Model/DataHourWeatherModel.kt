package com.cmzsoft.weather.Model

class DataHourWeatherModel(
    var timeEpoch: Long,
    var time: String,
    var tempC: Int,
    var tempF: Int,
    var isDay: Boolean,
    var urlIcon: Int
)
