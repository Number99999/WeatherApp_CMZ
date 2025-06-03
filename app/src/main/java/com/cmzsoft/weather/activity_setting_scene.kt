package com.cmzsoft.weather


import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.Spinner
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class activity_setting_scene : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_setting_scene)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initEventBack()
        setupSpinnerTemp()
        setupSpinnerRainfall()
        setupSpinnerVisibility()
        setupSpinnerWindSpeed()
        setupSpinnerAtm()
        setupSpinnerDateForm()
    }

    private fun initEventBack() {
        val button = findViewById<ImageButton>(R.id.backButton);
        button.setOnClickListener {
            finish()
        }
    }

    private fun setupSpinnerTemp() {
        val spinner: Spinner = findViewById(R.id.spinner_temp)
        val items = listOf("C", "F")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, items)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
    }

    private fun setupSpinnerRainfall() {
        val spinner: Spinner = findViewById(R.id.spinner_rain)
        val items = listOf("mm", "cm", "m")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, items)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
    }

    private fun setupSpinnerVisibility() {
        val spinner: Spinner = findViewById(R.id.spinner_visibility)
        val items = listOf("km", "Dặm")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, items)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
    }

    private fun setupSpinnerWindSpeed() {
        val spinner: Spinner = findViewById(R.id.spinner_wind_speed)
        val items = listOf("m/s", "km/h", "mph")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, items)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
    }

    private fun setupSpinnerAtm() {
        val spinner: Spinner = findViewById(R.id.spinner_atm)
        val items = listOf("Pa", "hPa", "mbar", "Bar", "atm")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, items)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
    }

    private fun setupSpinnerDateForm() {
        val spinner: Spinner = findViewById(R.id.spinner_date_format)
        val items = listOf("dd/MM/yyyy", "MM/dd/yyyy", "yyyy/MM/dd")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, items)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
    }
}