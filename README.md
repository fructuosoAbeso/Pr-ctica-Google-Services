Implementación de Autenticación con Google Credential Manager 🚀
Este proyecto implementa un flujo de inicio de sesión moderno en Android utilizando la nueva API Google Credential Manager, sustituyendo las antiguas librerías de Google Sign-In.

📋 Proceso de Configuración
Para que el sistema de autenticación funcione, se han realizado los siguientes pasos técnicos:

1. Configuración en Google Cloud / Firebase 🔐
Creación de Credenciales: Se configuró un proyecto en la consola de Google Cloud.

Client ID de Tipo Web: Se generó un Web Client ID. Este es el identificador que el Credential Manager utiliza para vincular la aplicación Android con el servidor de autenticación de Google.

Registro de Huella Digital (SHA-1): * Se obtuvo la clave SHA-1 del entorno de desarrollo (debug) de la MacBook Air utilizando el comando ./gradlew signingReport.

Este SHA-1 se registró en la consola de Firebase para autorizar las peticiones desde el dispositivo físico/emulador.

2. Dependencias del Proyecto 📦
Se añadieron las librerías necesarias en el archivo build.gradle.kts:

androidx.credentials: Para la gestión de credenciales.

com.google.android.libraries.identity.googleid: Específicamente para la integración con ID de Google.

3. Lógica de Autenticación (LoginActivity) 💻
La implementación incluye:

Verificación de Google Play Services: Un paso previo para asegurar que el dispositivo es compatible.

Petición de Credenciales: Uso de GetGoogleIdOption configurado con el web_client_id.

Manejo de Excepciones:

NoCredentialException: Si el usuario no tiene cuentas en el dispositivo, el sistema lo redirige automáticamente a los ajustes del sistema para añadir una cuenta de Google (Settings.ACTION_ADD_ACCOUNT).

GetCredentialException: Captura errores de red o cancelaciones por parte del usuario.

🛠️ Tecnologías Utilizadas
Kotlin con Corrutinas y lifecycleScope.

Jetpack Credential Manager.

Material Design 3 para la interfaz de usuario.

Google Play Services.
