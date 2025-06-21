package com.cmzsoft.weather

import android.os.Bundle
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cmzsoft.weather.CustomAdapter.CustomLayoutAdapter
import com.cmzsoft.weather.FrameWork.Data.LocalStorageManager
import com.cmzsoft.weather.Model.NotificationModel
import com.cmzsoft.weather.Model.Object.KeysStorage

class ActivitySettingNotification : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_setting_notification)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        setupRecycleView()
        setupBtnBack()
    }

    private fun setupBtnBack() {
        val btn = findViewById<ImageView>(R.id.backButton)
        btn.setOnClickListener {
            finish()
        }
    }

    private fun setupRecycleView() {
        var _safeData = LocalStorageManager.getObject<NotificationModel>(
            KeysStorage.customLayout, NotificationModel::class.java
        ) ?: NotificationModel()
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        val data = listOf(
            CustomLayoutItem("Thông báo", R.drawable.icon_chart, _safeData.notification),
            CustomLayoutItem("Báo động thời tiết", R.drawable.icon_chart, _safeData.warning),
            CustomLayoutItem(
                "Thời tiết hàng ngày", R.drawable.icon_rainfall, _safeData.weatherDaily
            ),
        )
        val adapter = CustomLayoutAdapter(data) { title, isChecked ->
            when (title) {
                "Thông báo" -> _safeData.notification = isChecked
                "Dự báo hàng ngày" -> _safeData.warning = isChecked
                "Lượng mưa" -> _safeData.weatherDaily = isChecked
            }
            LocalStorageManager.putObject(KeysStorage.noti, _safeData)
        }
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
    }
}