package com.cmzsoft.weather


import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.time.LocalDate
import java.time.format.DateTimeFormatter

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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            setupSpinnerDateForm()
        }
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
        val detail = listOf("độ C", "độ F")
        spinner.text = items.get(0);
        var i = 1;
        findViewById<TextView>(R.id.txt_temp_model).text = detail.get(0);
        (spinner.parent as View).setOnClickListener {
            if (i >= items.size) i = 0;
            spinner.text = items.get(i);
            findViewById<TextView>(R.id.txt_temp_model).text = spinner.text
            findViewById<TextView>(R.id.txt_temp_model).text = detail.get(i);
            i++;
        }
    }

    private fun setupSpinnerRainfall() {
        val spinner: TextView = findViewById(R.id.fake_spinner_rain)
        val items = listOf("mm", "cm", "in")
        val detail = listOf("mi-li-mét", "xăng-ti-mét", "inch");
        spinner.text = items.get(0);
        findViewById<TextView>(R.id.txt_rainfall_model).text = detail.get(0)
        var i = 1;
        (spinner.parent as View).setOnClickListener {
            if (i >= items.size) i = 0;
            spinner.text = items.get(i);
            findViewById<TextView>(R.id.txt_rainfall_model).text = detail.get(i)
            i++;
        }
    }

    private fun setupSpinnerVisibility() {
        val spinner: TextView = findViewById(R.id.fake_spinner_visibility)
        val items = listOf("km", "mile", "m")
        spinner.text = items.get(0);
        var i = 1;
        val detail = listOf("ki-lô-mét", "dặm", "mét")
        findViewById<TextView>(R.id.txt_visibilyty_model).text = detail.get(0)
        (spinner.parent as View).setOnClickListener {
            if (i >= items.size) i = 0;
            spinner.text = items.get(i);
            findViewById<TextView>(R.id.txt_visibilyty_model).text = detail.get(i)
            i++;
        }
    }

    private fun setupSpinnerWindSpeed() {
        val spinner: TextView = findViewById(R.id.fake_spinner_wind_speed)
        val items = listOf("mph", "km/h", "m/s")
        val txtDetail = findViewById<TextView>(R.id.txt_wind_speed_model)
        spinner.text = items.get(0);
        val detail = listOf("dặm trên giờ", "ki-lô-mét trên giờ", "mét trên giây")
        var i = 1;
        (spinner.parent as View).setOnClickListener {
            if (i >= items.size) i = 0;
            spinner.text = items.get(i);
            txtDetail.text = detail.get(i)
            i++;
        }
    }

    private fun setupSpinnerAtm() {
        val spinner: TextView = findViewById(R.id.fake_spinner_atm)
        val items = listOf("mmHg", "inHg", "psi", "bar", "mBar", "hPa", "atm")
        val detail = listOf(
            "milimét thủy ngân",
            "inch thủy ngân",
            "pound trên inch vuông",
            "bar",
            "milibar",
            "hectopascal",
            "khí quyển"
        )
        val txtDetail = findViewById<TextView>(R.id.txt_atm_model)
        spinner.text = items.get(0);
        var i = 1;
        (spinner.parent as View).setOnClickListener {
            if (i >= items.size) i = 0;
            spinner.text = items.get(i);
            txtDetail.text = detail.get(i)
            i++;
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setupSpinnerDateForm() {
        val spinner: TextView = findViewById(R.id.fake_spinner_date_form)
        val items = listOf("DD/MM/YYYY", "MM/DD/YYYY", "YYYY/MM/DD")
        spinner.text = items.get(0);
        var i = 1;
        val currentDate = LocalDate.now()
        val formatter = when (items[0]) {
            "DD/MM/YYYY" -> DateTimeFormatter.ofPattern("dd/MM/yyyy")
            "MM/DD/YYYY" -> DateTimeFormatter.ofPattern("MM/dd/yyyy")
            "YYYY/MM/DD" -> DateTimeFormatter.ofPattern("yyyy/MM/dd")
            else -> DateTimeFormatter.ofPattern("yyyy-MM-dd")
        }
        findViewById<TextView>(R.id.txt_time_model).text = currentDate.format(formatter)
        (spinner.parent as View).setOnClickListener {
            if (i >= items.size) i = 0;
//            val currentDate = LocalDate.now()
            val formatter = when (items[i]) {
                "DD/MM/YYYY" -> DateTimeFormatter.ofPattern("dd/MM/yyyy")
                "MM/DD/YYYY" -> DateTimeFormatter.ofPattern("MM/dd/yyyy")
                "YYYY/MM/DD" -> DateTimeFormatter.ofPattern("yyyy/MM/dd")
                else -> DateTimeFormatter.ofPattern("yyyy-MM-dd")
            }
            findViewById<TextView>(R.id.txt_time_model).text = currentDate.format(formatter)

            spinner.text = items[i]
            i++;
        }
    }
}