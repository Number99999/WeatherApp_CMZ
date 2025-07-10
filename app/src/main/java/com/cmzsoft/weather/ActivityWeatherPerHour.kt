package com.cmzsoft.weather

import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cmzsoft.weather.APICall.RequestAPI
import com.cmzsoft.weather.CustomAdapter.WeatherItemAdapter
import com.cmzsoft.weather.Model.FakeGlobal
import com.cmzsoft.weather.Utils.WeatherUtil
import com.cmzsoft.weather.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ActivityWeatherPerHour : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_weather_per_hour)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupAdapterData()
        findViewById<ImageButton>(R.id.backButton).setOnClickListener {
            finish()
        }
        findViewById<TextView>(R.id.cityName).text = FakeGlobal.getInstance().curLocation.name
    }

    private fun setupAdapterData() {
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)

        CoroutineScope(Dispatchers.Main).launch {
            try {
                val dataList = withContext(Dispatchers.IO) {
                    RequestAPI.getInstance().getWeatherPerHourInNextTwentyFour(
                        FakeGlobal.getInstance().curLocation.latitude,
                        FakeGlobal.getInstance().curLocation.longitude
                    )
                }
                if (dataList.isNotEmpty()) {
                    recyclerView.layoutManager = LinearLayoutManager(this@ActivityWeatherPerHour)
                    recyclerView.adapter = WeatherItemAdapter(dataList)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun getWeatherIconRes(iconCode: Int, isDay: Boolean): Int {
        val name = WeatherUtil.getWeatherIconName(iconCode, isDay)
        val resId =
            resources.getIdentifier(name, "drawable", packageName)
        return resId
    }
}

data class WeatherItem(
    val time: String,
    val iconRes: Int,
    val degree: String,
    val wind: String,
    val rainIconRes: Int,
    val rainfallRate: String
)