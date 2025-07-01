package com.boom.weather.Model

data class CustomLayoutModel(
    var daily: Boolean = true,
    var hourly: Boolean = true,
    var rainfall: Boolean = true,
    var wind: Boolean = true,
    var airQuality: Boolean = true,
    var detail: Boolean = true,
    var camera: Boolean = true,
    var allergy: Boolean = true,
    var map: Boolean = true,
    var sucep: Boolean = true,
)