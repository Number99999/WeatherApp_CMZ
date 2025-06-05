package com.cmzsoft.weather

import android.os.Bundle
import android.widget.ImageButton
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ActivityWeatherPerHour : AppCompatActivity() {

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
    }

    private fun setupAdapterData() {
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)

        // Sử dụng Coroutine để gọi API bất đồng bộ
        CoroutineScope(Dispatchers.Main).launch {
            try {
                // Gọi API để lấy dữ liệu
                val dataList = withContext(Dispatchers.IO) {
                    RequestAPI.getInstance().getWeatherPerHourInNextTwentyFour(
                        "${FakeGlobal.getInstance().curLocation.latLng.latitude},${FakeGlobal.getInstance().curLocation.latLng.longitude}"
                    )
                }
                if (dataList.isNotEmpty()) {
                    val adapter = WeatherItemAdapter(dataList)
                    recyclerView.adapter = adapter
                    recyclerView.layoutManager = LinearLayoutManager(this@ActivityWeatherPerHour)
                } else {
                    // Xử lý khi dataList rỗng
//                    showError("No weather data available.")
                }
            } catch (e: Exception) {
                // Xử lý khi có lỗi
                e.printStackTrace()
//                showError("Failed to fetch weather data.")
            }
        }
    }

    fun getWeatherIconRes(iconCode: Int, isDay: Boolean): Int {
        val name = WeatherUtil.getWeatherIconName(iconCode, isDay)
        val resId =
            resources.getIdentifier("status_" + name.removeSuffix(".png"), "drawable", packageName)
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