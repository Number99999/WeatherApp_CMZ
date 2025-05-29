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

class ActivityCustomLayout : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_custom_layout)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        testAdapter()
        findViewById<ImageButton>(R.id.backButton).setOnClickListener {
            finish()
        }
    }

    private fun testAdapter() {
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        val dataList = listOf(
            WeatherItem(
                "15:00",
                R.drawable.cloudy,
                "37°C",
                "NDN, 6.9mph",
                R.drawable.cloudy_sunny,
                "5%"
            ),
            WeatherItem(
                "15:00",
                R.drawable.cloudy,
                "37°C",
                "NDN, 6.9mph",
                R.drawable.cloudy_sunny,
                "5%"
            ),
            WeatherItem(
                "15:00",
                R.drawable.cloudy,
                "37°C",
                "NDN, 6.9mph",
                R.drawable.cloudy_sunny,
                "5%"
            ),
        )
        val adapter = CustomLayoutAdapter(dataList)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
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