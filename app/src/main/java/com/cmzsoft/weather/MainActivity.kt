package com.cmzsoft.weather

import XAxisRendererTwoLine
import android.Manifest
import android.animation.ValueAnimator
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.HorizontalScrollView
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.toColorInt
import androidx.core.view.get
import androidx.core.view.isEmpty
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cmzsoft.weather.APICall.RequestAPI
import com.cmzsoft.weather.CustomAdapter.TitleChartDegreeAdapter
import com.cmzsoft.weather.CustomView.SunArcView
import com.cmzsoft.weather.FrameWork.Data.LocalStorageManager
import com.cmzsoft.weather.Manager.AdManager
import com.cmzsoft.weather.Model.DataHourWeatherModel
import com.cmzsoft.weather.Model.DataWeatherPerHourModel
import com.cmzsoft.weather.Model.FakeGlobal
import com.cmzsoft.weather.Model.LocationWeatherModel
import com.cmzsoft.weather.Model.NavMenuModel
import com.cmzsoft.weather.Model.NightDayTempModel
import com.cmzsoft.weather.Model.Object.KeysStorage
import com.cmzsoft.weather.Model.TitleChartItemModel
import com.cmzsoft.weather.RendererChart.CustomLineChartRenderer
import com.cmzsoft.weather.RendererChart.RainfallRendererBarChart
import com.cmzsoft.weather.Service.DatabaseService
import com.cmzsoft.weather.Service.Interface.GetCurrentLocationCallback
import com.cmzsoft.weather.Service.LocationService
import com.cmzsoft.weather.Utils.WeatherUtil
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.lang.Integer.parseInt
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.math.roundToInt

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    lateinit var adManager: AdManager
    private val handler = android.os.Handler()
    private lateinit var updateRunnable: Runnable
    private lateinit var updateRunnableTime: Runnable
    private lateinit var navContainer: FrameLayout
    private lateinit var navPanel: View
    private var updateWeatherJob: Job? = null;
    private var updateCurTimeJob: Job? = null
    private var curLocation: LatLng = LatLng(21.0, 105.875)
    private var isFirstResume = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        if (intent.getBooleanExtra("FROM_REQUEST_LOCATION", false)) {
            this.showSettingsDialog();
        }

        initValiable()

        createNotificationChannel(this)
        RequestAcceptSendNotification();

        findViewById<ImageView>(R.id.add_location).setOnClickListener {
            val intent = Intent(this, ActivityChooseLocation::class.java);
            startActivity(intent)
        }
        this.getCurLocation();
        startAutoUpdateWeather()
//        test()
//        drawSunArcView()
    }

    private fun drawSunArcView() {
        val sunArcView = findViewById<SunArcView>(R.id.sunArcView)

        val animator = ValueAnimator.ofFloat(0f, 1f)
        animator.duration = 5000
        animator.addUpdateListener {
            val value = it.animatedValue as Float
            sunArcView.setSunPosition(value)
        }
        animator.start()
    }

    private fun initValiable() {
        val context: Context = this
        adManager = AdManager(context)
        showInterAds()
    }

    private fun onInitedLocation() {
        UpdateWeatherInfor()
        setupLineChart()
        initEventNavBar()
        setupLineChartDayNight()
        eventScrollMain()
        setupHeaderWithStatusBar()
        setupDataRainfallChart()
    }

    private fun setupDataRainfallChart() {
        lifecycleScope.launch {
            val resultAPI = withContext(Dispatchers.IO) {
                RequestAPI.getInstance().GetPOPNextWeek(
                    FakeGlobal.getInstance().curLocation.latitude,
                    FakeGlobal.getInstance().curLocation.longitude
                )
            }
            val arrTime = resultAPI.getJSONObject("hourly").getJSONArray("time")
            val arrRainSum =
                resultAPI.getJSONObject("hourly").getJSONArray("precipitation_probability")

            val barChart = findViewById<BarChart>(R.id.barchart_rainfall)
            barChart.setScaleEnabled(false)
            barChart.setPinchZoom(false)
            barChart.setDragEnabled(false)
            barChart.isDoubleTapToZoomEnabled = false
            barChart.animateY(1000)
            barChart.setDrawGridBackground(false)
            barChart.description.isEnabled = false
            barChart.legend.isEnabled = false
            barChart.isHighlightPerTapEnabled = false
            barChart.isHighlightPerDragEnabled = false

            val listData = mutableListOf<BarEntry>()
            val listTime = mutableListOf<String>()
            val listRainPop = mutableListOf<Number>()
            for (dayIndex in 0 until 7) {
                var maxRain = 0.0f
                for (hourIndex in 0 until 24) {
                    val index = dayIndex * 24 + hourIndex
                    val value = arrRainSum.get(index)
                    if (value is Number) {
                        val rainValue = value.toFloat()
                        if (rainValue > maxRain) {
                            maxRain = rainValue
                        }
                    }
                }
                listRainPop.add(maxRain)
                val fullStr = arrTime.getString(dayIndex * 24)
                val dayOfWeek = WeatherUtil.getDayOfWeek(fullStr.substring(0, 10))
                val dateShort = "${fullStr.substring(8, 10)}/${fullStr.substring(5, 7)}"
                listTime.add("$dayOfWeek\n$dateShort")
                listData.add(BarEntry(dayIndex.toFloat(), maxRain))
            }

            val barDataSet = BarDataSet(listData, "")
            barDataSet.color = resources.getColor(R.color.blue_600, null)
            barDataSet.barShadowColor = Color.TRANSPARENT
            barDataSet.valueTextColor = Color.WHITE
            barDataSet.setDrawValues(true)
            barDataSet.valueTextSize = 12f
            barDataSet.valueFormatter = object : ValueFormatter() {
                override fun getBarLabel(barEntry: BarEntry?): String {
                    val index = barEntry?.x?.toInt() ?: return ""
                    return listRainPop[index].toInt().toString().plus("%")
                }
            }

            val barData = BarData(barDataSet)
            barChart.data = barData
            barData.barWidth = 0.5f
            barChart.setDrawBorders(true)
            barChart.setBorderWidth(0.1f)
            barChart.notifyDataSetChanged()

            val xAxis: XAxis = barChart.xAxis
            xAxis.granularity = 1f
            xAxis.setDrawGridLines(false)
            xAxis.setDrawAxisLine(false)
            xAxis.setDrawLabels(true)
            xAxis.yOffset = 30f
            xAxis.valueFormatter = IndexAxisValueFormatter(listTime)
            xAxis.textColor = Color.WHITE
            xAxis.position = XAxis.XAxisPosition.BOTTOM

            val yAxis = barChart.axisLeft
            yAxis.axisMinimum = 0f
            yAxis.mAxisMaximum = 100f
            yAxis.setDrawLabels(false)
            yAxis.setDrawGridLines(false)
            yAxis.setDrawAxisLine(false)

            barChart.axisRight.isEnabled = false
            barChart.animate()
            val customRenderer =
                RainfallRendererBarChart(barChart, barChart.animator, barChart.viewPortHandler)
            customRenderer.initBuffers()
            barChart.renderer = customRenderer
            barChart.setXAxisRenderer(
                XAxisRendererTwoLine(
                    barChart.viewPortHandler,
                    barChart.xAxis,
                    barChart.getTransformer(YAxis.AxisDependency.RIGHT),
                    listTime.toList()
                )
            )
            barChart.invalidate()
        }
    }

    private fun eventScrollMain() {
        val scrollView = findViewById<ScrollView>(R.id.mainScroll)
        val headerView = findViewById<RelativeLayout>(R.id.header_main)
        scrollView.setOnScrollChangeListener { _, _, scrollY, _, _ ->
            val headerHeight = headerView.height
            if (scrollY >= headerHeight) {
                headerView.setBackgroundColor("#B3000000".toColorInt())
            } else {
                headerView.setBackgroundColor("#00000000".toColorInt())
            }
        }
    }

    private fun setupHeaderWithStatusBar() {
        val heightBar = getStatusBarHeight()
        val img_statusbar = findViewById<ImageView>(R.id.img_statusbar)
        val imgLayout = img_statusbar.layoutParams
        imgLayout.height = heightBar
        img_statusbar.layoutParams = imgLayout
    }

    fun getStatusBarHeight(): Int {
        var result = 0
        val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            result = resources.getDimensionPixelSize(resourceId)
        }
        return result
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
            val changePage = Intent(this, ActivityEstablish::class.java);
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

        findViewById<LinearLayout>(R.id.nav_privacy_policy).setOnClickListener {
            val changePage = Intent(this, ActivityPolicy::class.java)
            startActivity(changePage)
        }

        findViewById<LinearLayout>(R.id.nav_share).setOnClickListener {
            shareAppLink()
        }
    }

    private fun shareAppLink() {
        val appLink = "https://play.google.com/store/apps/details?id=${packageName}"

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, appLink)
        }

        startActivity(Intent.createChooser(intent, "Share app"))
    }

    private fun getCurLocation() {
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            this@MainActivity.onInitedLocation();
            return
        }
        if (!FakeGlobal.getInstance().isFirstLoadActivity) return;
        FakeGlobal.getInstance().isFirstLoadActivity = false;
        LocationService.getCurrentLocation(object : GetCurrentLocationCallback {
            override fun onLocationReceived(add: LocationWeatherModel) {
                val defaultAdd =
                    DatabaseService.getInstance(this@MainActivity).locationWeatherService.getDefaultLocationWeather();
                if (defaultAdd != null) {
                    this@MainActivity.curLocation =
                        LatLng(defaultAdd.latitude, defaultAdd.longitude);
                    FakeGlobal.getInstance().curLocation = defaultAdd;
                } else {
                    this@MainActivity.curLocation = LatLng(add.latitude, add.longitude);
                    FakeGlobal.getInstance().curLocation = add;
                }

                this@MainActivity.onInitedLocation();
            }

            override fun onError(exception: Exception) {
                Toast.makeText(
                    this@MainActivity, "Error: ${exception.message}", Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun UpdateWeatherInfor() {
        updateWeatherJob?.cancel();
        updateWeatherJob = lifecycleScope.launch {
            val requestAPI = RequestAPI.getInstance()
            val result = withContext(Dispatchers.IO) {
                requestAPI.GetCurrentWeather(
                    FakeGlobal.getInstance().curLocation.latitude,
                    FakeGlobal.getInstance().curLocation.longitude
                )
            }
            updateDataOnMainInfo(result)
            setIsRainInNextTwoHours()
        }
    }

    private fun updateDataOnMainInfo(result: JSONObject) {
        try {
            val curHour = parseInt(WeatherUtil.CurrentTime().substring(11, 13))
            val txtDegree = findViewById<TextView>(R.id.temperature)
            val currentWeather = result.getJSONObject("current_weather")
            val tempC = currentWeather.getDouble("temperature")
            txtDegree.text = tempC.roundToInt().toString()

            UpdateCurrentTime()
            val txtWeatherStatus = findViewById<TextView>(R.id.weatherStatus)
            val current = WeatherUtil.getWeatherStatus(currentWeather.getInt("weathercode"))
            txtWeatherStatus.text = current

            val imgWeatherIcon = findViewById<ImageView>(R.id.weatherIcon)
            val iconName = WeatherUtil.getWeatherIconName(
                currentWeather.getInt("weathercode"), currentWeather.getInt("is_day") == 1
            )
            val resId = resources.getIdentifier(iconName, "drawable", packageName)
            if (resId != 0) {
                imgWeatherIcon.setImageResource(resId)
            } else {
                imgWeatherIcon.setImageResource(R.drawable.icon_weather_1)
            }

            val windDir =
                WeatherUtil.degreeToShortDirection(currentWeather.getDouble("winddirection"))

            val txtWindKph = findViewById<TextView>(R.id.wind_kph)
            var wind_kph = currentWeather.getDouble("windspeed")
            txtWindKph.text = "Hướng gió\n" + windDir + " - " + wind_kph + "km/h"

            val uv = result.getJSONObject("hourly").getJSONArray("uv_index").get(curHour)
            val txtUv = findViewById<TextView>(R.id.txt_uv)
            txtUv.text = "UV:\n" + uv

            val humidity =
                result.getJSONObject("hourly").getJSONArray("relative_humidity_2m").get(curHour)
            findViewById<TextView>(R.id.txt_humidity).text = "Độ ẩm\n$humidity%"

            val feelsLike =
                result.getJSONObject("hourly").getJSONArray("apparent_temperature").get(curHour)
            findViewById<TextView>(R.id.txt_feel_like).text = "Môi trường:\n$feelsLike℃"

            if (FakeGlobal.getInstance()?.curLocation?.name != null) {
                val txtCity = findViewById<TextView>(R.id.cityName);
                txtCity.text = FakeGlobal.getInstance().curLocation.name;
            }
        } catch (e: Exception) {
            Toast.makeText(this@MainActivity, e.message.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupLineChartDayNight() {
        lifecycleScope.launch {
            val requestAPI = RequestAPI.getInstance()
            val dataInWeek = withContext(Dispatchers.IO) {
                requestAPI.GetTempInAWeek(curLocation.latitude, curLocation.longitude)
            }
            initChartDayNightTemp(dataInWeek)
        }
    }

    private fun initChartDayNightTemp(data: JSONObject) {
        val hourly = data.getJSONObject("hourly")
        val timeArr = hourly.getJSONArray("time")
        val tempArr = hourly.getJSONArray("temperature_2m")
        val weatherCodeArr = hourly.getJSONArray("weathercode")
        val rainProbArr = hourly.getJSONArray("precipitation_probability")

        // Map từng ngày: ngày -> list các chỉ số giờ trong ngày đó
        val dailyMap = mutableMapOf<String, MutableList<Int>>() // ngày (yyyy-MM-dd) -> list chỉ số

        for (i in 0 until timeArr.length()) {
            val timeStr = timeArr.getString(i)         // "2024-06-09T13:00"
            val dateStr = timeStr.substring(0, 10)     // "2024-06-09"
            dailyMap.getOrPut(dateStr) { mutableListOf() }.add(i)
        }

        val listData = mutableListOf<NightDayTempModel>()

        for ((date, idxList) in dailyMap) {
            val dayHours = mutableListOf<Int>()
            val nightHours = mutableListOf<Int>()
            // Giả sử: 6h (06:00) đến trước 18h (18:00) là ban ngày, còn lại là ban đêm
            for (idx in idxList) {
                val hour = timeArr.getString(idx).substring(11, 13).toInt()
                if (hour in 6 until 19) {
                    dayHours.add(idx)
                } else {
                    nightHours.add(idx)
                }
            }

            val rainDay = dayHours.maxOfOrNull { rainProbArr.optDouble(it, 0.0) }?.toInt() ?: 0
            val rainNight = nightHours.maxOfOrNull { rainProbArr.optDouble(it, 0.0) }?.toInt() ?: 0
            val tempDay = dayHours.maxOfOrNull { tempArr.optDouble(it, 0.0) }?.toFloat() ?: 0f
            val tempNight = nightHours.maxOfOrNull { tempArr.optDouble(it, 0.0) }?.toFloat() ?: 0f
            val iconID = dayHours.maxByOrNull { tempArr.optDouble(it, 0.0) }
                ?.let { weatherCodeArr.optInt(it, 0) } ?: 0

            val m = NightDayTempModel(
                date = date,
                rainDay = rainDay,
                rainNight = rainNight,
                tempDay = tempDay,
                tempNight = tempNight,
                iconID = iconID
            )
            listData.add(m)
        }

        this.setUpDayTempChart(listData)
        this.setUpNightTempChart(listData)
        this.setupRecycleNight(listData)
        this.setupRecycleDay(listData)
    }

    fun showInterAds() {
//        println("show inter ads")
        adManager.showInterstitialAdIfEligible(
            this,
            minIntervalMillis = 60_000L,
            adTag = "Home",
            onAdClosed = {
//                dismissAdLoadingDialog()
            },
            onAdSkipped = {
//                dismissAdLoadingDialog()
            },
            onAdFailedToShow = {
//                analyticsHelper.logShowInterstitialFailed(ScreenName.HOME)
//                dismissAdLoadingDialog()
            },
            onAdStartShowing = {
//                ALog.d("themd", "onAdStartShowing")
//                showAdLoadingDialog()
            },
            onAdImpression = {
//                analyticsHelper.logShowInterstitial(ScreenName.HOME)
//                analyticsHelper.logAdImpression(
//                    "interstitial", BuildConfig.INTERSTITIAL_AD_UNIT_ID
//                )
            })
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

    private fun setupRecycleDay(dataModel: List<NightDayTempModel>) {
        val containerView = findViewById<LinearLayout>(R.id.container_day)
        for (i in 0 until containerView.childCount) {
            if (dataModel.size <= i) continue;
            val parentView = containerView.getChildAt(i)
            if (parentView is ViewGroup) {
                val textView = parentView.getChildAt(0)
                if (textView is TextView) {
                    val d = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    val calendar =
                        Calendar.getInstance().apply { time = d.parse(dataModel[i].date) }

                    val day = calendar.get(Calendar.DAY_OF_MONTH)
                    val month = calendar.get(Calendar.MONTH) + 1
                    val dayOfWeek = WeatherUtil.getDayOfWeek(dataModel[i].date)

                    textView.text = "$dayOfWeek\n$day/$month"
                }

                if (parentView.getChildAt(1) is ImageView) {
                    val iconName = WeatherUtil.getWeatherIconName(
                        dataModel.get(i).iconID, true
                    )
                    val resId = resources.getIdentifier(iconName, "drawable", packageName)
                    if (resId != 0) {
                        (parentView.getChildAt(1) as ImageView).setImageResource(resId)
                    } else {
                        // fallback icon nếu không tìm thấy
                        (parentView.getChildAt(1) as ImageView).setImageResource(R.drawable.icon_weather_1)
                    }
                }

                if (parentView.getChildAt(2) is ViewGroup) {
                    val t = (parentView.getChildAt(2) as ViewGroup).getChildAt(1);
                    if (t is TextView) t.text = (dataModel[i].rainDay).toString() + "%";
                }


                if (parentView.getChildAt(3) is ViewGroup) {
                    val t = (parentView.getChildAt(3) as ViewGroup).getChildAt(1);
                    if (t is TextView) t.text =
                        (dataModel[i].tempDay.roundToInt()).toString() + "°C";
                }
            }
        }
    }

    private fun setupRecycleNight(dataModel: List<NightDayTempModel>) {
        val containerView = findViewById<LinearLayout>(R.id.container_night)
        for (i in 0 until containerView.childCount) {
            if (i >= dataModel.size) continue;
            val parentView = containerView.getChildAt(i)
            if (parentView is ViewGroup) {
                if (parentView.getChildAt(0) is ViewGroup) {
                    val t = (parentView.getChildAt(0) as ViewGroup).getChildAt(1)
                    if (t is TextView) {
                        t.text = dataModel[i].tempNight.roundToInt().toString() + "°C";
                    }
                }

                if (parentView.getChildAt(1) is ImageView) {
                    val iconName = WeatherUtil.getWeatherIconName(
                        dataModel.get(i).iconID, false
                    )
                    val resId = resources.getIdentifier(iconName, "drawable", packageName)
                    if (resId != 0) {
                        (parentView.getChildAt(1) as ImageView).setImageResource(resId)
                    } else {
                        (parentView.getChildAt(1) as ImageView).setImageResource(R.drawable.icon_weather_1)
                    }
                }

                if (parentView.getChildAt(2) is ViewGroup) {
                    val t = (parentView.getChildAt(2) as ViewGroup).getChildAt(1)
                    if (t is TextView) {
                        t.text = dataModel[i].rainNight.toString() + "%";
                    }
                }
            }
        }
    }

    private fun UpdateCurrentTime() {
        val textView = findViewById<TextView>(R.id.timeInfo)
        val timeConverted = WeatherUtil.CurrentTime()
        try {
            val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
            val calendar = Calendar.getInstance()
            calendar.time = formatter.parse(timeConverted);

            val day = calendar.get(Calendar.DAY_OF_MONTH)
            val month = calendar.get(Calendar.MONTH) + 1
            val year = calendar.get(Calendar.YEAR)

            val weekday = calendar.get(Calendar.DAY_OF_WEEK)

            Log.d("DATE", "Ngày: $day/$month/$year - Thứ: $weekday $timeConverted")
            val weekdays = arrayOf("Chủ nhật", "Thứ 2", "Thứ 3", "Thứ 4", "Thứ 5", "Thứ 6", "Thứ 7")
            val weekdayStr = weekdays[weekday - 1]
            val formatted = "${timeConverted.substring(11)} - $weekdayStr, $day tháng $month $year"
            textView.text = formatted
        } catch (e: ParseException) {
            e.printStackTrace()
        }
    }

    private fun showSettingsDialog() {
        val container = findViewById<FrameLayout>(R.id.container_dialog_setting)
        if (container.isEmpty()) {
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
        setupAdapterSettingDialog(R.id.fake_spinner_temp, listOf("C", "F"), R.id.txt_temp_model)
        setupAdapterSettingDialog(
            R.id.fake_spinner_rain, listOf("mm", "cm", "in"), R.id.txt_rainfall_model
        )
        setupAdapterSettingDialog(
            R.id.fake_spinner_visibility, listOf("km", "Dặm"), R.id.txt_visibilyty_model
        )
        setupAdapterSettingDialog(
            R.id.fake_spinner_wind_speed,
            listOf("mph", "kt", "km/h", "mi/h", "m/s"),
            R.id.txt_wind_speed_model
        )

        setupAdapterSettingDialog(
            R.id.fake_spinner_atm,
            listOf("mmHg", "inHg", "psi", "bar", "mbar", "hpa", "atm"),
            R.id.txt_atm_model
        )

        setupAdapterSettingDialog(
            R.id.fake_spinner_date_form, listOf("12 giờ", "24 giờ"), R.id.txt_time_model
        )
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
//        container.findViewById<LinearLayout>(R.id.contain_star)?.setOnClickListener {
//            Toast.makeText(this, "Open GG Play", Toast.LENGTH_SHORT).show();
//        }
        if (container.isEmpty()) {
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
                Toast.makeText(this, "Open GG Play", Toast.LENGTH_LONG).show();
            }
        }
        container.visibility = View.VISIBLE

        var count = 0;
        val containStar = findViewById<LinearLayout>(R.id.contain_star)
        var voted = false;
        for (i in 0 until containStar.childCount) {
            val childView = containStar.getChildAt(i)
            (childView as ImageView).setOnClickListener {
                voted = true;
                for (j in 0 until i + 1) (containStar.getChildAt(j) as ImageView).setImageResource(R.drawable.star_light);
                for (j in i + 1 until containStar.childCount) (containStar.getChildAt(j) as ImageView).setImageResource(
                    R.drawable.star_dark
                )
            }
            childView.animate().apply {
                duration = 300L * i // Stagger the duration for each child view
            }.withEndAction {
                childView.animate().apply {
                    duration = 150 // Apply scaling and rotation
                    scaleX(1.1f)
                    scaleY(1.1f)
                    rotation(10f)
                }.withEndAction {
                    childView.animate().apply {
                        duration = 150
                        scaleX(0.9f)
                        scaleY(0.9f)
                        rotation(-10f)
                    }.withEndAction {
                        childView.animate().apply {
                            duration = 150
                            scaleX(1f)
                            scaleY(1f)
                            rotation(0f)
                        }.withEndAction {
                            count++;
                            if (count == 5 && !voted) {
                                childView.animate().apply {
                                    duration = 300
                                }.withEndAction {
                                    for (j in 0 until containStar.childCount) {
                                        (containStar.get(j) as ImageView).setImageResource(R.drawable.star_dark)
                                    }
                                }.start()
                            }
                        }.start()
                    }.start()
                }.start()
            }.start()
        }
    }

    private fun setupLineChart() {
        findViewById<TextView>(R.id.txt_view_detail).setOnClickListener {
            val change = Intent(this, ActivityWeatherPerHour::class.java)
            startActivity(change)
        }
        lifecycleScope.launch {
            try {
                val resultAPI = withContext(Dispatchers.IO) {
                    RequestAPI.getInstance().getWeatherPerHourInNextTwentyFour(
                        FakeGlobal.getInstance().curLocation.latitude,
                        FakeGlobal.getInstance().curLocation.longitude
                    )
                }
                setupLineChartInUI(resultAPI)
            } catch (e: Exception) {
                Toast.makeText(this@MainActivity, e.message, Toast.LENGTH_SHORT).show()
                e.printStackTrace()
            }
        }
    }

    private fun setupLineChartInUI(arrInfo: List<DataWeatherPerHourModel>) {
        try {
//            val arrInfo = parseWeatherData(hour.getJSONObject("hourly"))
            setupTitleChartDegree(arrInfo)

            setupChart(arrInfo)
            setupScrollSync()
            test()
        } catch (e: Exception) {
            Toast.makeText(this, e.message.toString(), Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
    }

    private fun parseWeatherData(hourly: JSONObject): List<DataHourWeatherModel> {
        val arrInfo = mutableListOf<DataHourWeatherModel>()
        val calendar = Calendar.getInstance()
        val curHour = calendar.get(Calendar.HOUR_OF_DAY);
        val arrTime = hourly.getJSONArray("time");
        val arrTemp = hourly.getJSONArray("temperature_2m")
        val arrPop = hourly.getJSONArray("precipitation_probability")
        val arrWeatherCode = hourly.getJSONArray("weathercode")
        for (i in curHour until curHour + 24) {
            val s = arrTime.getString(i)
            val tempC = arrTemp.getDouble(i).toFloat()
            var h = curHour + i
            if (h >= 24) h -= 24
            val w = arrWeatherCode.getInt(i)
            arrInfo.add(
                DataHourWeatherModel(
                    0, s.takeLast(5), tempC.roundToInt(), tempC.roundToInt(), h in 6..18, w
                )
            )
        }
        return arrInfo
    }

    private fun setupChart(arrInfo: List<DataWeatherPerHourModel>) {
        try {
            val entries = ArrayList<Entry>()
            arrInfo.forEachIndexed { i, model ->
                entries.add(Entry(i.toFloat(), model.tempC.toFloat()))
            }
            val dataSet = createLineDataSet(entries)
            val lineChart = findViewById<LineChart>(R.id.lineChart)

            val yAxis = lineChart.axisLeft
            yAxis.setDrawGridLines(false)
            yAxis.setDrawZeroLine(false)

            val xAxis = lineChart.xAxis
            xAxis.setDrawGridLines(false)

            lineChart.axisLeft.setDrawGridLines(false)
            lineChart.axisRight.setDrawGridLines(false)

            dataSet.valueTextColor = Color.WHITE
            dataSet.mode = LineDataSet.Mode.CUBIC_BEZIER
            dataSet.setDrawCircles(false)
            dataSet.color = Color.parseColor("#f6ce1e")

            lineChart.requestLayout()
            lineChart.data = LineData(dataSet)
            setupChartStyle(lineChart, arrInfo)

            lineChart.invalidate()
        } catch (e: Exception) {
            Toast.makeText(this@MainActivity, e.message, Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
    }

    private fun setupChartStyle(lineChart: LineChart, arrInfo: List<DataWeatherPerHourModel>) {
        try {
            lineChart.setDrawGridBackground(false)
            lineChart.description.isEnabled = false
            lineChart.legend.isEnabled = false
            lineChart.axisRight.isEnabled = false
            lineChart.xAxis.setDrawGridLines(false)
            lineChart.axisLeft.setDrawGridLines(false)
            lineChart.axisRight.setDrawGridLines(false)
            val yAxis = lineChart.axisLeft
            yAxis.textColor = Color.WHITE
            yAxis.textSize = 15f
            yAxis.setDrawGridLines(false)

            yAxis.isEnabled = false
            val xAxis = lineChart.xAxis
            xAxis.setDrawLabels(false)
            xAxis.setDrawGridLines(false)

            lineChart.xAxis.setDrawGridLines(false)
            lineChart.axisLeft.setDrawGridLines(false)
            lineChart.axisRight.setDrawGridLines(false)

            lineChart.xAxis.setDrawAxisLine(false)
            lineChart.axisLeft.setDrawAxisLine(false)
            lineChart.axisRight.setDrawAxisLine(false)

            lineChart.setDrawBorders(false)

            lineChart.axisLeft.removeAllLimitLines()
            lineChart.axisRight.removeAllLimitLines()

            lineChart.axisLeft.isEnabled = false
            lineChart.axisRight.isEnabled = false

            lineChart.xAxis.setDrawLabels(false)
            lineChart.legend.isEnabled = false
            lineChart.description.isEnabled = false

            lineChart.renderer = CustomLineChartRenderer(
                lineChart, lineChart.animator, lineChart.viewPortHandler
            )
            lineChart.setExtraOffsets(10f, 20f, 10f, 10f)
            lineChart.isHighlightPerTapEnabled = false
            lineChart.isDoubleTapToZoomEnabled = false
            lineChart.isDragEnabled = false

            lineChart.requestLayout()

            val data = lineChart.data
            data?.dataSets?.forEach { set ->
                (set as LineDataSet).setDrawValues(true)
                (set as LineDataSet).valueFormatter = object : ValueFormatter() {
                    override fun getPointLabel(entry: Entry?): String {
                        if (entry != null) {
                            return entry.y.roundToInt().toString() + "°C"
                        } else return ""
                    }
                }
            }
        } catch (e: Exception) {
            Toast.makeText(this@MainActivity, e.message, Toast.LENGTH_SHORT).show()
            e.printStackTrace()
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
            handler.postDelayed(updateRunnable, 900_000)
        }


        updateRunnableTime = Runnable {
            UpdateCurrentTime()
            handler.postDelayed(updateRunnableTime, 60_000)
        }

        handler.postDelayed({
            UpdateWeatherInfor()
            UpdateCurrentTime()
            handler.postDelayed(updateRunnableTime, 60_000)
            handler.postDelayed(updateRunnable, 900_000)
        }, delayToNextMinute.toLong())
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(updateRunnable)
        handler.removeCallbacks(updateRunnableTime)
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

    private fun setupAdapterSettingDialog(
        spinnerId: Int, items: List<String>, txtViewId: Int
    ) {
        val spinner: TextView = findViewById(spinnerId)
        spinner.text = items.get(0);
        var i = 1;
        (spinner.parent as View).setOnClickListener {
            if (i >= items.size) i = 0;
            spinner.text = items.get(i);
            findViewById<TextView>(txtViewId).text = items.get(i);
            i++;
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
                Toast.makeText(
                    this, "Quyền thông báo bị từ chối", Toast.LENGTH_SHORT
                ).show()
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


    private fun setIsRainInNextTwoHours() {
        lifecycleScope.launch {
            val result = withContext(Dispatchers.IO) {
                RequestAPI.getInstance().getWeatherPerHourInNextTwentyFour(
                    curLocation.latitude, curLocation.longitude
                )
            }
            var isRain = false
            for (i in 0 until result.size) {
                if (i >= 3) break
                if (result[i].changeRain >= 40) {
                    isRain = true
                    break
                }
            }
            val txt = findViewById<TextView>(R.id.txt_noti_rain)
            if (isRain) {
                txt.text = "Có mưa trong 120 phút"
            } else {
                txt.text = "Không có mưa trong 120 phút"
            }
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
        val safeData =
            LocalStorageManager.getObject(KeysStorage.navMenuModel, NavMenuModel::class.java)
                ?: NavMenuModel()

        val switchNoti = findViewById<SwitchCompat>(R.id.switch_notification)
        val switchDaily = findViewById<SwitchCompat>(R.id.switch_daily_weather)
        val switchBg = findViewById<SwitchCompat>(R.id.switch_background)

        switchNoti.isChecked = safeData.notification
        switchBg.isChecked = safeData.background
        switchDaily.isChecked = safeData.weatherDaily

        switchNoti.setOnClickListener {
            safeData.notification = switchNoti.isChecked
            LocalStorageManager.putObject(KeysStorage.navMenuModel, safeData)
        }

        switchDaily.setOnClickListener {
            safeData.weatherDaily = switchDaily.isChecked
            LocalStorageManager.putObject(KeysStorage.navMenuModel, safeData)
        }

        switchBg.setOnClickListener {
            safeData.background = switchBg.isChecked
            LocalStorageManager.putObject(KeysStorage.navMenuModel, safeData)
        }
    }

    private fun hideCustomNav() {
        navPanel.animate().translationX(-navPanel.width.toFloat()).setDuration(300).withEndAction {
            navContainer.visibility = View.GONE
        }.start()
    }

    private fun setupTitleChartDegree(arr: List<DataWeatherPerHourModel>) {
        val rc_view = findViewById<RecyclerView>(R.id.rc_title_chart)
        val listData = mutableListOf<TitleChartItemModel>()
        for (item in arr) {
            listData.add(
                TitleChartItemModel(
                    item.time.substring(item.time.length - 5),
                    null,
                    item.iconCode,
                    item.isDay,
                    item.changeRain
                )
            )
        }

        val adapter = TitleChartDegreeAdapter(listData)
        rc_view.adapter = adapter
        rc_view.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

//        val itemCount = listData.size

//        rc_view.addItemDecoration(object : RecyclerView.ItemDecoration() {
//            override fun getItemOffsets(
//                outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State
//            ) {
//                val position = parent.getChildAdapterPosition(view)
//                if (position == RecyclerView.NO_POSITION) return
//
//                outRect.left = if (position == 0) -7 else 0
//                outRect.right = if (position == itemCount - 1) 8 else 0
//            }
//        })
    }

    fun test(){
        val bitmap = createLineChartBitmap(this)
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "chart_channel"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Biểu đồ", NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Thống kê hôm nay")
            .setContentText("Biểu đồ thể hiện dữ liệu")
            .setSmallIcon(R.drawable.icon_weather_1)
            .setStyle(NotificationCompat.BigPictureStyle().bigPicture(bitmap))
            .setOngoing(true)
            .build()

        notificationManager.notify(2001, notification)

    }

    fun createLineChartBitmap(context: Context): Bitmap {
       val chart = findViewById<LineChart>(R.id.lineChart);
        chart.isDrawingCacheEnabled = true
        chart.buildDrawingCache(true)
        val bitmap = Bitmap.createBitmap(chart.drawingCache)
        chart.isDrawingCacheEnabled = false
        return bitmap
        return bitmap
    }

    override fun onResume() {
        super.onResume()
        try {
            if (isFirstResume == false) {
                isFirstResume = true
                return
            } else {
                hideCustomNav()
                if (FakeGlobal.getInstance().curLocation != null) {
                    if (FakeGlobal.getInstance().flagIsChooseDefaultLocation && DatabaseService.getInstance(
                            applicationContext
                        ).locationWeatherService.checkIsExistLocationInDb(FakeGlobal.getInstance().curLocation)
                    ) {
                        FakeGlobal.getInstance().flagIsChooseDefaultLocation = false
                        this.showConfirmDefault()
                    }
                    curLocation = LatLng(
                        FakeGlobal.getInstance().curLocation.latitude,
                        FakeGlobal.getInstance().curLocation.longitude
                    )

                    if (::updateRunnable.isInitialized) {
                        handler.removeCallbacks(updateRunnable)
                    }

                    if (::updateRunnableTime.isInitialized) {
                        handler.removeCallbacks(updateRunnableTime)
                    }
                    this.onInitedLocation()
                }
            }
        } catch (e: Exception) {
            println("erorrrrrrrrrrr: ${e.message}")
        }
    }

    private fun showConfirmDefault() {
        val contain = findViewById<FrameLayout>(R.id.popup_confirm_defaul)
        contain.visibility = View.VISIBLE;
        findViewById<TextView>(R.id.txt_title_popup_confirm_defaul).text =
            FakeGlobal.getInstance().curLocation.name;
        findViewById<Button>(R.id.rej_set_default_1).setOnClickListener {
            contain.visibility = View.GONE
        }
        findViewById<Button>(R.id.rej_set_default_2).setOnClickListener {
            contain.visibility = View.GONE
        }

        findViewById<Button>(R.id.btn_accept_set_default).setOnClickListener {
            if (FakeGlobal.getInstance().isCurrentLocation == false) DatabaseService.getInstance(
                applicationContext
            ).locationWeatherService.changeDefaultLocation(
                FakeGlobal.getInstance().curLocation
            )
            contain.visibility = View.GONE
        }
    }
}