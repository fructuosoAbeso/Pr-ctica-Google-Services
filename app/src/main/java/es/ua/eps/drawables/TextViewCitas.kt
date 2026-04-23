package es.ua.eps.drawables

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.widget.TextView
import kotlin.random.Random

@SuppressLint("AppCompatCustomView")
class TextViewCitas @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : TextView(context, attrs, defStyleAttr) {

    // Array de citas
    private val citas = arrayOf(
        "La vida es lo que pasa mientras estás ocupado haciendo otros planes. – John Lennon",
        "El éxito es aprender a ir de fracaso en fracaso sin desesperarse. – Winston Churchill",
        "No cuentes los días, haz que los días cuenten. – Muhammad Ali",
        "La imaginación es más importante que el conocimiento. – Albert Einstein",
        "Haz hoy lo que otros no harán, para lograr mañana lo que otros no pueden."
    )

    init {
        // Mostrar una cita aleatoria al iniciar
        text = obtenerCitaAleatoria()

        // Cambiar cita al pulsar sobre el TextView
        setOnClickListener {
            text = obtenerCitaAleatoria()
        }
    }

    // Función para obtener una cita aleatoria
    private fun obtenerCitaAleatoria(): String {
        val indice = Random.nextInt(citas.size)
        return citas[indice]
    }
}
