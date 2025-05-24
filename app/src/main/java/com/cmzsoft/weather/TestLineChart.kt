package com.cmzsoft.weather

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.graphics.toArgb
import android.graphics.Color
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter

class TestLineChart : AppCompatActivity() {
    private lateinit var _lineChart: LineChart

    private lateinit var xValues: List<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.test_line_chart)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

    testLine()
    }

    private fun testLine() {
        // All X labels are "37°" as in the image
        val xValues = listOf("37°", "37°", "37°", "37°", "37°", "37°")
        _lineChart = findViewById<LineChart>(R.id.lineChart)

        // Remove chart description
        _lineChart.description.isEnabled = false
        // Remove legend
        _lineChart.legend.isEnabled = false

        // Set background color to blue
        _lineChart.setBackgroundColor(Color.parseColor("#4186b7"))
        _lineChart.setDrawGridBackground(false)

        // Configure X axis
        val xAxis = _lineChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.valueFormatter = IndexAxisValueFormatter(xValues)
        xAxis.labelCount = xValues.size
        xAxis.granularity = 1f
        xAxis.textColor = Color.WHITE
        xAxis.textSize = 16f
        xAxis.setDrawAxisLine(false)
        xAxis.setDrawGridLines(true)
        xAxis.gridColor = Color.WHITE
        xAxis.gridLineWidth = 2f
        xAxis.enableGridDashedLine(10f, 10f, 0f) // Dashed vertical lines
        xAxis.setDrawLabels(true)
        xAxis.yOffset = 10f

        // Hide left Y axis
        val yAxis = _lineChart.axisLeft
        yAxis.setDrawLabels(false)
        yAxis.setDrawAxisLine(false)
        yAxis.setDrawGridLines(false)
        yAxis.axisMinimum = 15f
        yAxis.axisMaximum = 40f

        // Hide right Y axis
        _lineChart.axisRight.isEnabled = false

        // Data for the yellow line (slightly sloped)
        val entries = listOf(
            com.github.mikephil.charting.data.Entry(0f, 37.2f),
            com.github.mikephil.charting.data.Entry(1f, 37.1f),
            com.github.mikephil.charting.data.Entry(2f, 37.05f),
            com.github.mikephil.charting.data.Entry(3f, 37.0f),
            com.github.mikephil.charting.data.Entry(4f, 37.0f),
            com.github.mikephil.charting.data.Entry(5f, 37.0f)
        )

        val dataSet = LineDataSet(entries, "").apply {
            color = Color.YELLOW
            lineWidth = 3f
            setDrawCircles(false)
            setDrawValues(false)
            setDrawFilled(false)
            mode = LineDataSet.Mode.CUBIC_BEZIER // Smooth curve
            setDrawHighlightIndicators(false)
        }

        val lineData = LineData(dataSet)
        _lineChart.data = lineData

        // Remove chart borders
        _lineChart.setDrawBorders(false)

        // Custom: Draw bottom dashed border (simulate with a limit line)
        val bottomLine = com.github.mikephil.charting.components.LimitLine(37f, "")
        bottomLine.lineColor = Color.WHITE
        bottomLine.lineWidth = 2f
        bottomLine.enableDashedLine(10f, 10f, 0f)
        yAxis.removeAllLimitLines()
        yAxis.addLimitLine(bottomLine)

        _lineChart.invalidate()
    }
}