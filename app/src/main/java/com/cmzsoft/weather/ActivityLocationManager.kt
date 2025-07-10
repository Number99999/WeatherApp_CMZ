package com.cmzsoft.weather

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cmzsoft.weather.APICall.RequestAPI
import com.cmzsoft.weather.CustomAdapter.ItemLocationManagerAdapter
import com.cmzsoft.weather.Manager.NetworkManager
import com.cmzsoft.weather.Model.LocationWeatherModel
import com.cmzsoft.weather.Service.DatabaseService
import com.cmzsoft.weather.Service.Interface.GetCurrentLocationCallback
import com.cmzsoft.weather.Service.LocationService
import com.cmzsoft.weather.Utils.WeatherUtil
import com.cmzsoft.weather.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ActivityLocationManager : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_location_manager)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        addEventOnBtnBackClicked()
        if (NetworkManager.getInstance(this).isConnected()) setupAdapter()
        else {
            findViewById<TextView>(R.id.txt_title).text = "Network not connected";
        }
        findViewById<TextView>(R.id.txt_title).isSelected = true
    }
    
    private fun setupAdapter() {
        var listModel: List<LocationWeatherModel> =
            DatabaseService.getInstance(this).locationWeatherService.getAllLocationWeather();
        val recyclerView = findViewById<RecyclerView>(R.id.recycler_view)
        val newList = listModel.toMutableList()
        newList.add(LocationWeatherModel())
        var adapter = ItemLocationManagerAdapter(newList.toMutableList())
//        findViewById<TextView>(R.id.btn_edit).setOnClickListener {
//            adapter.btnEditClicked();
//        }
        if (LocationService.checkPermissionLocation() == false) {
            recyclerView.adapter = adapter
            recyclerView.layoutManager =
                LinearLayoutManager(this@ActivityLocationManager, RecyclerView.VERTICAL, false)
            adapter.notifyDataSetChanged()
            lifecycleScope.launch {
                for (item in newList) {
                    val requestAPI = RequestAPI.getInstance()
                    val result = withContext(Dispatchers.IO) {
                        requestAPI.GetCurrentWeather(
                            item.latitude, item.longitude
                        )
                    }
                    val currentWeather = result.getJSONObject("current_weather")
                    val iconName = WeatherUtil.getWeatherIconName(
                        currentWeather.getInt("weathercode"), currentWeather.getInt("is_day") == 1
                    )
                    val resId = resources.getIdentifier(iconName, "drawable", packageName)
                    item.weather = resId.toString()
                    adapter.notifyItemChanged(newList.indexOf(item))
                    recyclerView.adapter = adapter
                    recyclerView.layoutManager = LinearLayoutManager(
                        this@ActivityLocationManager, RecyclerView.VERTICAL, false
                    )
                    adapter.notifyDataSetChanged()
                }
            }
        } else {
            if (ActivityCompat.checkSelfPermission(
                    this, Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
                    this, Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
            LocationService.getCurrentLocation(object : GetCurrentLocationCallback {
                @Override
                override fun onLocationReceived(address: LocationWeatherModel) {
                    newList.add(0, address);
                    adapter = ItemLocationManagerAdapter(newList.toMutableList())
                    recyclerView.adapter = adapter
                    recyclerView.layoutManager = LinearLayoutManager(
                        this@ActivityLocationManager, RecyclerView.VERTICAL, false
                    )
                    adapter.notifyDataSetChanged()
                    recyclerView.scrollToPosition(0)

                    lifecycleScope.launch {
                        for (item in newList) {
                            val requestAPI = RequestAPI.getInstance()
                            val result = withContext(Dispatchers.IO) {
                                requestAPI.GetCurrentWeather(
                                    item.latitude, item.longitude
                                )
                            }
                            val currentWeather = result.getJSONObject("current_weather")
                            val iconName = WeatherUtil.getWeatherIconName(
                                currentWeather.getInt("weathercode"),
                                currentWeather.getInt("is_day") == 1
                            )
                            val resId = resources.getIdentifier(iconName, "drawable", packageName)
                            item.weather = resId.toString()
                            adapter.notifyItemChanged(newList.indexOf(item))
                        }
                    }
                }

                override fun onError(exception: Exception) {
                    recyclerView.adapter = adapter
                    recyclerView.layoutManager = LinearLayoutManager(
                        this@ActivityLocationManager, RecyclerView.VERTICAL, false
                    )
                    adapter.notifyDataSetChanged()
                    exception.printStackTrace();
                }
            })
        }
    }

    private fun addEventOnBtnBackClicked() {
        findViewById<ImageButton>(R.id.backButton)?.setOnClickListener {
            finish()
        }
    }
}