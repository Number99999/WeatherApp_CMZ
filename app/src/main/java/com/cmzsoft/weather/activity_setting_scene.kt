package com.cmzsoft.weather


import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
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
        val spinner: TextView = findViewById(R.id.fake_spinner_temp)
        val items = listOf("C", "F")
        spinner.text = items.get(0);
        var i = 1;
        (spinner.parent as View).setOnClickListener {
            if (i >= items.size) i = 0;
            spinner.text = items.get(i);
            i++;
        }
    }

    private fun setupSpinnerRainfall() {
        val spinner: TextView = findViewById(R.id.fake_spinner_rain)
        val items = listOf("mm", "cm", "im")
        spinner.text = items.get(0);
        var i = 1;
        (spinner.parent as View).setOnClickListener {
            if (i >= items.size) i = 0;
            spinner.text = items.get(i);
            i++;
        }
    }

    private fun setupSpinnerVisibility() {
        val spinner: TextView = findViewById(R.id.fake_spinner_visibility)
        val items = listOf("km", "mile", "m")
        spinner.text = items.get(0);
        var i = 1;
        (spinner.parent as View).setOnClickListener {
            if (i >= items.size) i = 0;
            spinner.text = items.get(i);
            i++;
        }
    }

    private fun setupSpinnerWindSpeed() {
        val spinner: TextView = findViewById(R.id.fake_spinner_wind_speed)
        val items = listOf("mph", "km/h", "mi/h", "m/s")
        spinner.text = items.get(0);
        var i = 1;
        (spinner.parent as View).setOnClickListener {
            if (i >= items.size) i = 0;
            spinner.text = items.get(i);
            i++;
        }
    }

    private fun setupSpinnerAtm() {
        val spinner: TextView = findViewById(R.id.fake_spinner_atm)
        val items = listOf("mmHg", "inHg", "psi", "bar", "mBar", "hPa", "atm")
        spinner.text = items.get(0);
        var i = 1;
        (spinner.parent as View).setOnClickListener {
            if (i >= items.size) i = 0;
            spinner.text = items.get(i);
            i++;
        }
    }

    private fun setupSpinnerDateForm() {
        val spinner: TextView = findViewById(R.id.fake_spinner_date_form)
        val items = listOf("24 giờ", "12 giờ")
        spinner.text = items.get(0);
        var i = 1;
        (spinner.parent as View).setOnClickListener {
            if (i >= items.size) i = 0;
            spinner.text = items.get(i);
            i++;
        }
    }
}