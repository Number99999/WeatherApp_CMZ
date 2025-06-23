package com.cmzsoft.weather.FrameWork.EventApp

import android.os.Bundle
import com.google.firebase.Firebase
import com.google.firebase.analytics.analytics

class FirebaseManager {
    private fun testFirebase() {
        val parameters = Bundle().apply {
            this.putString("level_name", "Caverns01")
            this.putInt("level_difficulty", 4)
        }
//        firebaseAnalytics.setDefaultEventParameters(parameters)
    }
}