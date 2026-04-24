package es.ua.eps.drawables

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ProgressBar
import android.widget.SeekBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // Ajuste de insets para diseño edge-to-edge
        val mainView = findViewById<androidx.constraintlayout.widget.ConstraintLayout>(R.id.main)
        ViewCompat.setOnApplyWindowInsetsListener(mainView) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 1. Mostrar el nombre que viene del Login
        val nombre = intent.getStringExtra("USER_NAME") ?: "Invitado"
        val tvGreeting = findViewById<TextView>(R.id.texto) // Asegúrate de tener este ID en tu XML
        tvGreeting.text = "Hola, $nombre"

        // 2. Configuración de progreso (SeekBar -> ProgressBar)
        val progreso = findViewById<ProgressBar>(R.id.progreso)
        val seekBar = findViewById<SeekBar>(R.id.seekBar)

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                progreso.progress = progress
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        // 3. BOTÓN DE LOGOUT (Muy importante para poder volver al Login)
        val btnLogout = findViewById<Button>(R.id.btn_logout)
        btnLogout.setOnClickListener {
            // Borramos la sesión de SharedPreferences
            val sharedPref = getSharedPreferences("AUTH_PREFS", Context.MODE_PRIVATE)
            with(sharedPref.edit()) {
                clear() // Borra IS_LOGGED_IN y USER_NAME
                apply()
            }

            // Volvemos a la pantalla de Login
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish() // Cerramos MainActivity
        }
    }
}