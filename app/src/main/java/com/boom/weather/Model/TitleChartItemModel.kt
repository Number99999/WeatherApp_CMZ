package com.boom.weather.Model

class TitleChartItemModel(
    val time: String,
    val weatherIconRes: Int? = null, // drawable resource, nullable
    val weatherIconUrl: Int, // link ảnh, nullable
    var isDay: Boolean,
    val rainPercent: Int
)