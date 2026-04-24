package es.ua.eps.drawables

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
        btnGoogleAuth.setOnClickListener { lanzarRegistroGoogle() }
    }

    private fun verificarServiciosGoogle() {
        val availability = GoogleApiAvailability.getInstance()
        val resultCode = availability.isGooglePlayServicesAvailable(this)
        if (resultCode == ConnectionResult.SUCCESS) {
            Toast.makeText(this, "Google Play Services está disponible", Toast.LENGTH_SHORT).show()
        } else {
            availability.getErrorDialog(this, resultCode, 9000)?.show()
        }
    }

    private fun lanzarRegistroGoogle() {
        val credentialManager = CredentialManager.create(this)

        // Usamos el ID de tu archivo strings.xml
        val serverClientId = getString(R.string.web_client_id)

        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(serverClientId)
            .setAutoSelectEnabled(false) // Para que el usuario siempre elija la cuenta
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        lifecycleScope.launch {
            try {
                val result = credentialManager.getCredential(this@LoginActivity, request)
                procesarResultado(result)
            } catch (e: NoCredentialException) {
                // Si no hay cuentas de Google configuradas en el móvil, abrimos ajustes
                Log.e("AUTH", "No hay cuentas configuradas: ${e.message}")
                Toast.makeText(this@LoginActivity, "No hay cuentas de Google. Añade una.", Toast.LENGTH_LONG).show()
                val intent = Intent(Settings.ACTION_ADD_ACCOUNT).apply {
                    putExtra(Settings.EXTRA_ACCOUNT_TYPES, arrayOf("com.google"))
                }
                startActivity(intent)
            } catch (e: GetCredentialException) {
                Log.e("AUTH", "Error Credential Manager: ${e.message}")
                Toast.makeText(this@LoginActivity, "Error: ${e.message}", Toast.LENGTH_LONG).show()
            } catch (e: Exception) {
                Log.e("AUTH", "Error desconocido: ${e.message}")
            }
        }
    }

    private fun procesarResultado(result: GetCredentialResponse) {
        val credential = result.credential

        if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
            try {
                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)

                // Aquí ya tienes los datos del usuario
                val nombreUsuario = googleIdTokenCredential.displayName ?: "Usuario"
                val emailUsuario = googleIdTokenCredential.id

                Log.d("AUTH", "Login exitoso. Email: $emailUsuario")
                Toast.makeText(this, "Bienvenido $nombreUsuario", Toast.LENGTH_SHORT).show()

                // Ir a la MainActivity
                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra("USER_NAME", nombreUsuario)
                startActivity(intent)
                finish()
            } catch (e: Exception) {
                Log.e("AUTH", "Error al procesar los datos de la cuenta")
            }
        }
    }
}