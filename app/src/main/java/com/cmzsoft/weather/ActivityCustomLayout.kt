package com.cmzsoft.weather

import android.os.Bundle
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cmzsoft.weather.CustomAdapter.CustomLayoutAdapter
import com.cmzsoft.weather.FrameWork.Data.LocalStorageManager
import com.cmzsoft.weather.Model.CustomLayoutModel
import com.cmzsoft.weather.Model.Object.KeysStorage

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
            CustomLayoutItem("Dự báo theo giờ", R.drawable.icon_chart, _safeData.hourly),
            CustomLayoutItem("Dự báo hàng ngày", R.drawable.icon_chart, _safeData.daily),
            CustomLayoutItem("Lượng mưa", R.drawable.icon_rainfall, _safeData.rainfall),
            CustomLayoutItem("Gió", R.drawable.windy, _safeData.wind),
            CustomLayoutItem(
                "Chất lượng không khí", R.drawable.icon_air_quality, _safeData.airQuality
            ),
            CustomLayoutItem("Chi tiết", R.drawable.icon_detail, _safeData.detail),
            CustomLayoutItem("Nhiếp ảnh", R.drawable.icon_camera, _safeData.camera),
            CustomLayoutItem("Dị ứng", R.drawable.icon_allergy, _safeData.allergy),
            CustomLayoutItem("Bản đồ", R.drawable.icon_map, _safeData.map),
            CustomLayoutItem("CN & Sức ép", R.drawable.icon_sucep, _safeData.sucep)
        )
        val adapter = CustomLayoutAdapter(data) { title, isChecked ->
            when (title) {
                "Dự báo theo giờ" -> _safeData.hourly = isChecked
                "Dự báo hàng ngày" -> _safeData.daily = isChecked
                "Lượng mưa" -> _safeData.rainfall = isChecked
                "Gió" -> _safeData.wind = isChecked
                "Chất lượng không khí" -> _safeData.airQuality = isChecked
                "Chi tiết" -> _safeData.detail = isChecked
                "Nhiếp ảnh" -> _safeData.camera = isChecked
                "Dị ứng" -> _safeData.allergy = isChecked
                "Bản đồ" -> _safeData.map = isChecked
                "CN & Sức ép" -> _safeData.sucep = isChecked
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