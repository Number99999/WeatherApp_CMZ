package com.cmzsoft.weather

import android.os.Bundle
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.cmzsoft.weather.Model.LocationWeatherModel
import com.cmzsoft.weather.Service.DatabaseService

class ActivityLocationManager : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_location_manager)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        addEventOnBtnBackClicked()
        getListLocation()
    }

    private fun getListLocation() {
        var listModel: List<LocationWeatherModel> =
            DatabaseService.getInstance(this).locationWeatherService.getAllLocationWeather();
        for (i in listModel) println("Loaded ${i}")
    }

    private fun addEventOnBtnBackClicked() {
        findViewById<ImageButton>(R.id.backButton)?.setOnClickListener {
            finish()
        }
    }
}