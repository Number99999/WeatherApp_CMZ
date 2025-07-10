package com.cmzsoft.weather

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import java.util.Locale

open class BaseActivity : AppCompatActivity() {
    override fun attachBaseContext(newBase: Context) {
        val locale = getSavedLocale(newBase)
        val context = updateBaseContextLocale(newBase, locale)
        super.attachBaseContext(context)
    }

    private fun updateBaseContextLocale(context: Context, language: String): Context {
        val locale = Locale(language)
        Locale.setDefault(locale)
        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)
        return context.createConfigurationContext(config)
    }

    private fun getSavedLocale(context: Context): String {
        val pref = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
        return pref.getString("language", "en") ?: "en"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
}
