package com.cmzsoft.weather.Model

class TitleChartItemModel(
    val time: String,
    val weatherIconRes: Int? = null, // drawable resource, nullable
    val weatherIconUrl: String? = null, // link ảnh, nullable
    val rainPercent: Int
)