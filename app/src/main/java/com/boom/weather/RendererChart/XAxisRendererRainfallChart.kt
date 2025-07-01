import android.graphics.Canvas
import android.graphics.Paint
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.renderer.XAxisRenderer
import com.github.mikephil.charting.utils.MPPointF
import com.github.mikephil.charting.utils.Transformer
import com.github.mikephil.charting.utils.ViewPortHandler

class XAxisRendererRainfallChart(
    viewPortHandler: ViewPortHandler,
    xAxis: XAxis,
    trans: Transformer,
    private var labels: List<String>
) : XAxisRenderer(viewPortHandler, xAxis, trans) {

    override fun drawLabels(c: Canvas, pos: Float, anchor: MPPointF) {
        val labelPaint: Paint = mAxisLabelPaint
        for (i in labels.indices) {
            val x = i.toFloat()
            val pt = floatArrayOf(x, 0f)

            mTrans.pointValuesToPixel(pt)

            if (mViewPortHandler.isInBoundsX(pt[0])) {
                val rawLabel = labels[i]
                val parts = rawLabel.split("\n")

                val line1 = parts.getOrNull(0) ?: ""
                val line2 = parts.getOrNull(1) ?: ""
                c.drawText(line1, pt[0], pos - 25f, labelPaint)
                c.drawText(line2, pt[0], pos + labelPaint.textSize - 15f, labelPaint)
            }
        }
    }
}
