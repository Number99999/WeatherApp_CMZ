package com.cmzsoft.weather.RendererChart

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.DashPathEffect
import android.graphics.Paint
import com.github.mikephil.charting.animation.ChartAnimator
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.renderer.LineChartRenderer
import com.github.mikephil.charting.utils.ViewPortHandler

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
