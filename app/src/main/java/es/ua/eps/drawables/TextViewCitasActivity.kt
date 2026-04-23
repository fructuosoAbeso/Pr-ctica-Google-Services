package es.ua.eps.drawables

import android.os.Bundle
import android.widget.SeekBar
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class TextViewCitasActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_text_view_citas)

        // Ajustar padding por las barras del sistema
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // ⭐ TextViewCitas
        val textViewCitas = findViewById<TextViewCitas>(R.id.texto_citas)
        // Ya cambia cita al pulsar, no se necesita más código aquí

        // ⭐ EdicionBorrable
        val edicionBorrable = findViewById<EdicionBorrable>(R.id.edicion_borrable)
        edicionBorrable.setTexto("Texto inicial") // opcional, se puede dejar vacío

        // ⭐ Grafica + SeekBar
        val grafica = findViewById<Grafica>(R.id.grafica)
        val seekBarGrafica = findViewById<SeekBar>(R.id.seekBarGrafica)

        seekBarGrafica.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                grafica.percentage = progress
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }
}
