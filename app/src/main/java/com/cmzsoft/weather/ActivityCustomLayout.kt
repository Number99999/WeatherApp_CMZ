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
        setupRecycleView()
        findViewById<ImageButton>(R.id.backButton).setOnClickListener {
            finish()
        }
    }

    private fun setupRecycleView() {
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        val data = listOf(
            CustomLayoutItem("Dự báo theo giờ", R.drawable.icon_chart),
            CustomLayoutItem("Dự báo hàng ngày", R.drawable.icon_chart),
            CustomLayoutItem("Lượng mưa", R.drawable.icon_rainfall),
            CustomLayoutItem("Gió", R.drawable.windy),
            CustomLayoutItem("Chất lượng không khí", R.drawable.icon_air_quality),
            CustomLayoutItem("Chi tiết", R.drawable.icon_detail),
            CustomLayoutItem("Nhiếp ảnh", R.drawable.icon_camera),
            CustomLayoutItem("Dị ứng", R.drawable.icon_allergy),
            CustomLayoutItem("Bản đồ", R.drawable.icon_map),
            CustomLayoutItem("Bản đồ", R.drawable.icon_map)
        )
        val adapter = CustomLayoutAdapter(data)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
    }
}

data class CustomLayoutItem(
    val title: String,
    val icon: Int,
)