package com.cmzsoft.weather

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cmzsoft.weather.APICall.RequestAPI
import com.cmzsoft.weather.CustomAdapter.ItemLocationManagerAdapter
import com.cmzsoft.weather.Model.LocationWeatherModel
import com.cmzsoft.weather.Service.DatabaseService
import com.cmzsoft.weather.Service.Interface.GetCurrentLocationCallback
import com.cmzsoft.weather.Service.LocationService
import com.cmzsoft.weather.Utils.WeatherUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ActivityLocationManager : AppCompatActivity() {
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
        setupAdapter()
        findViewById<TextView>(R.id.btn_add_location).setOnClickListener {
            val changePage = Intent(this, ActivityChooseLocation::class.java)
            changePage.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(changePage)
        }
        findViewById<TextView>(R.id.txt_title).isSelected = true
    }


    private fun setupAdapter() {
        var listModel: List<LocationWeatherModel> =
            DatabaseService.getInstance(this).locationWeatherService.getAllLocationWeather();
        for (i in listModel) println("Loaded ${i}")

        val recyclerView = findViewById<RecyclerView>(R.id.recycler_view)
        var newList = listModel.toMutableList();

        var adapter = ItemLocationManagerAdapter(listModel.toMutableList())

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)

        findViewById<TextView>(R.id.btn_edit).setOnClickListener {
            adapter.btnEditClicked();
        }

        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
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
                }
            }
        } else {
            LocationService.getCurrentLocation(object : GetCurrentLocationCallback {
                @Override
                override fun onLocationReceived(address: LocationWeatherModel) {

                    newList.add(0, address);
                    adapter = ItemLocationManagerAdapter(newList.toMutableList())
                    recyclerView.adapter = adapter
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
                            val resId =
                                resources.getIdentifier(iconName, "drawable", packageName)
                            item.weather = resId.toString()
                            adapter.notifyItemChanged(newList.indexOf(item))
                        }
                    }
                }

                override fun onError(exception: Exception) {
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