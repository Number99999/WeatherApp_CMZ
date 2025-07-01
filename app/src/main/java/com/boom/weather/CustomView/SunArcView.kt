package com.boom.weather.CustomView

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import kotlin.math.cos
import kotlin.math.sin
class SunArcView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : View(context, attrs) {

    // Paint nét vàng (phần đã đi qua)
    private val arcPaintYellow = Paint().apply {
        color = Color.YELLOW
        style = Paint.Style.STROKE
        strokeWidth = 6f
        pathEffect = DashPathEffect(floatArrayOf(10f, 10f), 0f)
        isAntiAlias = true
    }

    // Paint nét trắng (phần chưa đi tới)
    private val arcPaintWhite = Paint().apply {
        color = Color.WHITE
        style = Paint.Style.STROKE
        strokeWidth = 6f
        pathEffect = DashPathEffect(floatArrayOf(10f, 10f), 0f)
        isAntiAlias = true
    }

    // Paint mặt trời
    private val sunPaint = Paint().apply {
        color = Color.YELLOW
        style = Paint.Style.FILL
        isAntiAlias = true
    }

    // Giá trị vị trí mặt trời (0f → 1f)
    private var sunPosition = 0f

    /**
     * Gọi hàm này để cập nhật vị trí mặt trời từ ngoài
     * @param percent từ 0f đến 1f
     */
    fun setSunPosition(percent: Float) {
        sunPosition = percent.coerceIn(0f, 1f)
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Setup toạ độ và bán kính cung
        val padding = 80f
        val width = width.toFloat() - padding * 2
        val radius = width / 2
        val centerX = padding + radius
        val centerY = padding + radius

        // Hình chữ nhật bao quanh cung tròn
        val rectF = RectF(
            centerX - radius,
            centerY - radius,
            centerX + radius,
            centerY + radius
        )

        // Vẽ phần đường đã đi qua (vàng)
        val startAngle = 180f
        val sweepTo = 180f * sunPosition
        val pathYellow = Path().apply {
            arcTo(rectF, startAngle, sweepTo)
        }
        canvas.drawPath(pathYellow, arcPaintYellow)

        // Vẽ phần chưa đi tới (trắng)
        val pathWhite = Path().apply {
            arcTo(rectF, startAngle + sweepTo, 180f - sweepTo)
        }
        canvas.drawPath(pathWhite, arcPaintWhite)

        // Vẽ mặt trời tại vị trí tương ứng
        val angleRad = Math.toRadians(180.0 + 180.0 * sunPosition)
        val sunX = centerX + radius * cos(angleRad).toFloat()
        val sunY = centerY + radius * sin(angleRad).toFloat()
        canvas.drawCircle(sunX, sunY, 20f, sunPaint)
    }
}