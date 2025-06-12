package com.cmzsoft.weather

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cmzsoft.weather.CustomAdapter.ItemLocationManagerAdapter
import com.cmzsoft.weather.Model.LocationWeatherModel
import com.cmzsoft.weather.Service.DatabaseService

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
        findViewById<ImageView>(R.id.btn_edit).setOnClickListener{
            Toast.makeText(this, "EDIT LOCATION", Toast.LENGTH_SHORT).show()
        }
        findViewById<ImageView>(R.id.btn_add_location).setOnClickListener{
            val changePage = Intent(this, ActivityChooseLocation::class.java)
            changePage.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(changePage)
        }
    }



    private fun setupAdapter() {
        var listModel: List<LocationWeatherModel> =
            DatabaseService.getInstance(this).locationWeatherService.getAllLocationWeather();
        for (i in listModel) println("Loaded ${i}")

        val recyclerView = findViewById<RecyclerView>(R.id.recycler_view)
        val adapter = ItemLocationManagerAdapter(listModel)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
    }

    private fun addEventOnBtnBackClicked() {
        findViewById<ImageButton>(R.id.backButton)?.setOnClickListener {
            finish()
        }
    }
}