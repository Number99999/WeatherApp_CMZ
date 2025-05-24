package com.cmzsoft.weather

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.bumptech.glide.Glide
import com.cmzsoft.weather.APICall.RequestAPI
import com.google.android.material.navigation.NavigationView
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

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

        InitEventNavigationBar();
        createNotificationChannel(this)
        RequestAcceptSendNotification();

        UpdateWeatherInfor()
    }

    private fun UpdateWeatherInfor() {
        val requestAPI = RequestAPI.getInstance();
        Thread {
            val result = requestAPI.CallAPI(21.0285, 105.8542);

            runOnUiThread {
                val txtDegree = findViewById<TextView>(R.id.temperature)
                val tempC = result.getJSONObject("current").getDouble("temp_c")
                txtDegree.text = tempC.toString() + "℃"

                UpdateCurrentTime()

                val txtWeatherStatus = findViewById<TextView>(R.id.weatherStatus)
                val current =
                    result.getJSONObject("current").getJSONObject("condition").getString("text")
                txtWeatherStatus.text = current

                val imgWeatherIcon = findViewById<ImageView>(R.id.weatherIcon)
                val imgUrl = "https:" + result.getJSONObject("current").getJSONObject("condition")
                    .getString("icon")

                val windDir = result.getJSONObject("current").getString("wind_dir")

                val txtWindKph = findViewById<TextView>(R.id.wind_kph)
                var wind_kph = result.getJSONObject("current").getString("wind_kph")
                txtWindKph.text = "Hướng gió\n " + windDir + " - " + wind_kph + "km/h"

                val uv = result.getJSONObject("current").getDouble("uv")
                val txtUv = findViewById<TextView>(R.id.txt_uv)
                txtUv.text = "UV: " + uv

                val humidity = result.getJSONObject("current").getDouble("humidity")
                findViewById<TextView>(R.id.txt_humidity).text = "Độ ẩm\n $humidity%"

                val feelsLike = result.getJSONObject("current").getDouble("feelslike_c")
                findViewById<TextView>(R.id.txt_feel_like).text = "Môi trường: $feelsLike℃"

                Glide.with(this).load(imgUrl).into(imgWeatherIcon)
            }
        }.start()
    }

    private fun UpdateCurrentTime() {
        val textView = findViewById<TextView>(R.id.timeInfo)
        val now = java.util.Date()

        val hourMinute = SimpleDateFormat("HH:mm", Locale.getDefault()).format(now)

        val calendar = Calendar.getInstance()
        calendar.time = now
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val month = calendar.get(Calendar.MONTH) + 1
        val year = calendar.get(Calendar.YEAR)

        val weekday = calendar.get(Calendar.DAY_OF_WEEK)
        val weekdays = arrayOf("Chủ nhật", "Thứ 2", "Thứ 3", "Thứ 4", "Thứ 5", "Thứ 6", "Thứ 7")
        val weekdayStr = weekdays[weekday - 1]

        val formatted = "$hourMinute - $weekdayStr, $day tháng $month $year"

        textView.text = formatted
    }


    private fun showSettingsDialog() {
        val container = findViewById<FrameLayout>(R.id.container_dialog_setting)
        if (container.childCount == 0) {
            val dialogView = layoutInflater.inflate(R.layout.dialog_setting, container, false)
            val params = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT
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

                R.id.nav_settings -> {
                    showSettingsDialog();
                }

                R.id.nav_establish -> {
                    val changePage = Intent(this, activity_setting_scene::class.java);
                    startActivity(changePage);
                }

                R.id.nav_choose_theme -> {
                    val changePage = Intent(this, SettingTheme::class.java);
                    startActivity(changePage);
                }

                else -> {}
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }
    }


    private val NOTIFICATION_PERMISSION_REQUEST_CODE = 1001
    private fun RequestAcceptSendNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(
                    arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                    NOTIFICATION_PERMISSION_REQUEST_CODE
                )
            } else {
                sendNotification()
            }
        } else {
            sendNotification()
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == NOTIFICATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                sendNotification()
            } else {
                Toast.makeText(this, "Quyền thông báo bị từ chối", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private val CHANNEL_ID = "my_channel_id"
    private val CHANNEL_NAME = "My Channel"
    private val CHANNEL_DESCRIPTION = "Channel for app notifications"

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
                description = CHANNEL_DESCRIPTION
            }
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun sendNotification() {

        return

        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.icon_correct_location).setContentTitle("Thông báo từ Weath")
            .setContentText("test 1 2 3 1 2 3").setPriority(NotificationCompat.PRIORITY_DEFAULT)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this, android.Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                with(NotificationManagerCompat.from(this)) {
                    notify(1001, builder.build())
                }
            } else {
                requestPermissions(
                    arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                    NOTIFICATION_PERMISSION_REQUEST_CODE
                )
            }
        } else {
            try {
                with(NotificationManagerCompat.from(this)) {
                    notify(1001, builder.build())
                }
            } catch (e: Exception) {
                Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show()
            }
        }
    }


    // same enable
//    override fun onResume() {
//        super.onResume()
//        sendNotification()
//    }
}