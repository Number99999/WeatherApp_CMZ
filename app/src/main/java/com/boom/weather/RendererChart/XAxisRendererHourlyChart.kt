package com.boom.weather.RendererChart

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.renderer.XAxisRenderer
import com.github.mikephil.charting.utils.MPPointF
import com.github.mikephil.charting.utils.Transformer
import com.github.mikephil.charting.utils.ViewPortHandler

class XAxisRendererHourlyChart(
    viewPortHandler: ViewPortHandler,
    xAxis: XAxis,
    trans: Transformer,
    private val labels: List<String>,
    private val context: Context, // Thêm context để load ảnh
    private val imageResIds: List<Int> // Danh sách ID ảnh
) : XAxisRenderer(viewPortHandler, xAxis, trans) {

    override fun drawLabels(c: Canvas, pos: Float, anchor: MPPointF) {
        val labelPaint: Paint = mAxisLabelPaint
        println("DRAWLABEL: " + pos)
        for (i in labels.indices) {
            val x = i.toFloat()
            val pt = floatArrayOf(x, 0f)

            mTrans.pointValuesToPixel(pt)

            if (mViewPortHandler.isInBoundsX(pt[0])) {
                val rawLabel = labels[i]
                val parts = rawLabel.split(" ")

                val line1 = parts.getOrNull(0) ?: ""
                val line2 = parts.getOrNull(1) ?: ""

                val drawable = ContextCompat.getDrawable(context, imageResIds.getOrNull(i) ?: 0)
                drawable?.let {
                    val imageWidth = 30  // Đặt kích thước ảnh
                    val imageHeight = 30
                    val left = pt[0] - imageWidth / 2 // Căn giữa ảnh
                    val top = pos - 50f  // Đặt ảnh trên trục X
                    it.setBounds(
                        left.toInt(),
                        top.toInt(),
                        (left + imageWidth).toInt(),
                        (top + imageHeight).toInt()
                    )
                    it.draw(c)
                }

                c.drawText(line1, pt[0], pos - 20f, labelPaint)
                c.drawText(line2, pt[0], pos + labelPaint.textSize - 30f, labelPaint)
            }
        }
    }
}