package com.boom.weather.NotificationApp

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import androidx.core.app.NotificationCompat
import com.boom.weather.FrameWork.Data.LocalStorageManager
import com.boom.weather.Model.NavMenuModel
import com.boom.weather.Model.Object.KeysStorage
import com.boom.weather.R

object NotiManager {
    private val CHANNEL_ID = "cmz.soft.weather"
    private val CHANNEL_NAME = "My Channel"
    private val CHANNEL_DESCRIPTION = "Channel for app notifications"

    private var isCreate: Boolean = false
    fun createNotificationChannel(context: Context) {
        if (isCreate) return;
        isCreate = true;
        if (LocalStorageManager.getObject<NavMenuModel>(
                KeysStorage.navMenuModel,
                NavMenuModel::class.java
            ).notification == true
        ) {

        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
                description = CHANNEL_DESCRIPTION
            }

            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun showNotification(
        context: Context,
        title: String,
        message: String,
        notificationId: Int = 1001,
        bitmap: Bitmap? = null
    ) {
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.icon_notification).setContentTitle(title)
            .setContentText(message).setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        if (bitmap != null) {
            builder.setStyle(NotificationCompat.BigPictureStyle().bigPicture(bitmap))
        }

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(notificationId, builder.build())
    }

    fun sendNotiWeather() {

    }
}