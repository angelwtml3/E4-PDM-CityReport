# S13: Firebase Authentication y Google Sign-In
**Integrante:** Alan Efraín Barrera Sandoval  
**Matrícula:** 2023600875  
**Responsabilidad principal:** Firebase Authentication y Google Sign-In mediante Credential Manager  

---

## 1. Resumen Ejecutivo de la Responsabilidad
Como Integrante 1 del equipo, mi responsabilidad principal en la práctica S13 Flipped fue investigar, diseñar e implementar la capa de autenticación de usuarios mediante **Firebase Authentication** y **Google Sign-In** utilizando la API moderna de Android **Credential Manager** (`androidx.credentials`).

Esta implementación permite que los usuarios de "City Report" inicien sesión de forma transparente con sus cuentas de Google, recuperen su identificador único (UID), nombre, correo electrónico y fotografía de perfil, manteniendo su sesión iniciada entre reinicios de la aplicación.

---

## 2. Investigación Técnica sobre Firebase Authentication y Credential Manager

### 2.1. Evolución de Google Sign-In en Android
Anteriormente, las aplicaciones Android utilizaban la biblioteca legada `GoogleSignInClient` de Play Services. En las versiones recientes de Android (Android 14+ / API 34), Google estandarizó la autenticación a través del **Credential Manager API**, ofreciendo una experiencia unificada que soporta llaves de paso (Passkeys), contraseñas guardadas y tokens ID de Google (Google ID Tokens).

### 2.2. Flujo de Autenticación Implementado
1. **Petición de Credencial (`GetCredentialRequest`):** Se solicita la credencial de Google mediante `GetGoogleIdOption`, pasando el Web Client ID de OAuth 2.0.
2. **Obtención del Token ID:** Credential Manager despliega la interfaz nativa del sistema para seleccionar la cuenta de Google y retorna un `GoogleIdTokenCredential`.
3. **Intercambio con Firebase Auth:** Se extrae el `idToken` de la credencial de Google y se convierte en una credencial de Firebase mediante `GoogleAuthProvider.getCredential(idToken, null)`.
4. **Inicio de Sesión en Firebase:** Se invoca `FirebaseAuth.getInstance().signInWithCredential(firebaseCredential)`, registrando al usuario en Firebase Authentication y asignándole un UID global.
5. **Gestión de Perfil y Sesión:** Se obtiene el `FirebaseUser` activo para consultar `uid`, `displayName`, `email` y `photoUrl`.

---

## 3. Configuración individual de Firebase y proceso de Google Sign-In

Conforme a las instrucciones del docente, a continuación detallo el procedimiento paso a paso que ejecuto individualmente en mi rama de prueba (`feature/s13-alan-authentication`):

### 1. Registro o selección del proyecto Firebase
- Acceso a [Firebase Console](https://console.firebase.google.com/).
- Creación del proyecto nombrado `CityReport-Firebase` (o selección del proyecto existente).

### 2. Registro de la aplicación Android
- En la vista general del proyecto en Firebase Console, se selecciona la opción "Agregar aplicación Android".

### 3. Configuración del nombre de paquete
- Se registra exactamente el paquete del proyecto: `com.example.cityreport`.

### 4. Obtención de `google-services.json`
- Una vez registrada la app, se descarga el archivo `google-services.json` generado por Firebase Console.
- Se coloca en la raíz del módulo del proyecto Android: `app/google-services.json`.

### 5. Configuración de huella digital SHA-1
- Desde la terminal de Android Studio, se ejecuta la tarea de Gradle:
  `./gradlew signingReport`
- Se copia la huella SHA-1 obtenida (por ejemplo, del variant `debug`) y se registra en la configuración de la aplicación Android en Firebase Console.

### 6. Activación del proveedor Google en Firebase Console
- En el menú lateral de Firebase Console, se navega a **Authentication** -> **Sign-in method**.
- Se habilita el proveedor **Google** y se guarda la configuración.

### 7. Configuración de OAuth Web Client ID
- Al activar Google como proveedor, Firebase Console genera un **ID de cliente Web (Web Client ID)** en la pestaña de configuración del proveedor o en Google Cloud Console.
- Este Web Client ID se registra en el archivo `res/values/strings.xml` bajo la clave `default_web_client_id_placeholder`.

### 8. Funcionamiento de Google Sign-In
- En la aplicación, la clase `FirebaseAuthManager` utiliza `CredentialManager.create(context)` y solicita la credencial vinculada al Web Client ID.

### 9. Conexión con Firebase Authentication
- `FirebaseAuthManager.iniciarSesionConGoogle` recibe la credencial del sistema, obtiene el `idToken` de Google y llama a `FirebaseAuth.signInWithCredential()`.

### 10. Prueba individual de autenticación
- Se ejecuta la app en la rama `feature/s13-alan-authentication`, se pulsa el botón "Iniciar sesión con Google", se selecciona la cuenta y se verifica que en la pantalla de bienvenida se muestre la tarjeta de perfil con el UID, Nombre y Correo del usuario.

---

## 4. Archivos Desarrollados y Responsabilidades
- `app/src/main/java/com/example/cityreport/data/remote/FirebaseAuthManager.kt`: Helper singleton encargado de iniciar sesión con Credential Manager, cerrar sesión y extraer el `UserProfile`.
- `app/src/main/java/com/example/cityreport/WelcomeFragment.kt`: UI integrada con botones para "Iniciar sesión con Google", tarjetas de perfil de usuario y "Cerrar sesión".
- `app/src/main/res/layout/fragment_welcome.xml`: Diseño de la pantalla de bienvenida con la tarjeta `MaterialCardView` para datos del usuario.

---

## 5. Dificultades Encontradas y Soluciones Aplicadas

1. **Dificultad:** Incompatibilidad de bibliotecas legadas de Google Sign-In con el compilador KSP / AGP 9.2.
   - **Solución:** Se implementó Credential Manager (`androidx.credentials`) junto con la biblioteca `com.google.android.libraries.identity.googleid:googleid`, que es el estándar moderno recomendado por Google.

2. **Dificultad:** Manejo de la ausencia del archivo `google-services.json` durante el desarrollo inicial.
   - **Solución:** Se estructuraron los métodos de `FirebaseAuthManager` con verificaciones defensivas (`try-catch` y retorno seguro de nulos) para evitar cierres inesperados de la app en entornos de desarrollo sin credenciales configuradas.

---

## 6. Evidencias e Integración
- **Rama de Git asignada:** `feature/s13-alan-authentication`
- **Mensaje de Commit:** `S13 - Firebase Authentication - Alan Barrera`
- **Pruebas verificadas:** Compilación exitosa de la capa de autenticación, integración con `WelcomeFragment` y validación de arquitectura MVP.
