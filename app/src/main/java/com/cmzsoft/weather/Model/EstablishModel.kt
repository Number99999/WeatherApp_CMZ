package com.cmzsoft.weather.Model

data class EstablishModel(
    var boughtPackage: Boolean = false,
    var typeTemp: String = "C",
    var rainfall: String = "mm",
    var visibility: String = "km",
    var winSpeed: String = "mph",
    var presure: String = "mmHg",
    var dateForm: String = "DD/MM/YYYY",
    var is24h: Boolean = true
)