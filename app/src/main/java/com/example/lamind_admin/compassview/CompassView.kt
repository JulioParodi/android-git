package com.example.lamind_admin.compassview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.view.View

class CompassView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private fun measure(measureSpec: Int): Int {
        // Decode the measurement specifications.
        val specMode = MeasureSpec.getMode(measureSpec)
        val specSize = MeasureSpec.getSize(measureSpec)
        return if (specMode == MeasureSpec.UNSPECIFIED) 200 else specSize
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val measuredWidth = measure(widthMeasureSpec)
        val measuredHeight = measure(heightMeasureSpec)
        val d = Math.min(measuredWidth, measuredHeight)
        setMeasuredDimension(d, d)
    }

    private val markerPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val textPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val circlePaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var northString = resources.getString(R.string.cardinalN)
    private var eastString = resources.getString(R.string.cardinalE)
    private var southString = resources.getString(R.string.cardinalS)
    private var westString = resources.getString(R.string.cardinalW)
    private var textHeight = 0f
    var bearing: Float = 0f

    init {
        initCompassView()
    }

    private fun initCompassView() {
        circlePaint.color = ContextCompat.getColor(context, R.color.colorCompassBackgnd)
        circlePaint.strokeWidth = 1f
        circlePaint.style = Paint.Style.FILL_AND_STROKE
        textPaint.color = ContextCompat.getColor(context, R.color.colorCompassText)
        markerPaint.color = ContextCompat.getColor(context, R.color.colorCompassMarker)
        markerPaint.strokeWidth = 8f
        textHeight = 48f //textHeight = textPaint.measureText("yY")
        textPaint.textSize = 48f
    }

    override fun onDraw(canvas: Canvas) {
        // centro e raio
        val px = measuredWidth / 2f
        val py = measuredHeight / 2f
        val radius = Math.min(px, py)
        // desenha a base do mostrador
        canvas.drawCircle(px, py, radius, circlePaint)

        // rotacionar base conforme contrário da
        // orientação para desenhar itens
        canvas.save()
        canvas.rotate(-bearing, px, py)


        val textWidth = textPaint.measureText("W").toInt()
        val cardinalX = px - textWidth / 2
        val cardinalY = py - radius + textHeight
        // desenhar marcas a cada 16 graus e texto a cada 45
        for (i in 0..23) {
            canvas.drawLine(px, py - radius, px, py - radius + 30, markerPaint)
            canvas.save()
            canvas.translate(0f, textHeight)
            if (i % 6 == 0) {
                val dirString = when (i) {
                    0 -> {
                        val arrowY = 2 * textHeight
                        canvas.drawLine(px, arrowY, px - 8, 4 * textHeight, markerPaint)
                        canvas.drawLine(px, arrowY, px + 8, 4 * textHeight, markerPaint)
                        northString
                    }
                    6 -> eastString
                    12 -> southString
                    18 -> westString
                    else -> ""
                }
                canvas.drawText(dirString, cardinalX, cardinalY, textPaint)
            } else if (i % 3 == 0) {
                val angle = (i * 15).toString()
                val angleTextWidth = textPaint.measureText(angle)
                val angleTextX = (px - angleTextWidth / 2).toInt()
                val angleTextY = py - radius + textHeight
                canvas.drawText(angle, angleTextX.toFloat(), angleTextY, textPaint)
            }
            canvas.restore()
            canvas.rotate(15f, px, py)
        }
        canvas.restore()
    }

}