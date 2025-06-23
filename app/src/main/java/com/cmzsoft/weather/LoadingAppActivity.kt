package com.cmzsoft.weather

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.cmzsoft.weather.FrameWork.Data.LocalStorageManager
import com.cmzsoft.weather.Model.Object.KeysStorage
import com.cmzsoft.weather.RendererChart.XAxisRendererHourlyChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter


class LoadingAppActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_loading_app)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            var systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        activeActivity()
//        test()
    }

    fun test() {
        var chart = findViewById<LineChart>(R.id.lineChart)
        var xAxis = chart.xAxis

        xAxis.position = XAxis.XAxisPosition.TOP
        xAxis.setDrawLabels(true)

        var labels = arrayListOf("January", "February", "March", "April", "May")
        var imageResIds = listOf(
            R.drawable.icon_map,
            R.drawable.icon_chart,
            R.drawable.icon_sucep,
            R.drawable.icon_camera,
            R.drawable.icon_chart
        )

        xAxis.valueFormatter = IndexAxisValueFormatter(labels)

        var customXAxisRenderer = XAxisRendererHourlyChart(
            chart.viewPortHandler,
            xAxis,
            chart.getTransformer(YAxis.AxisDependency.LEFT),
            labels,
            this,
            imageResIds
        )

        chart.setXAxisRenderer(customXAxisRenderer)

        var values = ArrayList<Entry>()
        values.add(Entry(0f, 10f))
        values.add(Entry(1f, 15f))
        values.add(Entry(2f, 25f))
        values.add(Entry(3f, 40f))
        values.add(Entry(4f, 30f))

        var lineDataSet = LineDataSet(values, "Sample Data")
        var lineData = LineData(lineDataSet)
        chart.data = lineData
        chart.invalidate()
    }

    fun activeActivity() {
//        var changePage = Intent(this, ActivitySettingTheme::class.java);
//        startActivity(changePage);
//
//        return
        if (LocalStorageManager.getString(KeysStorage.isFirstOpenApp) == null) {
            var changePage = Intent(this, ActivityRequestLocation::class.java);
            startActivity(changePage);
            finish()
        } else {
            var changePage = Intent(this, MainActivity::class.java);
            startActivity(changePage);
            finish()
        }
    }

}