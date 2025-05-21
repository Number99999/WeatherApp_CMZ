package com.cmzsoft.weather

import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView

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

//        val requestAPI = RequestAPI()
//        Thread {
//            val result = requestAPI.CallAPI()
//            runOnUiThread {
//                Toast.makeText(this, result, Toast.LENGTH_SHORT).show()
//            }
//        }.start()

        val btn = findViewById<Button>(R.id.button2)
        btn.setOnClickListener {
//            val changePage = Intent(this, activity_setting_scene::class.java);
//            startActivity(changePage);

            showSettingsDialog();
        }

        InitEventNavigationBar();
    }


    private fun showSettingsDialog() {
        val container = findViewById<FrameLayout>(R.id.container_dialog_setting)

        if (container.childCount == 0) {
            val dialogView = layoutInflater.inflate(R.layout.dialog_setting, container, false)
            val params = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
            )
            params.gravity = Gravity.CENTER
            dialogView.layoutParams = params

            container.addView(dialogView)

            val btnDone = dialogView.findViewById<Button>(R.id.btn_done)
            btnDone.setOnClickListener {
                container.visibility = View.GONE
                container.removeAllViews()
            }
        }

        container.visibility = View.VISIBLE
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
                R.id.nav_settings -> {
                    showSettingsDialog();
                }
                else -> {}
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }
    }
}