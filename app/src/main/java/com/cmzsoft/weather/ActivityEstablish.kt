package com.cmzsoft.weather


import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.cmzsoft.weather.FrameWork.Data.LocalStorageManager
import com.cmzsoft.weather.FrameWork.EventApp.FirebaseManager
import com.cmzsoft.weather.Model.EstablishModel
import com.cmzsoft.weather.Model.Object.KeyEventFirebase
import com.cmzsoft.weather.Model.Object.KeysStorage
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class ActivityEstablish : AppCompatActivity() {
    private lateinit var _safeData: EstablishModel
    private lateinit var _firstData: EstablishModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_establish)
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
        setupTimeFormat()
        findViewById<LinearLayout>(R.id.contain_noti).setOnClickListener {
            val t = Intent(this, ActivitySettingNotification::class.java)
            startActivity(t)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            setupSpinnerDateForm()
        }
    }

    private fun setupTimeFormat() {
        val sw = findViewById<SwitchCompat>(R.id.switchTemperature)
        sw.isChecked = _safeData.is24h
        sw.setOnClickListener {
            _safeData.is24h = sw.isChecked;
            LocalStorageManager.putObject(KeysStorage.establish, _safeData)
        }
    }

    init {
        _safeData = LocalStorageManager.getObject(KeysStorage.establish, EstablishModel::class.java)
            ?: EstablishModel()
        _firstData = _safeData.copy()
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
        var i = items.indexOf(_safeData.typeTemp)

        spinner.text = items.get(i);
        findViewById<TextView>(R.id.txt_temp_model).text = detail.get(0);
        (spinner.parent as View).setOnClickListener {
            i++;
            if (i >= items.size) i = 0;
            spinner.text = items.get(i)
            findViewById<TextView>(R.id.txt_temp_model).text = spinner.text
            findViewById<TextView>(R.id.txt_temp_model).text = detail.get(i);

            _safeData.typeTemp = items.get(i)
            LocalStorageManager.putObject(KeysStorage.establish, _safeData);
        }
    }

    private fun setupSpinnerRainfall() {
        val spinner: TextView = findViewById(R.id.fake_spinner_rain)
        val items = listOf("mm", "cm", "in")
        val detail = listOf("mi-li-mét", "xăng-ti-mét", "inch");

        findViewById<TextView>(R.id.txt_rainfall_model).text = detail.get(0)
        var i = items.indexOf(_safeData.rainfall)
        spinner.text = items.get(i);
        (spinner.parent as View).setOnClickListener {
            i++;
            if (i >= items.size) i = 0;
            spinner.text = items.get(i);
            findViewById<TextView>(R.id.txt_rainfall_model).text = detail.get(i)
            _safeData.rainfall = items.get(i)
            LocalStorageManager.putObject(KeysStorage.establish, _safeData);
        }
    }

    private fun setupSpinnerVisibility() {
        val spinner: TextView = findViewById(R.id.fake_spinner_visibility)
        val items = listOf("km", "mile", "m")
        var i = items.indexOf(_safeData.visibility)
        spinner.text = items.get(i);
        val detail = listOf("ki-lô-mét", "dặm", "mét")
        findViewById<TextView>(R.id.txt_visibilyty_model).text = detail.get(0)
        (spinner.parent as View).setOnClickListener {
            i++;
            if (i >= items.size) i = 0;
            spinner.text = items.get(i);
            findViewById<TextView>(R.id.txt_visibilyty_model).text = detail.get(i)
            _safeData.visibility = items.get(i)
            LocalStorageManager.putObject(KeysStorage.establish, _safeData);
        }
    }

    private fun setupSpinnerWindSpeed() {
        val spinner: TextView = findViewById(R.id.fake_spinner_wind_speed)
        val items = listOf("mph", "km/h", "m/s")
        val txtDetail = findViewById<TextView>(R.id.txt_wind_speed_model)
        val detail = listOf("dặm trên giờ", "ki-lô-mét trên giờ", "mét trên giây")
        var i = items.indexOf(_safeData.winSpeed)
        spinner.text = items.get(i);
        (spinner.parent as View).setOnClickListener {
            i++;
            if (i >= items.size) i = 0;
            spinner.text = items.get(i);
            txtDetail.text = detail.get(i)
            _safeData.winSpeed = items.get(i)
            LocalStorageManager.putObject(KeysStorage.establish, _safeData);
        }
    }

    private fun setupSpinnerAtm() {
        val spinner: TextView = findViewById(R.id.fake_spinner_atm)
        val items = listOf("mmHg", "inHg", "psi", "bar", "mBar", "hPa", "presure")
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
        var i = items.indexOf(_safeData.presure)
        spinner.text = items.get(i);
        (spinner.parent as View).setOnClickListener {
            i++;
            if (i >= items.size) i = 0;
            spinner.text = items.get(i);
            txtDetail.text = detail.get(i)
            _safeData.presure = items.get(i)
            LocalStorageManager.putObject(KeysStorage.establish, _safeData);
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setupSpinnerDateForm() {
        val spinner: TextView = findViewById(R.id.fake_spinner_date_form)
        val items = listOf("DD/MM/YYYY", "MM/DD/YYYY", "YYYY/MM/DD")
        var i = items.indexOf(_safeData.dateForm)
        spinner.text = items.get(i);
        val currentDate = LocalDate.now()
        val formatter = when (items[0]) {
            "DD/MM/YYYY" -> DateTimeFormatter.ofPattern("dd/MM/yyyy")
            "MM/DD/YYYY" -> DateTimeFormatter.ofPattern("MM/dd/yyyy")
            "YYYY/MM/DD" -> DateTimeFormatter.ofPattern("yyyy/MM/dd")
            else -> DateTimeFormatter.ofPattern("yyyy-MM-dd")
        }
        findViewById<TextView>(R.id.txt_time_model).text = currentDate.format(formatter)
        (spinner.parent as View).setOnClickListener {
            i++;
            if (i >= items.size) i = 0;
            val formatter = when (items[i]) {
                "DD/MM/YYYY" -> DateTimeFormatter.ofPattern("dd/MM/yyyy")
                "MM/DD/YYYY" -> DateTimeFormatter.ofPattern("MM/dd/yyyy")
                "YYYY/MM/DD" -> DateTimeFormatter.ofPattern("yyyy/MM/dd")
                else -> DateTimeFormatter.ofPattern("yyyy-MM-dd")
            }
            findViewById<TextView>(R.id.txt_time_model).text = currentDate.format(formatter)
            spinner.text = items[i]
            _safeData.dateForm = items.get(i)
            LocalStorageManager.putObject(KeysStorage.establish, _safeData);
        }
    }

    private fun sendEvenIfChange() {
        val ins = FirebaseManager.getInstance(this)
        if (_safeData.typeTemp != _firstData.typeTemp) ins.sendEvent(
            KeyEventFirebase.settingTemp,
            "type",
            _safeData.typeTemp
        );
        if (_safeData.rainfall != _firstData.rainfall) ins.sendEvent(
            KeyEventFirebase.settingRain,
            "type",
            _safeData.rainfall
        );
        if (_safeData.visibility != _firstData.visibility) ins.sendEvent(
            KeyEventFirebase.settingSpeed,
            "type",
            _safeData.visibility
        );
        if (_safeData.winSpeed != _firstData.winSpeed) ins.sendEvent(
            KeyEventFirebase.settingWindSpeed,
            "type",
            _safeData.winSpeed
        );
        if (_safeData.presure != _firstData.presure) ins.sendEvent(
            KeyEventFirebase.settingPresure,
            "type",
            _safeData.presure
        );
        if (_safeData.dateForm != _firstData.dateForm) ins.sendEvent(
            KeyEventFirebase.settingTime,
            "type",
            _safeData.dateForm
        );
    }
}