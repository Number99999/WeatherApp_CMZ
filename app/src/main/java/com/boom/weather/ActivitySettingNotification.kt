package com.boom.weather

import android.os.Bundle
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.boom.weather.CustomAdapter.CustomLayoutAdapter
import com.boom.weather.FrameWork.Data.LocalStorageManager
import com.boom.weather.FrameWork.EventApp.FirebaseManager
import com.boom.weather.Model.NavMenuModel
import com.boom.weather.Model.NotificationModel
import com.boom.weather.Model.Object.KeyEventFirebase
import com.boom.weather.Model.Object.KeysStorage

class ActivitySettingNotification : BaseActivity() {
    private var _safeData: NotificationModel
    private var _firstData: NotificationModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_setting_notification)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        setupBtnBack()
        setupAdapter()
    }

    init {
        _safeData =
            LocalStorageManager.getObject(KeysStorage.settingNoti, NotificationModel::class.java)
                ?: NotificationModel()
        _firstData = _safeData.copy()
    }

    private fun setupAdapter() {
        val rec = findViewById<RecyclerView>(R.id.recyclerView)
        val l = mutableListOf<CustomLayoutItem>(
            CustomLayoutItem("Notifications", R.drawable.icon_notification, _safeData.notification),
            CustomLayoutItem("Weather Alerts", R.drawable.icon_warning, _safeData.warning),
            CustomLayoutItem("Daily Weather", R.drawable.icon_small_cloud, _safeData.weatherDaily)
        )

        val adapter = CustomLayoutAdapter(l) { title, checked ->
            when (title) {
                "Notifications" -> _safeData.notification = checked
                "Weather Alerts" -> _safeData.warning = checked
                "Daily Weather" -> _safeData.weatherDaily = checked
            }

            val _otherData = LocalStorageManager.getObject<NavMenuModel>(
                KeysStorage.navMenuModel, NavMenuModel::class.java
            )
            _otherData.notification = checked

            LocalStorageManager.putObject(KeysStorage.settingNoti, _safeData)
            LocalStorageManager.putObject(KeysStorage.navMenuModel, _otherData)
        }

        rec.adapter = adapter
        adapter.notifyDataSetChanged()
        rec.layoutManager = LinearLayoutManager(this)
    }

    private fun setupBtnBack() {
        val btn = findViewById<ImageView>(R.id.backButton)
        btn.setOnClickListener {
            sendEvenIfChange()
            finish()
        }
    }

    private fun sendEvenIfChange() {
        val ins = FirebaseManager.getInstance(this)
        if (_safeData.notification != _firstData.notification) ins.sendEvent(
            KeyEventFirebase.settingNoti, "type", _safeData.notification
        )
        if (_safeData.weatherDaily != _firstData.weatherDaily) ins.sendEvent(
            KeyEventFirebase.settingDailyWeather, "type", _safeData.weatherDaily
        )
    }
}