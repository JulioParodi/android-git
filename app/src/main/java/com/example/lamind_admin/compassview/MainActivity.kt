package com.example.lamind_admin.compassview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.os.Bundle
import android.os.Parcelable
import android.support.v7.app.AppCompatActivity
import android.util.AttributeSet
import android.view.View
import com.example.lamind_admin.compassview.R.id.compassView
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

    }

    abstract class CompassView  @JvmOverloads constructor(
            context: Context,
            attrs: AttributeSet? = null,
            defStyleAttr: Int = 0
    ) : View(context, attrs, defStyleAttr), Parcelable {
        init {
            initCompassView()
        }

        private fun measure(measureSpec: Int): Int {
            // Decode the measurement specifications.
            val specMode = View.MeasureSpec.getMode(measureSpec)
            val specSize = View.MeasureSpec.getSize(measureSpec)
            return if (specMode == View.MeasureSpec.UNSPECIFIED) 200 else specSize
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
        private var northString = ""
        private var eastString = ""
        private var southString = ""
        private var westString = ""
        private var textHeight = 0f
        var bearing: Float = 0f


        private fun initCompassView() {
            isFocusable = true
            val r = this.resources
            circlePaint.color = r.getColor(R.color)
            circlePaint.strokeWidth = 1f
            circlePaint.style = Paint.Style.FILL_AND_STROKE
            northString = r.getString(R.string.cardinalN)
            eastString = r.getString(R.string.cardinalE)
            southString = r.getString(R.string.cardinalS)
            westString = r.getString(R.string.cardinalW)
            textPaint.color = r.getColor(R.color.colorCompassText)
            textHeight = textPaint.measureText("yY")
            markerPaint.color = r.getColor(R.color.colorCompassMarker)
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
                canvas.drawLine(px, py - radius, px, py - radius + 10, markerPaint)
                canvas.save()
                canvas.translate(0f, textHeight)
                if (i % 6 == 0) {
                    val dirString = when (i) {
                        0 -> {
                            val arrowY = 2 * textHeight
                            canvas.drawLine(px, arrowY, px - 5, 3 * textHeight, markerPaint)
                            canvas.drawLine(px, arrowY, px + 5, 3 * textHeight, markerPaint)
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


}


