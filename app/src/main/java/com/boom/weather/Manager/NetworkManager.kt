package com.boom.weather.Manager

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities

class NetworkManager private constructor(private val context: Context) {

    companion object {
        @Volatile
        private var instance: NetworkManager? = null

        fun getInstance(context: Context): NetworkManager {
            return instance ?: synchronized(this) {
                instance ?: NetworkManager(context.applicationContext).also {
                    instance = it
                }
            }
        }
    }

    fun isConnected(): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false

        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
}