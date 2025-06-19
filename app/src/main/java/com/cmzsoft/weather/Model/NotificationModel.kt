package com.cmzsoft.weather.Model

data class NotificationModel(
    var notification: Boolean = true, var warning: Boolean = true, var weatherDaily: Boolean = true
)