package es.ua.eps.drawables

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout

class EdicionBorrable @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private val editText: EditText
    private val button: Button

    init {
        // Inflar un layout interno
        orientation = HORIZONTAL
        val inflater = LayoutInflater.from(context)
        inflater.inflate(R.layout.view_edicion_borrable, this, true)

        // Referencias a los elementos internos
        editText = findViewById(R.id.edicion)
        button = findViewById(R.id.boton_borrar)

        // Acción del botón: borrar el contenido del EditText
        button.setOnClickListener {
            editText.text.clear()
        }
    }

    // Función opcional para obtener el texto actual
    fun getTexto(): String {
        return editText.text.toString()
    }

    // Función opcional para establecer texto
    fun setTexto(texto: String) {
        editText.setText(texto)
    }
}
