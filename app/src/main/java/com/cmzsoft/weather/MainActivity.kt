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
import android.graphics.Rect
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.HorizontalScrollView
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.cmzsoft.weather.APICall.RequestAPI
import com.cmzsoft.weather.CustomAdapter.TitleChartDegreeAdapter
import com.cmzsoft.weather.DatabaService.DatabaseService
import com.cmzsoft.weather.Model.DataHourWeatherModel
import com.cmzsoft.weather.Model.LocationWeatherModel
import com.cmzsoft.weather.Model.NightDayTempModel
import com.cmzsoft.weather.Model.TitleChartItemModel
import com.github.mikephil.charting.animation.ChartAnimator
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.renderer.LineChartRenderer
import com.github.mikephil.charting.utils.ViewPortHandler
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.math.roundToInt

class MainActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private val handler = android.os.Handler()
    private lateinit var updateRunnable: Runnable
    private lateinit var navContainer: FrameLayout
    private lateinit var navPanel: View

    private var distance2Emoji = 0;

    private var curLocation: String = "Hanoi"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }

        createNotificationChannel(this)
        RequestAcceptSendNotification();

        UpdateWeatherInfor()
        startAutoUpdateWeather()
        setupLineChart()
        initEventNavBar()
        setupLineChartDayNight()
    }

    private fun initEventNavBar() {
        navContainer = findViewById(R.id.frame_nav_bar)
        navPanel = findViewById(R.id.nav_panel)

        findViewById<ImageView>(R.id.img_show_nav).setOnClickListener {
            showCustomNav()
        }
        navContainer.setOnClickListener {
            hideCustomNav()
        }

        initEventBtnInNavBar()
    }

    private fun initEventBtnInNavBar() {
        val nav_establish = findViewById<LinearLayout>(R.id.nav_establish)
        nav_establish.setOnClickListener {
            val changePage = Intent(this, activity_setting_scene::class.java);
            startActivity(changePage);
        }

        findViewById<LinearLayout>(R.id.nav_notification).setOnClickListener {
            val changePage = Intent(this, ActivitySettingNotification::class.java)
            startActivity(changePage)
        }

        findViewById<LinearLayout>(R.id.nav_location).setOnClickListener {
            val changePage = Intent(this, ActivityLocationManager::class.java)
            startActivity(changePage)
        }

        findViewById<LinearLayout>(R.id.nav_setting).setOnClickListener {
            showSettingsDialog()
            hideCustomNav()
        }

        findViewById<LinearLayout>(R.id.nav_remove_ads).setOnClickListener {
            hideCustomNav()
            showRemoveAdsDialog()
        }

        findViewById<LinearLayout>(R.id.nav_rate_us).setOnClickListener {
            showVoteApp()
            hideCustomNav()
        }

        findViewById<LinearLayout>(R.id.nav_feedback).setOnClickListener {
            val changePage = Intent(this, ActivityFeedback::class.java)
            startActivity(changePage)
        }

        findViewById<LinearLayout>(R.id.nav_layout_customize).setOnClickListener {
            val changePage = Intent(this, ActivityCustomLayout::class.java)
            startActivity(changePage)
        }
    }

    private fun getCurLocationInDb(): LocationWeatherModel? {
        val listLocation: List<LocationWeatherModel> =
            DatabaseService.getInstance(this).locationWeatherService.getAllLocationWeather()

        if (listLocation.size == 0) return null
        return listLocation[listLocation.size - 1]
    }

    private fun UpdateWeatherInfor() {
        lifecycleScope.launch {
            val requestAPI = RequestAPI.getInstance()
            var location = "21.0285, 105.8542"
            val curLocationInDb = withContext(Dispatchers.IO) {
                getCurLocationInDb()
            }

            if (curLocationInDb != null) {
                location =
                    curLocationInDb.latitude.toString() + "," + curLocationInDb.longitude.toString()
            }

            val result = withContext(Dispatchers.IO) {
                requestAPI.CallAPI(location)
            }

            updateDataOnMainInfo(result)
        }
    }

    private fun updateDataOnMainInfo(result: JSONObject) {
        val txtDegree = findViewById<TextView>(R.id.temperature)
        val tempC = result.getJSONObject("current").getDouble("temp_c")
        txtDegree.text = tempC.roundToInt().toString() + "℃"

        UpdateCurrentTime()

        val txtWeatherStatus = findViewById<TextView>(R.id.weatherStatus)
        val current = result.getJSONObject("current").getJSONObject("condition").getString("text")
        txtWeatherStatus.text = current

        val imgWeatherIcon = findViewById<ImageView>(R.id.weatherIcon)
        val imgUrl =
            "https:" + result.getJSONObject("current").getJSONObject("condition").getString("icon")

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

    private fun setupLineChartDayNight() {
        lifecycleScope.launch {
            val requestAPI = RequestAPI.getInstance()
            val dataInWeek = withContext(Dispatchers.IO) {
                requestAPI.GetTempInAWeek(curLocation)
            }
            initChartDayNightTemp(dataInWeek)
        }
    }

    private fun initChartDayNightTemp(data: JSONObject) {
        // model NightDayTempModel

        val forecastday = data.getJSONObject("forecast").getJSONArray("forecastday")
        val listData = mutableListOf<NightDayTempModel>();
        for (i in 0 until forecastday.length()) {
            val dayObj = forecastday.getJSONObject(i)
            val dayDetail = dayObj.getJSONObject("day")
            val condition = dayDetail.getJSONObject("condition")
            val astro = dayObj.getJSONObject("astro")
            val hourArr = dayObj.getJSONArray("hour")

            // Lấy giờ mặt trời mọc/lặn để phân chia
            fun parseHour(h: String): Int {
                val sdf = java.text.SimpleDateFormat("hh:mm a", java.util.Locale.ENGLISH)
                return try {
                    val d = sdf.parse(h)
                    val cal = java.util.Calendar.getInstance()
                    cal.time = d
                    cal.get(java.util.Calendar.HOUR_OF_DAY)
                } catch (e: Exception) {
                    0
                }
            }

            val sunrise = parseHour(astro.getString("sunrise"))      // ví dụ: 5
            val sunset = parseHour(astro.getString("sunset"))        // ví dụ: 18

            val dayHours = mutableListOf<JSONObject>()
            val nightHours = mutableListOf<JSONObject>()
            for (j in 0 until hourArr.length()) {
                val hourObj = hourArr.getJSONObject(j)
                val hour = hourObj.getString("time").split(" ")[1].split(":")[0].toInt()
                if (hour in sunrise until sunset) {
                    dayHours.add(hourObj)
                } else {
                    nightHours.add(hourObj)
                }
            }

            val rainDay = dayHours.maxOfOrNull { it.optInt("chance_of_rain", 0) } ?: 0
            val rainNight = nightHours.maxOfOrNull { it.optInt("chance_of_rain", 0) } ?: 0
            val tempDay = dayHours.maxOfOrNull { it.optDouble("temp_c", 0.0) }?.toFloat() ?: 0f
            val tempNight = nightHours.maxOfOrNull { it.optDouble("temp_c", 0.0) }?.toFloat() ?: 0f
            val urlIcon = "https:" + condition.getString("icon")

            val m = NightDayTempModel(
                date = dayObj.getString("date"),
                rainDay = rainDay,
                rainNight = rainNight,
                tempDay = tempDay,
                tempNight = tempNight,
                urlIcon = urlIcon
            )
            listData.add(m)
        }


        this.setUpDayTempChart(listData)
        this.setUpNightTempChart(listData)
    }

    private fun setUpDayTempChart(dataModel: List<NightDayTempModel>) {
        val lineChart = findViewById<LineChart>(R.id.chart_temp_at_day)
        val entries = dataModel.mapIndexed { idx, item ->
            Entry(idx.toFloat(), item.tempDay.toFloat())
        }

        val dataSetDay = LineDataSet(entries, "").apply {
            color = Color.parseColor("#32a0e9")
            setDrawCircles(true)
            setCircleColor(Color.parseColor("#32a0e9"))
            setDrawValues(false)
            lineWidth = 2.5f
            circleRadius = 5f
            circleHoleRadius = 3f
            setDrawFilled(true)
            setDrawHighlightIndicators(false)
            fillDrawable = ContextCompat.getDrawable(baseContext, R.drawable.chart_gradient_blue)
        }

        lineChart.apply {
            data = LineData(dataSetDay)
            description.isEnabled = false
            legend.isEnabled = false
            axisLeft.isEnabled = false
            axisRight.isEnabled = false
            xAxis.isEnabled = false
            setDrawGridBackground(false)
            setDrawBorders(false)
            setTouchEnabled(false)
            setPinchZoom(false)
            setScaleEnabled(false)
            invalidate()
        }
    }

    private fun setUpNightTempChart(dataModel: List<NightDayTempModel>) {
        val lineChart = findViewById<LineChart>(R.id.chart_temp_at_night)
        val entries = dataModel.mapIndexed { idx, item ->
            Entry(idx.toFloat(), item.tempNight.toFloat())
        }

        val dataSetDay = LineDataSet(entries, "").apply {
            color = Color.parseColor("#cc496c")
            setDrawCircles(true)
            setCircleColor(Color.parseColor("#cc496c"))
            setDrawValues(false)
            lineWidth = 2.5f
            circleRadius = 5f
            circleHoleRadius = 3f
            setDrawFilled(true)
            setDrawHighlightIndicators(false)
            fillDrawable = ContextCompat.getDrawable(baseContext, R.drawable.char_gradient_red)
        }

        lineChart.apply {
            data = LineData(dataSetDay)
            description.isEnabled = false
            legend.isEnabled = false
            axisLeft.isEnabled = false
            axisRight.isEnabled = false
            xAxis.isEnabled = false
            setDrawGridBackground(false)
            setDrawBorders(false)
            setTouchEnabled(false)
            setPinchZoom(false)
            setScaleEnabled(false)
            invalidate()
        }
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

    private fun showRemoveAdsDialog() {
        val container = findViewById<FrameLayout>(R.id.container_dialog_remove_ads)
        if (container.childCount == 0) {
            val dialogView = layoutInflater.inflate(R.layout.dialog_remove_ads, container, false)
            val params = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT
            )
            params.gravity = Gravity.CENTER
            dialogView.layoutParams = params

            container.addView(dialogView)
            dialogView.findViewById<ImageView>(R.id.btn_back)?.setOnClickListener {
                container.visibility = View.GONE
                container.removeAllViews()
            }
            dialogView.findViewById<Button>(R.id.btn_accept_buy)?.setOnClickListener {
                container.visibility = View.GONE
                container.removeAllViews()
            }
            dialogView.findViewById<Button>(R.id.btn_cancel)?.setOnClickListener {
                container.visibility = View.GONE
                container.removeAllViews()
            }
        }
        container.visibility = View.VISIBLE
    }

    private fun showVoteApp() {
        val container = findViewById<FrameLayout>(R.id.container_vote_app)
        if (container.childCount == 0) {
            val dialogView = layoutInflater.inflate(R.layout.dialog_vote_app, container, false)
            val params = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT
            )
            params.gravity = Gravity.CENTER

            dialogView.layoutParams = params

            container.addView(dialogView)
            dialogView.findViewById<ImageView>(R.id.btn_back)?.setOnClickListener {
                container.visibility = View.GONE
                container.removeAllViews()
            }
            dialogView.findViewById<Button>(R.id.btn_accept_vote)?.setOnClickListener {
                container.visibility = View.GONE
                container.removeAllViews()
            }
        }
        container.visibility = View.VISIBLE
    }

    private fun setupLineChart() {
        findViewById<TextView>(R.id.txt_view_detail).setOnClickListener {
            val change = Intent(this, ActivityWeatherPerHour::class.java)
            startActivity(change)
        }

        lifecycleScope.launch {
            try {
                val resultAPI = withContext(Dispatchers.IO) {
                    RequestAPI.getInstance().GetAllDataInCurrentDay(21.0227396, 105.8369637)
                }

                val forecastday1 =
                    resultAPI.getJSONObject("forecast").getJSONArray("forecastday").getJSONObject(0)
                        .getJSONArray("hour")
                val forecastday2 =
                    resultAPI.getJSONObject("forecast").getJSONArray("forecastday").getJSONObject(1)
                        .getJSONArray("hour")

                for (i in 0 until forecastday2.length()) {
                    forecastday1.put(forecastday2.get(i))
                }

                try {
                    setupLineChartInUI(forecastday1)
                } catch (e: JSONException) {
                    throw RuntimeException(e)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

    }

    private fun setupLineChartInUI(array: JSONArray) {
        try {
            val arrInfo = parseWeatherData(array)
            setupTitleChartDegree(arrInfo)

            setupChart(arrInfo)
            setupScrollSync()

        } catch (e: Exception) {
            Toast.makeText(this, e.message.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    private fun parseWeatherData(array: JSONArray): List<DataHourWeatherModel> {
        val arrInfo = mutableListOf<DataHourWeatherModel>()

        val calendar = Calendar.getInstance()
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)

        val nowEpoch = calendar.timeInMillis / 1000   // epoch giây làm tròn xuống giờ
        val next24hEpoch = nowEpoch + 23 * 3600        // 24 giờ sau

        for (i in 0 until array.length()) {
            val hourObj = array.getJSONObject(i)
            val timeEpoch = hourObj.getLong("time_epoch")
            if (timeEpoch in nowEpoch..next24hEpoch) {
                arrInfo.add(
                    DataHourWeatherModel(
                        timeEpoch,
                        hourObj.getString("time"),
                        hourObj.getDouble("temp_c").roundToInt(),
                        hourObj.getDouble("temp_f").roundToInt(),
                        hourObj.getInt("is_day") == 1,
                        "https:" + hourObj.getJSONObject("condition").getString("icon")
                    )
                )
            }
        }

        return arrInfo
    }

    private fun setupChart(arrInfo: List<DataHourWeatherModel>) {
        val entries = ArrayList<Entry>()
        arrInfo.forEachIndexed { i, model ->
            entries.add(Entry(i.toFloat(), model.tempC.toFloat()))
        }
        val dataSet = createLineDataSet(entries)
        val lineChart = findViewById<LineChart>(R.id.lineChart)
        dataSet.valueTextColor = Color.WHITE
        dataSet.mode = LineDataSet.Mode.CUBIC_BEZIER
        dataSet.setDrawCircles(false)
        dataSet.color = Color.parseColor("#f6ce1e")

        lineChart.requestLayout()
        lineChart.data = LineData(dataSet)
        setupChartStyle(lineChart, arrInfo)

        lineChart.invalidate()
    }

    private fun setupChartStyle(lineChart: LineChart, arrInfo: List<DataHourWeatherModel>) {
        lineChart.setDrawGridBackground(false)
        lineChart.description.isEnabled = false
        lineChart.legend.isEnabled = false
        lineChart.axisRight.isEnabled = false
        lineChart.axisRight.setDrawGridLines(false)

        val yAxis = lineChart.axisLeft
        yAxis.textColor = Color.WHITE
        yAxis.textSize = 15f
        yAxis.setDrawGridLines(false)

        yAxis.isEnabled = false

        val xAxis = lineChart.xAxis
        xAxis.setDrawLabels(false)
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
            fillDrawable =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) ContextCompat.getDrawable(
                    this@MainActivity, R.drawable.line_chart_fade
                )
                else null
            fillColor = Color.parseColor("#5524D2FF")
        }
    }

    private fun setupScrollSync() {
        val lineChartScrollView = findViewById<HorizontalScrollView>(R.id.scroll_char)
        val recyclerView = findViewById<RecyclerView>(R.id.rc_title_chart)

        var isScrollingChart = false
        var isScrollingRecycler = false

        lineChartScrollView.setOnScrollChangeListener { _, scrollX, _, _, _ ->
            if (!isScrollingRecycler) {
                isScrollingChart = true
                val rvScrollX = recyclerView.computeHorizontalScrollOffset()
                val diff = scrollX - rvScrollX
                if (diff != 0) {
                    recyclerView.scrollBy(diff, 0)
                }
                isScrollingChart = false
            }
        }

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(rv: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(rv, dx, dy)
                if (!isScrollingChart) {
                    isScrollingRecycler = true
                    val scrollX = recyclerView.computeHorizontalScrollOffset()
                    lineChartScrollView.scrollTo(scrollX, 0)
                    isScrollingRecycler = false
                }
            }
        })
    }

    fun dpToPx(dp: Int): Int {
        return Math.round(dp * resources.displayMetrics.density)
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

    private val CHANNEL_ID = "cmz.soft.weather"
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
                this, Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            return
        }
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            if (location != null) {
                val latitude = location.latitude
                val longitude = location.longitude
                Toast.makeText(this, "Lat: $latitude, Lon: $longitude", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Không lấy được vị trí!", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
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

    private fun showCustomNav() {
        navContainer.visibility = View.VISIBLE
        navPanel.post {
            navPanel.translationX = -navPanel.width.toFloat()
            navPanel.animate().translationX(0f).setDuration(300).start()
        }
    }

    private fun hideCustomNav() {
        navPanel.animate().translationX(-navPanel.width.toFloat()).setDuration(300).withEndAction {
            navContainer.visibility = View.GONE
        }.start()
    }

    private fun setupTitleChartDegree(arr: List<DataHourWeatherModel>) {
        val rc_view = findViewById<RecyclerView>(R.id.rc_title_chart)
        val params = rc_view.layoutParams as ViewGroup.MarginLayoutParams
//        params.width = dpToPx(1400)
//        rc_view.layoutParams = params
        val listData = mutableListOf<TitleChartItemModel>()
        for (item in arr) {
            listData.add(
                TitleChartItemModel(
                    item.time.substring(item.time.length - 5), null, item.urlIcon, item.tempC
                )
            )
        }

        val adapter = TitleChartDegreeAdapter(listData)
        rc_view.adapter = adapter
        rc_view.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        val recyclerViewWidth = 1400
        val itemCount = listData.size

        val spacing = 10

        rc_view.addItemDecoration(object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(
                outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State
            ) {
                val position = parent.getChildAdapterPosition(view)
                if (position == RecyclerView.NO_POSITION) return

                outRect.left = if (position == 0) -7 else spacing
                outRect.right = if (position == itemCount - 1) 8 else 0
            }
        })
    }

// same enable
//    override fun onResume() {
//        super.onResume()
//        sendNotification()
//    }
}

class CustomLineChartRenderer(
    chart: LineChart, animator: ChartAnimator, viewPortHandler: ViewPortHandler
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
                    pos.x.toFloat(),
                    pos.y.toFloat(),
                    pos.x.toFloat(),
                    mViewPortHandler.contentBottom(),
                    paint
                )
            }
        }
    }
}


