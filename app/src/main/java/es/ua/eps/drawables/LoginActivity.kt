package es.ua.eps.drawables

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.NoCredentialException
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. AUTO-LOGIN (Persistencia local): Si ya existe el flag en SharedPreferences, saltar al Main
        val sharedPref = getSharedPreferences("AUTH_PREFS", Context.MODE_PRIVATE)
        if (sharedPref.getBoolean("IS_LOGGED_IN", false)) {
            val nombre = sharedPref.getString("USER_NAME", "Usuario")
            irAMain(nombre!!)
            return
        }

        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.login_main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val btnGoogleAuth = findViewById<Button>(R.id.btn_google_auth)
        val btnCheckServices = findViewById<Button>(R.id.btn_check_services)

        btnCheckServices.setOnClickListener { verificarServiciosGoogle() }
        btnGoogleAuth.setOnClickListener { iniciarFlujoAutenticacion() }
    }

    private fun verificarServiciosGoogle() {
        val availability = GoogleApiAvailability.getInstance()
        val resultCode = availability.isGooglePlayServicesAvailable(this)
        if (resultCode == ConnectionResult.SUCCESS) {
            Toast.makeText(this, "Servicios OK", Toast.LENGTH_SHORT).show()
        } else {
            availability.getErrorDialog(this, resultCode, 9000)?.show()
        }
    }

    private fun iniciarFlujoAutenticacion() {
        val credentialManager = CredentialManager.create(this)
        val serverClientId = getString(R.string.web_client_id)

        // OPCIÓN LOGIN: Busca cuentas que YA han autorizado esta app anteriormente.
        val loginOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(true)
            .setServerClientId(serverClientId)
            .setAutoSelectEnabled(true) // Crucial: Si ya existe, hace login automático sin popup
            .build()

        // OPCIÓN REGISTRO: Permite elegir cualquier cuenta si no se encuentra una autorizada.
        val registroOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(serverClientId)
            .setAutoSelectEnabled(false)
            .build()

        // Añadimos ambas: CredentialManager intentará primero la más restrictiva (login)
        val request = GetCredentialRequest.Builder()
            .addCredentialOption(loginOption)
            .addCredentialOption(registroOption)
            .build()

        lifecycleScope.launch {
            try {
                val result = credentialManager.getCredential(this@LoginActivity, request)
                procesarResultado(result)
            } catch (e: NoCredentialException) {
                // Si no hay ninguna cuenta de Google en el dispositivo
                startActivity(Intent(Settings.ACTION_ADD_ACCOUNT).apply {
                    putExtra(Settings.EXTRA_ACCOUNT_TYPES, arrayOf("com.google"))
                })
            } catch (e: GetCredentialException) {
                Log.e("AUTH", "Error: ${e.message}")
                Toast.makeText(this@LoginActivity, "Sesión no iniciada", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun procesarResultado(result: GetCredentialResponse) {
        val credential = result.credential

        if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
            try {
                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                val nombreUsuario = googleIdTokenCredential.displayName ?: "Usuario"

                // GUARDAR SESIÓN: Para que el autologin del onCreate funcione la próxima vez
                val sharedPref = getSharedPreferences("AUTH_PREFS", Context.MODE_PRIVATE)
                with(sharedPref.edit()) {
                    putBoolean("IS_LOGGED_IN", true)
                    putString("USER_NAME", nombreUsuario)
                    apply()
                }

                Toast.makeText(this, "Bienvenido $nombreUsuario", Toast.LENGTH_SHORT).show()
                irAMain(nombreUsuario)

            } catch (e: Exception) {
                Log.e("AUTH", "Error al procesar token")
            }
        }
    }

    private fun irAMain(nombre: String) {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("USER_NAME", nombre)
        startActivity(intent)
        finish()
    }
}