package es.ua.eps.drawables

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.TypedValue
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import com.google.android.material.snackbar.Snackbar

class NotificacionActivity : AppCompatActivity() {

    private var ultimaTarea: String? = null
    private var contadorTareas = 0
    private val canalID = "canal_tareas"
    private val notificacionID = 1

    // TextView para mostrar contador dentro de la app
    private lateinit var textViewContador: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notificacion)

        // ---------------- EJERCICIO 1: TOAST ----------------
        val editTextToast = findViewById<EditText>(R.id.editTextToast)
        val buttonToast = findViewById<Button>(R.id.buttonToast)

        buttonToast.setOnClickListener {
            val texto = editTextToast.text.toString().trim()
            if (texto.isEmpty()) {
                Toast.makeText(this, "Escribe algo para mostrar el Toast", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, texto, Toast.LENGTH_SHORT).show()
                editTextToast.text.clear()
            }
        }

        // ---------------- EJERCICIO 2: SNACKBAR ----------------
        val editText = findViewById<EditText>(R.id.editTextSnackbar)
        val button = findViewById<Button>(R.id.buttonSnackbar)
        val textViewLista = findViewById<TextView>(R.id.textViewLista)

        button.setOnClickListener {
            val texto = editText.text.toString().trim()
            if (texto.isEmpty()) {
                Toast.makeText(this, "Escribe un texto", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            ultimaTarea = texto
            textViewLista.append("$texto\n")
            editText.text.clear()

            Snackbar.make(button, "Tarea añadida", Snackbar.LENGTH_LONG)
                .setAction("Deshacer") { deshacerTarea(textViewLista) }
                .show()
        }

        // ---------------- EJERCICIO 3: DIÁLOGOS ----------------
        val buttonColor = findViewById<Button>(R.id.buttonColor)
        val buttonTamano = findViewById<Button>(R.id.buttonTamano)
        val textoEjemplo = findViewById<TextView>(R.id.textoEjemplo)

        buttonColor.setOnClickListener {
            val colores = arrayOf("Blanco y Negro", "Negro y Blanco", "Negro y Verde")
            AlertDialog.Builder(this)
                .setTitle("Selecciona un estilo de color")
                .setItems(colores) { _, which ->
                    when (which) {
                        0 -> { textoEjemplo.setBackgroundColor(0xFFFFFFFF.toInt()); textoEjemplo.setTextColor(0xFF000000.toInt()) }
                        1 -> { textoEjemplo.setBackgroundColor(0xFF000000.toInt()); textoEjemplo.setTextColor(0xFFFFFFFF.toInt()) }
                        2 -> { textoEjemplo.setBackgroundColor(0xFF000000.toInt()); textoEjemplo.setTextColor(0xFF00FF00.toInt()) }
                    }
                }.show()
        }

        buttonTamano.setOnClickListener {
            val tamanos = arrayOf("Pequeño", "Normal", "Grande")
            AlertDialog.Builder(this)
                .setTitle("Selecciona tamaño de texto")
                .setItems(tamanos) { _, which ->
                    when (which) {
                        0 -> textoEjemplo.setTextSize(TypedValue.COMPLEX_UNIT_SP, 8f)
                        1 -> textoEjemplo.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f)
                        2 -> textoEjemplo.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20f)
                    }
                }.show()
        }

        // ---------------- EJERCICIO 4: NOTIFICACIONES ----------------
        val buttonIniciar = findViewById<Button>(R.id.buttonIniciar)
        val buttonDetener = findViewById<Button>(R.id.buttonDetener)
        textViewContador = findViewById(R.id.textViewContador)

        crearCanalNotificacion() // Crear canal en Android 8+

        buttonIniciar.setOnClickListener {
            contadorTareas++
            actualizarContador()
            mostrarNotificacion()
        }

        buttonDetener.setOnClickListener {
            if (contadorTareas > 0) {
                contadorTareas--
                if (contadorTareas == 0) {
                    cancelarNotificacion()
                } else {
                    mostrarNotificacion()
                }
                actualizarContador()
            }
        }
    }

    // ---------------- FUNCIONES ----------------
    private fun deshacerTarea(textViewLista: TextView) {
        ultimaTarea?.let { tarea ->
            val nuevoTexto = textViewLista.text.toString().replace("$tarea\n", "")
            textViewLista.text = nuevoTexto
            ultimaTarea = null
        }
    }

    private fun actualizarContador() {
        textViewContador.text = "Tareas iniciadas: $contadorTareas"
    }

    private fun crearCanalNotificacion() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val nombre = "Tareas Canal"
            val descripcion = "Notificaciones de tareas iniciadas"
            val importancia = NotificationManager.IMPORTANCE_HIGH
            val canal = NotificationChannel(canalID, nombre, importancia)
            canal.description = descripcion
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(canal)
        }
    }

    private fun mostrarNotificacion() {
        val intent = Intent(this, NotificacionActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(this, canalID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Tareas iniciadas")
            .setContentText("Tareas iniciadas: $contadorTareas")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(false)

        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(notificacionID, builder.build())
    }

    private fun cancelarNotificacion() {
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.cancel(notificacionID)
    }
}
