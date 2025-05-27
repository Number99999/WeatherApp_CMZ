package com.cmzsoft.weather

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.HorizontalScrollView
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.bumptech.glide.Glide
import com.cmzsoft.weather.APICall.RequestAPI
import com.cmzsoft.weather.DatabaService.DatabaseService
import com.cmzsoft.weather.Model.LocationWeatherModel
import com.github.mikephil.charting.animation.ChartAnimator
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.renderer.LineChartRenderer
import com.github.mikephil.charting.utils.ViewPortHandler
import com.google.android.gms.location.LocationServices
import com.google.android.material.navigation.NavigationView
import org.json.JSONArray
import org.json.JSONException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.math.roundToInt

class MainActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var toggle: ActionBarDrawerToggle
    private val handler = android.os.Handler()
    private lateinit var updateRunnable: Runnable


    private var isScrollingEmoji = false
    private var isScrollingChart = false

    private var distance2Emoji = 0;

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
        startAutoUpdateWeather()
        setupLineChart()
    }

    private fun getCurLocationInDb(): LocationWeatherModel? {
        val listLocation: List<LocationWeatherModel> =
            DatabaseService.getInstance(this).locationWeatherService.getAllLocationWeather()

        if (listLocation.size == 0) return null
        return listLocation[listLocation.size - 1]
    }

    private fun UpdateWeatherInfor() {
        val requestAPI = RequestAPI.getInstance();
        Thread {
            var result = requestAPI.CallAPI(21.0285, 105.8542);
            val curLocationInDb = getCurLocationInDb();
            if (curLocationInDb != null)
                result = requestAPI.CallAPI(curLocationInDb.latitude, curLocationInDb.longitude)

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

    private fun setupLineChart() {
        Thread {
            try {
                val resultAPI =
                    RequestAPI.getInstance().GetAllDataInCurrentDay(21.0227396, 105.8369637)
                val forecastday = resultAPI
                    .getJSONObject("forecast")
                    .getJSONArray("forecastday")
                    .getJSONObject(0)
                    .getJSONArray("hour")
                runOnUiThread {
                    try {
                        setupLineChartInUI(forecastday)
                    } catch (e: JSONException) {
                        throw RuntimeException(e)
                    }
                }
            } catch (e: java.lang.Exception) {
                runOnUiThread {
                    Toast.makeText(
                        this@MainActivity,
                        e.message.toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                e.printStackTrace()
            }
        }.start()
    }

    private fun setupLineChartInUI(array: JSONArray) {
        try {
            val arrInfo = parseWeatherData(array)
            setupEmojiAndChart(arrInfo)
        } catch (e: Exception) {
            Toast.makeText(this, e.message.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    // 1. Parse data từ JSON → List<DataHourWeatherModel>
    private fun parseWeatherData(array: JSONArray): List<DataHourWeatherModel> {
        val arrInfo = mutableListOf<DataHourWeatherModel>()
        for (i in 0 until array.length()) {
            val hourObj = array.getJSONObject(i)
            arrInfo.add(
                DataHourWeatherModel(
                    hourObj.getLong("time_epoch"),
                    hourObj.getString("time"),
                    hourObj.getDouble("temp_c").roundToInt(),
                    hourObj.getDouble("temp_f").roundToInt(),
                    hourObj.getInt("is_day") == 1,
                    "https:" + hourObj.getJSONObject("condition").getString("icon")
                )
            )
        }
        return arrInfo
    }

    private fun setupEmojiAndChart(arrInfo: List<DataHourWeatherModel>) {
        val iconWidthPx = dpToPx(30)
        val iconMarginPx = dpToPx(6)
        val totalWidth = arrInfo.size * (iconWidthPx + iconMarginPx * 2)

        distance2Emoji = iconWidthPx + iconMarginPx

        setupEmojiIcons(arrInfo, iconWidthPx, iconMarginPx, totalWidth)
        setupChart(arrInfo, totalWidth)
        setupScrollSync()
    }

    private fun setupEmojiIcons(
        arrInfo: List<DataHourWeatherModel>,
        iconWidthPx: Int,
        iconMarginPx: Int,
        totalWidth: Int
    ) {
        val emojiContainer = findViewById<LinearLayout>(R.id.emojiContainer)
        emojiContainer.removeAllViews()
        for (model in arrInfo) {
            val imageView = ImageView(this)
            val params = LinearLayout.LayoutParams(iconWidthPx, iconWidthPx)
            params.setMargins(iconMarginPx, 0, iconMarginPx, 0)
            imageView.layoutParams = params
            Glide.with(this).load(model.urlIcon).into(imageView)
            emojiContainer.addView(imageView)
        }
        emojiContainer.layoutParams.width = totalWidth
        emojiContainer.requestLayout()
    }

    private fun setupChart(arrInfo: List<DataHourWeatherModel>, totalWidth: Int) {
        val entries = ArrayList<Entry>()
        arrInfo.forEachIndexed { i, model ->
            entries.add(Entry(i.toFloat(), model.tempC.toFloat()))
        }
        val dataSet = createLineDataSet(entries)
        val lineChart = findViewById<LineChart>(R.id.lineChart)
        dataSet.valueTextColor = Color.WHITE

        lineChart.requestLayout()
        lineChart.data = LineData(dataSet)
        setupChartStyle(lineChart, arrInfo)

        lineChart.invalidate()
        val emojiContainer = findViewById<LinearLayout>(R.id.emojiContainer)
        emojiContainer.post {
            val params = lineChart.layoutParams
            params.width = emojiContainer.width
            lineChart.layoutParams = params
            lineChart.data = LineData(dataSet)
            setupChartStyle(lineChart, arrInfo)
            lineChart.invalidate()
        }
    }

    private fun setupChartStyle(lineChart: LineChart, arrInfo: List<DataHourWeatherModel>) {
        lineChart.setDrawGridBackground(false)
        lineChart.description.isEnabled = false
        lineChart.legend.isEnabled = false

        val yAxis = lineChart.axisLeft
        yAxis.textColor = Color.WHITE
        yAxis.textSize = 15f
        yAxis.gridColor = Color.parseColor("#33FFFFFF")
        lineChart.axisRight.isEnabled = false
        lineChart.axisLeft.isEnabled = false

        val xAxis = lineChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.textColor = Color.WHITE
        xAxis.textSize = 12f
        xAxis.granularity = 1f
        xAxis.setLabelCount(arrInfo.size, true)
        xAxis.valueFormatter = object : ValueFormatter() {
            override fun getAxisLabel(value: Float, axis: AxisBase): String {
                val i = value.toInt()
                return if (i in arrInfo.indices) {
                    arrInfo[i].time.split(" ")[1].substring(0, 5)
                } else ""
            }
        }
        xAxis.setDrawGridLines(false)

        lineChart.renderer =
            CustomLineChartRenderer(lineChart, lineChart.animator, lineChart.viewPortHandler)
        lineChart.setExtraOffsets(10f, 20f, 10f, 10f)
        lineChart.setScaleEnabled(false)
        lineChart.setPinchZoom(false)
        lineChart.isHighlightPerTapEnabled = false
        lineChart.isDoubleTapToZoomEnabled = false
        lineChart.isDragEnabled = false

        val params = lineChart.layoutParams
        val emojiContainer = findViewById<LinearLayout>(R.id.emojiContainer)
        params.width = emojiContainer.width
        lineChart.layoutParams = params
        lineChart.requestLayout()

        val data = lineChart.data
        data?.dataSets?.forEach { set ->
            (set as? LineDataSet)?.setDrawValues(true)
            (set as? LineDataSet)?.valueFormatter = object : ValueFormatter() {
                override fun getPointLabel(entry: Entry?): String {
                    return entry?.y?.roundToInt()?.toString() + "°C"
                }
            }
        }

    }


    private fun createLineDataSet(entries: List<Entry>): LineDataSet {
        return LineDataSet(entries, "Nhiệt độ (°C)").apply {
            valueTextSize = 15f
            valueTextColor = Color.WHITE
            lineWidth = 3f
            circleRadius = 7f
            setCircleColor(Color.WHITE)
            circleHoleColor = Color.parseColor("#199AD8")
            circleHoleRadius = 5f
            color = Color.parseColor("#24D2FF")
            setDrawValues(false)
            setDrawFilled(true)
            fillDrawable = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2)
                ContextCompat.getDrawable(this@MainActivity, R.drawable.line_chart_fade)
            else null
            fillColor = Color.parseColor("#5524D2FF")
        }
    }

    private fun setupScrollSync() {
        val emojiScrollView = findViewById<HorizontalScrollView>(R.id.emoji_scroll)
        val lineChartScrollView = findViewById<HorizontalScrollView>(R.id.scroll_char)
        var isScrollingEmoji = false
        var isScrollingChart = false

        emojiScrollView.setOnScrollChangeListener { _, scrollX, _, _, _ ->
            if (!isScrollingChart) {
                isScrollingEmoji = true
                lineChartScrollView.scrollTo(scrollX, 0)
                isScrollingEmoji = false
            }
        }
        lineChartScrollView.setOnScrollChangeListener { _, scrollX, _, _, _ ->
            if (!isScrollingEmoji) {
                isScrollingChart = true
                emojiScrollView.scrollTo(scrollX, 0)
                isScrollingChart = false
            }
        }
    }

    fun dpToPx(dp: Int): Int {
        return Math.round(dp * resources.displayMetrics.density)
    }

    private fun InitEventNavigationBar() {
        drawerLayout = findViewById(R.id.main)
        navView = findViewById(R.id.nav_view)

        val toolbar: Toolbar = findViewById(R.id.toolbar)

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

    private fun startAutoUpdateWeather() {
        val now = java.util.Calendar.getInstance()
        val second = now.get(java.util.Calendar.SECOND)
        val delayToNextMinute = (60 - second) * 1000

        updateRunnable = Runnable {
            UpdateWeatherInfor()
            handler.postDelayed(updateRunnable, 60_000)
        }

        handler.postDelayed({
            UpdateWeatherInfor()
            handler.postDelayed(updateRunnable, 60_000)
        }, delayToNextMinute.toLong())
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(updateRunnable) // Dừng lặp khi Activity bị huỷ
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

    private fun getLastLocation() {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            return
        }
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                if (location != null) {
                    val latitude = location.latitude
                    val longitude = location.longitude
                    Toast.makeText(this, "Lat: $latitude, Lon: $longitude", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    Toast.makeText(this, "Không lấy được vị trí!", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Lỗi lấy vị trí!", Toast.LENGTH_SHORT).show()
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

class CustomLineChartRenderer(
    chart: LineChart,
    animator: ChartAnimator,
    viewPortHandler: ViewPortHandler
) : LineChartRenderer(chart, animator, viewPortHandler) {

    override fun drawExtras(c: Canvas) {
        super.drawExtras(c)
        val paint = Paint().apply {
            color = Color.WHITE
            strokeWidth = 1.5f
            alpha = 180
            style = Paint.Style.STROKE
            isAntiAlias = true
            pathEffect = DashPathEffect(floatArrayOf(10f, 10f), 0f)
        }
        val dataSets = mChart.lineData.dataSets
        for (dataSet in dataSets) {
            for (i in 0 until dataSet.entryCount) {
                val entry = dataSet.getEntryForIndex(i)
                val pos = mChart.getTransformer(dataSet.axisDependency)
                    .getPixelForValues(entry.x, entry.y)
                c.drawLine(
                    pos.x.toFloat(), pos.y.toFloat(),
                    pos.x.toFloat(), mViewPortHandler.contentBottom(),
                    paint
                )
            }
        }
    }
}


internal class DataHourWeatherModel(
    var timeEpoch: Long,
    var time: String,
    var tempC: Int,
    var tempF: Int,
    var isDay: Boolean,
    var urlIcon: String
)
