package com.boom.weather.FrameWork.EventApp

import android.content.Context
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase

@Suppress("DEPRECATION")
class FirebaseManager {
    companion object {
        @Volatile
        private var instance: FirebaseManager? = null
        private var firebaseAnalytics: FirebaseAnalytics? = null

        fun getInstance(context: Context): FirebaseManager {
            return instance ?: synchronized(this) {
                instance ?: FirebaseManager().also {
                    firebaseAnalytics = FirebaseAnalytics.getInstance(context.applicationContext)
                    instance = it
                }
            }
        }
    }

    fun sendEvent(key: String, prKey: String, pr: Any) {
        val firebaseAnalytics = Firebase.analytics
        firebaseAnalytics.logEvent(key) {
            when (pr) {
                is String -> param(prKey, pr)
                is Int -> param(prKey, pr.toLong())
                is Long -> param(prKey, pr)
                is Double -> param(prKey, pr)
                is Float -> param(prKey, pr.toDouble())
                is Boolean -> param(prKey, if (pr) 1 else 0)
                else -> param(prKey, pr.toString())
            }
        }
    }
}