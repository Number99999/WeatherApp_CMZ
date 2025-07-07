package com.boom.weather

import android.os.Bundle
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.boom.weather.CustomAdapter.CustomLayoutAdapter
import com.boom.weather.FrameWork.Data.LocalStorageManager
import com.boom.weather.Model.CustomLayoutModel
import com.boom.weather.Model.Object.KeysStorage

class ActivityCustomLayout : AppCompatActivity() {
    private lateinit var _safeData: CustomLayoutModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_custom_layout)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        setupRecycleView()
        findViewById<ImageButton>(R.id.backButton).setOnClickListener {
            finish()
        }
    }

    init {
        _safeData = LocalStorageManager.getObject<CustomLayoutModel>(
            KeysStorage.customLayout, CustomLayoutModel::class.java
        ) ?: CustomLayoutModel()
    }

    private fun setupRecycleView() {
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        val data = listOf(
            CustomLayoutItem("Hourly Forecast", R.drawable.icon_chart, _safeData.hourly),
            CustomLayoutItem("Daily Forecast", R.drawable.icon_chart, _safeData.daily),
            CustomLayoutItem("Rainfall", R.drawable.icon_rainfall, _safeData.rainfall),
            CustomLayoutItem("Wind", R.drawable.windy, _safeData.wind),
            CustomLayoutItem("Air Quality", R.drawable.icon_air_quality, _safeData.airQuality),
            CustomLayoutItem("Details", R.drawable.icon_detail, _safeData.detail),
            CustomLayoutItem("Photography", R.drawable.icon_camera, _safeData.camera),
            CustomLayoutItem("Allergies", R.drawable.icon_allergy, _safeData.allergy),
            CustomLayoutItem("Map", R.drawable.icon_map, _safeData.map),
            CustomLayoutItem("UV & Pressure", R.drawable.icon_sucep, _safeData.sucep)
        )

        val adapter = CustomLayoutAdapter(data) { title, isChecked ->
            when (title) {
                "Hourly Forecast" -> _safeData.hourly = isChecked
                "Daily Forecast" -> _safeData.daily = isChecked
                "Rainfall" -> _safeData.rainfall = isChecked
                "Wind" -> _safeData.wind = isChecked
                "Air Quality" -> _safeData.airQuality = isChecked
                "Details" -> _safeData.detail = isChecked
                "Photography" -> _safeData.camera = isChecked
                "Allergies" -> _safeData.allergy = isChecked
                "Map" -> _safeData.map = isChecked
                "UV & Pressure" -> _safeData.sucep = isChecked
            }
            LocalStorageManager.putObject(KeysStorage.customLayout, _safeData)
        }
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter.notifyDataSetChanged()
    }
}

data class CustomLayoutItem(
    val title: String, val icon: Int, var isCheck: Boolean
)