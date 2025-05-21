package com.cmzsoft.weather

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.cmzsoft.weather.APICall.RequestAPI
import com.google.android.material.navigation.NavigationView
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.appcompat.app.AlertDialog

class MainActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var toggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val requestAPI = RequestAPI()
        Thread {
            val result = requestAPI.CallAPI()
            runOnUiThread {
                Toast.makeText(this, result, Toast.LENGTH_SHORT).show()
            }
        }.start()

        val btn = findViewById<Button>(R.id.button2)
        btn.setOnClickListener {
//            val changePage = Intent(this, activity_setting_scene::class.java);
            val changePage = Intent(this, activity_setting_scene::class.java);
            startActivity(changePage);
        }

        InitEventNavigationBar();
    }


    private fun InitEventNavigationBar() {
        drawerLayout = findViewById(R.id.main)
        navView = findViewById(R.id.nav_view)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        toggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_location -> {
                    Toast.makeText(this, "Quản lý vị trí", Toast.LENGTH_SHORT).show()
                }

                R.id.nav_notification -> {
                    Toast.makeText(this, "Thông báo", Toast.LENGTH_SHORT).show()
                }

                R.id.nav_daily_weather -> {
                    Toast.makeText(this, "Thời tiết hàng ngày", Toast.LENGTH_SHORT).show()
                }

                else -> {}
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }

//        showSettingsDialog();
    }

//    private fun showSettingsDialog() {
//        val view = LayoutInflater.from(this).inflate(R.layout.dialog_settings, null)
//
//        val spinnerTemp = view.findViewById<Spinner>(R.id.spinner_temp)
//        val spinnerRain = view.findViewById<Spinner>(R.id.spinner_rain)
//        // Add more spinners here if needed
//
//        spinnerTemp.adapter = ArrayAdapter.createFromResource(
//            this,
//            R.array.temp_units,
//            android.R.layout.simple_spinner_dropdown_item
//        )
//
//        spinnerRain.adapter = ArrayAdapter.createFromResource(
//            this,
//            R.array.rain_units,
//            android.R.layout.simple_spinner_dropdown_item
//        )
//
//        val dialog = AlertDialog.Builder(this)
//            .setView(view)
//            .create()
//
//        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
//
//        val btnDone = view.findViewById<Button>(R.id.btn_done)
//        btnDone.setOnClickListener {
//            dialog.dismiss()
//        }
//
//        dialog.show()
//    }

//    override fun onBackPressed() {
//        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
//            drawerLayout.closeDrawer(GravityCompat.START)
//            Toast.makeText(this, "Close drawer onBackPressed", Toast.LENGTH_SHORT).show()
//        } else {
//            super.onBackPressed()
//        }
//    }

//        val intent = Intent(this, R.layout.)
//        startActivity(intent)
}