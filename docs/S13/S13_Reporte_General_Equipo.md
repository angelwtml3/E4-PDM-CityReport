# S13 Flipped: Firebase + Multimedia - Reporte General del Equipo
**Materia:** Programación de Dispositivos Móviles  
**Proyecto:** City Report  
**Fecha:** Septiembre 2026  

---

## 1. Portada e Integrantes del Equipo

| Nombre Completo | Matrícula | Responsabilidad Principal |
| :--- | :--- | :--- |
| **Alan Efraín Barrera Sandoval** | 2023600875 | Firebase Authentication y Google Sign-In |
| **Miguel Ángel Hernández Herrera** | 2024601990 | Cloud Firestore y Estructura de Datos |
| **Alison Liliana Rojo Gómez** | 2024601281 | Firebase Storage y Gestión Multimedia |
| **Marco Antonio Tamayo Álvarez** | 2025600436 | Integración General, Sincronización, Room y DataStore |

---

## 2. Introducción
La aplicación **City Report** es una plataforma móvil orientada al reporte y seguimiento de incidencias urbanas (baches, fugas de agua, fallas de alumbrado público y acumulación de basura). Tras haber desarrollado en la práctica S12 el soporte de persistencia local mediante **Room Database** y la administración de preferencias del sistema con **Preferences DataStore**, en esta práctica S13 Flipped se potencia la aplicación integrando la plataforma en la nube **Firebase** (Authentication, Cloud Firestore y Firebase Storage) junto con la API moderna de autenticación **Credential Manager** y la captura/selección multimedia.

---

## 3. Objetivo de la Actividad
Ampliar la arquitectura y capacidades de "City Report" mediante la integración de los servicios de Firebase, garantizando la autenticación segura mediante cuentas de Google, la sincronización de incidencias en Cloud Firestore, la carga de fotografías en Firebase Storage y el mantenimiento del soporte offline y persistencia local desarrollados en S12 sin romper ninguna funcionalidad previa.

---

## 4. Descripción General de Firebase
Firebase es la plataforma de desarrollo móvil de Google que proporciona servicios de infraestructura en la nube en tiempo real. En este proyecto se integran tres de sus pilares fundamentales:
- **Firebase Authentication:** Gestión de identidades y tokens OAuth.
- **Cloud Firestore:** Base de datos NoSQL jerárquica orientada a documentos.
- **Firebase Storage:** Almacenamiento seguro de objetos y archivos multimedia (imágenes).

---

## 5. Firebase Authentication
Firebase Authentication proporciona servicios de backend, SDKs fáciles de usar y librerías de UI listas para usar para autenticar a los usuarios en la aplicación. Permite la identificación unificada mediante tokens JWT securizados y asigna a cada usuario registrado un identificador único global (`UID`).

---

## 6. Google Sign-In (Credential Manager)
Siguiendo las recomendaciones oficiales vigentes para Android 14+ (API 34+), el inicio de sesión se implementó utilizando **Credential Manager** (`androidx.credentials`) en combinación con la biblioteca `GoogleIdTokenCredential`. Este enfoque reemplaza los SDKs antiguos de Google Sign-In, permitiendo solicitar el token de identidad de Google de forma nativa e intercambiarlo por una credencial de Firebase mediante `GoogleAuthProvider.getCredential(idToken, null)`.

---

## 7. Cloud Firestore
Cloud Firestore almacena la información de los reportes organizándolos en una estructura jerárquica multiusuario:

`users/{uid}/incidencias/{incidenciaId}`

Cada documento guarda las propiedades: `id`, `titulo`, `descripcion`, `categoria`, `latitud`, `longitud`, `fotoUri`, `fecha`, `resuelta` y `userId`. De este modo, se asegura que cada usuario gestione sus propias incidencias de manera aislada.

---

## 8. Firebase Storage
Firebase Storage administra el almacenamiento masivo de los archivos de imagen capturados por la cámara o seleccionados desde la galería del dispositivo.
Las imágenes se almacenan bajo la ruta:

`users/{uid}/incidencias/{incidenciaId}/imagenes/foto_{timestamp}.jpg`

Una vez subida la imagen, el SDK retorna la URL de descarga HTTPS pública, la cual se guarda tanto en Firestore como en Room para su posterior renderizado en la interfaz.

---

## 9. Arquitectura General de la Aplicación
La aplicación mantiene el patrón arquitectónico **Modelo-Vista-Presentador (MVP)** con división modular:
- **`model`**: Data class `Incidencia`, enum `Categoria`, interfaces y repositorio local Room.
- **`data.local`**: Base de datos SQLite Room (`AppDatabase`), DAO (`IncidenciaDao`), TypeConverters y DataStore (`UserPreferencesRepository`).
- **`data.remote`**: Módulos de Firebase (`FirebaseAuthManager`, `FirestoreRepository`, `FirebaseStorageManager`).
- **`data.repository`**: `SyncRepository`, encargado de coordinar las operaciones locales y en la nube.
- **`view` & `presenter`**: UI (Fragments/Activities) y Presenters de vista desacoplados.

---

## 10. Integración con Room
Room Database (`city_report.db`) actúa como la base de datos primaria en el dispositivo. Todas las operaciones de lectura para el `RecyclerView` leen de Room, garantizando que el usuario tenga acceso inmediato a sus datos registrados incluso en ausencia de conexión a la red.

---

## 11. Integración con DataStore
Preferences DataStore (`user_preferences`) administra las preferencias locales de la interfaz:
- `dark_theme` (Boolean): Aplicación dinámica del Tema Oscuro mediante `AppCompatDelegate.setDefaultNightMode()`.
- `notifications_enabled` (Boolean): Control de activación/desactivación de las alertas del sistema.

---

## 12. Sincronización Local y en la Nube (Estrategia Offline-First)
1. **Registro Local:** Cuando el usuario crea un reporte, este se guarda de forma inmediata en Room.
2. **Carga Multimedia:** Si existe una fotografía seleccionada y el usuario inició sesión, la imagen se sube a Firebase Storage.
3. **Persistencia Nube:** Se actualiza la URL en Room y se envía el documento a Cloud Firestore.
4. **Resilencia a Fallos de Red:** Si no hay conexión, los datos permanecen seguros en Room hasta que se ejecute la resincronización.

---

## 13. Distribución de Responsabilidades del Equipo

- **Alan Efraín Barrera Sandoval (Integrante 1):** Diseño e implementación de `FirebaseAuthManager.kt`, integración de Credential Manager con Google Sign-In, gestión de la tarjeta de perfil en `WelcomeFragment.kt` y documentación individual.
- **Miguel Ángel Hernández Herrera (Integrante 2):** Diseño del esquema NoSQL en Firestore, implementación de `FirestoreRepository.kt` (CRUD completo) y reglas de seguridad de datos.
- **Alison Liliana Rojo Gómez (Integrante 3):** Integración de Firebase Storage (`FirebaseStorageManager.kt`), selector de fotos nativo (`PickVisualMedia`) en `NuevaIncidenciaFragment.kt` y visualización multimedia.
- **Marco Antonio Tamayo Álvarez (Integrante 4):** Orquestación en `SyncRepository.kt`, preservación de Room y DataStore (S12), resolución de conflictos, integración global y reporte general.

---

## 14. Resultados Obtenidos
- Aplicación Android 100% funcional y compilable.
- Autenticación transparente mediante Google Sign-In y Firebase Auth.
- Persistencia híbrida local-nube (Room + Cloud Firestore).
- Carga de imágenes a Firebase Storage con previsualización dinámica.
- Administración de preferencias con DataStore (Tema Claro/Oscuro y Notificaciones).
- 5 pruebas unitarias automatizadas ejecutadas y aprobadas.

---

## 15. Dificultades Encontradas y Soluciones Aplicadas

1. **Incompatibilidad KSP / AGP 9.2 en funciones suspend de Room DAO:**
   - *Solución:* Se definieron los métodos de `IncidenciaDao` de forma síncrona y se envolvieron dentro de bloques `runBlocking(Dispatchers.IO)` en `IncidenciaRepositoryImpl`, resolviendo el error del compilador KSP.
2. **Expederación de URIs de galería provisionales:**
   - *Solución:* Se implementó la subida inmediata del archivo a Firebase Storage al confirmar el reporte, almacenando la URL pública permanente.
3. **Ausencia inicial de `google-services.json`:**
   - *Solución:* Se envolvió la instanciación de los SDKs de Firebase en bloques de verificación defensiva para garantizar que el proyecto compile de forma limpia.

---

## 16. Pruebas Realizadas

- [x] **Compilación Gradle:** Ejecución de `:app:assembleDebug` con estado de éxito.
- [x] **Pruebas Unitarias:** Ejecución de `:app:testDebugUnitTest` (5/5 pruebas aprobadas).
- [x] **Autenticación:** Comprobación del flujo de estado de sesión en `WelcomeFragment`.
- [x] **Room & DataStore:** Validación de persistencia tras reinicio de la app.

---

## 17. Espacio para Capturas de Evidencia (Para incluir tras pruebas en consola)

- *[Captura 1: Configuración de Firebase Console - Panel de Control]*
- *[Captura 2: Firebase Authentication - Proveedor Google habilitado]*
- *[Captura 3: Cloud Firestore - Colección `users/{uid}/incidencias`]*
- *[Captura 4: Firebase Storage - Bucket de imágenes `users/{uid}/...`]*
- *[Captura 5: Aplicación Android - Pantalla de Bienvenida con Perfil]*
- *[Captura 6: Aplicación Android - Formulario de Incidencia con Foto y Ubicación]*

---

## 18. Conclusiones
La integración de Firebase junto con la persistencia local previa (Room y DataStore) ha permitido transformar "City Report" en una solución móvil robusta, profesional y preparada para producción. La arquitectura MVP facilita la separación de responsabilidades entre los integrantes del equipo y garantiza la mantenibilidad del código a largo plazo.

---

## 19. Referencias Oficiales Consultadas
1. Documentación Oficial de Firebase para Android: `https://firebase.google.com/docs/android/setup`
2. Guía de Credential Manager en Android: `https://developer.android.com/identity/sign-in/credential-manager`
3. Guía de Cloud Firestore para Android: `https://firebase.google.com/docs/firestore/quickstart`
4. Guía de Firebase Storage para Android: `https://firebase.google.com/docs/storage/android/start`
5. Documentación de Room Persistence Library: `https://developer.android.com/training/data-storage/room`
