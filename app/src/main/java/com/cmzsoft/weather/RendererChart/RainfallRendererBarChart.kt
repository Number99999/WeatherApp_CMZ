package com.cmzsoft.weather.RendererChart

import XAxisRendererTwoLine
import android.graphics.Canvas
import android.graphics.Path
import android.graphics.RectF
import com.github.mikephil.charting.animation.ChartAnimator
import com.github.mikephil.charting.buffer.BarBuffer
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.github.mikephil.charting.renderer.BarChartRenderer
import com.github.mikephil.charting.utils.ViewPortHandler

class RainfallRendererBarChart constructor(
    private val chart: BarChart,
    animator: ChartAnimator,
    private val viewPortHandler: ViewPortHandler,
    private val radius: Float = 50f,
) : BarChartRenderer(chart, animator, viewPortHandler) {

    override fun drawDataSet(c: Canvas, dataSet: IBarDataSet, index: Int) {
        val trans = mChart.getTransformer(dataSet.axisDependency)
        val buffer: BarBuffer = mBarBuffers[index]
        buffer.setPhases(mAnimator.phaseX, mAnimator.phaseY)
        buffer.setDataSet(index)
        buffer.setInverted(mChart.isInverted(dataSet.axisDependency))
        buffer.setBarWidth(mChart.barData.barWidth)
        buffer.feed(dataSet)

        trans.pointValuesToPixel(buffer.buffer)

        val isSingleColor = dataSet.colors.size == 1
        if (isSingleColor) {
            mRenderPaint.color = dataSet.color
        }

        var j = 0
        while (j < buffer.size()) {
            if (!mViewPortHandler.isInBoundsLeft(buffer.buffer[j + 2])) {
                j += 4
                continue
            }
            if (!mViewPortHandler.isInBoundsRight(buffer.buffer[j])) break

            if (!isSingleColor) {
                mRenderPaint.color = dataSet.getColor(j / 4)
            }

            val left = buffer.buffer[j]
            val top = buffer.buffer[j + 1]
            val right = buffer.buffer[j + 2]
            val bottom = buffer.buffer[j + 3]

            if (radius > 0f) {
                val path = roundRect(
                    Path(),
                    RectF(left, top, right, bottom),
                    radius,
                    radius,
                    tl = true,
                    tr = true,
                    bl = true,
                    br = true
                )
                c.drawPath(path, mRenderPaint)
            } else {
                c.drawRect(left, top, right, bottom, mRenderPaint)
            }

            j += 4
        }
    }

    private fun roundRect(
        path: Path,
        rect: RectF,
        rx: Float,
        ry: Float,
        tl: Boolean,
        tr: Boolean,
        bl: Boolean,
        br: Boolean
    ): Path {
        var rx = rx.coerceAtLeast(0f)
        var ry = ry.coerceAtLeast(0f)
        val width = rect.width()
        val height = rect.height()

        rx = rx.coerceAtMost(width / 2)
        ry = ry.coerceAtMost(height / 2)

        val widthMinusCorners = width - 2 * rx
        val heightMinusCorners = height - 2 * ry

        path.reset()
        path.moveTo(rect.right, rect.top + ry)

        if (tr) path.rQuadTo(0f, -ry, -rx, -ry) else {
            path.rLineTo(0f, -ry)
            path.rLineTo(-rx, 0f)
        }

        path.rLineTo(-widthMinusCorners, 0f)

        if (tl) path.rQuadTo(-rx, 0f, -rx, ry) else {
            path.rLineTo(-rx, 0f)
            path.rLineTo(0f, ry)
        }

        path.rLineTo(0f, heightMinusCorners)

        if (bl) path.rQuadTo(0f, ry, rx, ry) else {
            path.rLineTo(0f, ry)
            path.rLineTo(rx, 0f)
        }

        path.rLineTo(widthMinusCorners, 0f)

        if (br) path.rQuadTo(rx, 0f, rx, -ry) else {
            path.rLineTo(rx, 0f)
            path.rLineTo(0f, -ry)
        }

        path.rLineTo(0f, -heightMinusCorners)
        path.close()
        return path
    }
}
