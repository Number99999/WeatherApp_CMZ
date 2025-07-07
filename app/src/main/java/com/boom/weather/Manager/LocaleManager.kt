package com.boom.weather.Manager

import android.app.Activity
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

    /**
     * Set the app's locale (language)
     * @param languageCode ISO language code (e.g., "en", "es", "fr", "tl", etc.)
     */
    fun setLocale(languageCode: String) {
        // Persist the selected language
        val editor = sharedPreferences.edit()
        editor.putString(LANGUAGE_KEY, languageCode)
        editor.apply()

        // Change the app's locale
        val locale = Locale(languageCode)
        Locale.setDefault(locale)

        val config = Configuration()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            config.setLocale(locale)
            // Apply the configuration changes to the context for Android Nougat and above
            val newContext = context.createConfigurationContext(config)
            context.resources.updateConfiguration(config, context.resources.displayMetrics)
        } else {
            config.locale = locale
            // Apply the configuration changes to the context for older Android versions
            context.resources.updateConfiguration(config, context.resources.displayMetrics)
        }

        // If context is Activity, recreate it to reflect the new language
        if (context is Activity) {
            context.recreate()
        }
    }

    /**
     * Get the saved language from SharedPreferences
     * @return The saved language code or default language code ("en")
     */
    fun getSavedLanguage(): String {
        return sharedPreferences.getString(LANGUAGE_KEY, "en") ?: "en"
    }

    /**
     * Apply the saved language to the app when the app is restarted
     */
    fun applySavedLanguage() {
        val languageCode = getSavedLanguage()
        setLocale(languageCode)
    }
}
