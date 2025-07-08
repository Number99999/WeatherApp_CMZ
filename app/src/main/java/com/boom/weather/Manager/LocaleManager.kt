package com.boom.weather.Manager

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Build
import java.util.Locale

class LocaleManager(private val context: Context) {

    private val sharedPreferences: SharedPreferences =
        context.applicationContext.getSharedPreferences("settings", Context.MODE_PRIVATE)

    companion object {
        const val LANGUAGE_KEY = "language"
    }

    fun setLocale(languageCode: String) {
        val editor = sharedPreferences.edit()
        editor.putString(LANGUAGE_KEY, languageCode)
        editor.apply()

        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val config = Configuration()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            config.setLocale(locale)
            context.resources.updateConfiguration(config, context.resources.displayMetrics)
        } else {
            config.locale = locale
            context.resources.updateConfiguration(config, context.resources.displayMetrics)
        }
    }

    fun getSavedLanguage(): String {
        return sharedPreferences.getString(LANGUAGE_KEY, "en") ?: "en"
    }

    fun applySavedLanguage() {
        val languageCode = getSavedLanguage()
        setLocale(languageCode)
    }
}
