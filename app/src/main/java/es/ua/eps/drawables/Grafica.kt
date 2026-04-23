package es.ua.eps.drawables

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import kotlin.math.min

class Grafica @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    // Paints para los sectores
    private val paintRojo = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.RED
        style = Paint.Style.FILL
    }

    private val paintAzul = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.BLUE
        style = Paint.Style.FILL
    }

    // Porcentaje a mostrar (0-100)
    var percentage: Int = 50
        set(value) {
            field = value.coerceIn(0, 100) // asegurarnos que esté entre 0 y 100
            invalidate() // volver a dibujar
        }

    init {
        // Leer atributo personalizado desde XML si lo hubiera
        attrs?.let {
            val a = context.obtainStyledAttributes(it, R.styleable.Grafica)
            percentage = a.getInt(R.styleable.Grafica_percentage, 50)
            a.recycle()
        }
    }

    // Sobrescribimos onMeasure para tamaño 100x100 por defecto
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = MeasureSpec.getSize(widthMeasureSpec)
        val height = MeasureSpec.getSize(heightMeasureSpec)
        val size = min(width, height)
        setMeasuredDimension(size, size)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val width = width.toFloat()
        val height = height.toFloat()
        val radio = min(width, height) / 2f
        val centroX = width / 2f
        val centroY = height / 2f

        val rectLeft = centroX - radio
        val rectTop = centroY - radio
        val rectRight = centroX + radio
        val rectBottom = centroY + radio

        // Dibujar sector azul (resto)
        canvas.drawArc(
            rectLeft,
            rectTop,
            rectRight,
            rectBottom,
            -90f + percentage * 3.6f, // empieza donde termina el rojo
            360 - percentage * 3.6f,
            true,
            paintAzul
        )

        // Dibujar sector rojo (porcentaje)
        canvas.drawArc(
            rectLeft,
            rectTop,
            rectRight,
            rectBottom,
            -90f, // empieza desde arriba
            percentage * 3.6f, // 360 grados * porcentaje / 100
            true,
            paintRojo
        )
    }
}
