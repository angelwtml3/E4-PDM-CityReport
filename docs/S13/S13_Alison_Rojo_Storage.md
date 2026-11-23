# S13: Firebase Storage y Multimedia
**Integrante:** Alison Liliana Rojo Gómez  
**Matrícula:** 2024601281  
**Responsabilidad principal:** Firebase Storage, selección de imágenes desde Galería y gestión de multimedia  

---

## 1. Resumen Ejecutivo de la Responsabilidad
Como Integrante 3 del equipo, mi responsabilidad principal en la práctica S13 Flipped fue investigar e implementar la gestión de elementos multimedia en la nube utilizando **Firebase Storage**, permitiendo la selección de imágenes tanto desde la **Galería del dispositivo** (mediante Android Photo Picker) como desde la cámara nativa (CameraX), la carga de imágenes a la nube, la obtención de URLs de descarga públicas y la asociación de dichas imágenes con las incidencias del usuario.

---

## 2. Investigación Técnica sobre Firebase Storage y Android Photo Picker

### 2.1. Selector de Fotografías Moderno (`PickVisualMedia`)
En versiones recientes de Android (Android 13+ / Photo Picker de Play Services), solicitar el permiso general de almacenamiento `READ_EXTERNAL_STORAGE` está desaconsejado. En su lugar, se utilizó el contrato estandarizado **`ActivityResultContracts.PickVisualMedia()`**, el cual permite al usuario seleccionar imágenes de manera segura sin necesidad de conceder permisos globales de lectura de archivos.

### 2.2. Estructura de Almacenamiento en Firebase Storage
Para evitar colisiones de nombres entre imágenes de distintos usuarios y mantener los archivos organizados por reporte, se definió la siguiente estructura de carpetas en el bucket de Storage:

`users/{uid}/incidencias/{incidenciaId}/imagenes/foto_{timestamp}.jpg`

---

## 3. Configuración individual de Firebase y proceso de Google Sign-In

Conforme a las instrucciones del profesor, presento el procedimiento individual que realizo en mi rama de prueba (`feature/s13-alison-storage`):

### 1. Registro o selección del proyecto Firebase
- Acceso a Firebase Console y vinculación al proyecto del equipo `CityReport-Firebase`.

### 2. Registro de la aplicación Android
- Registro del identificador de paquete: `com.example.cityreport`.

### 3. Configuración del paquete
- Verificación del archivo `build.gradle.kts` y coincidencia del paquete `com.example.cityreport`.

### 4. Obtención de `google-services.json`
- Inclusión del archivo `google-services.json` descargado en el directorio `app/`.

### 5. Configuración de SHA-1
- Registro de la clave SHA-1 obtenida mediante `./gradlew signingReport` en Firebase Console.

### 6. Activación del proveedor Google
- Habilitación de Google como método de autenticación en Firebase Console.

### 7. Configuración OAuth Web Client ID
- Configuración del Web Client ID en las fuentes del proyecto.

### 8. Funcionamiento de Google Sign-In
- Comprobación del flujo de autenticación mediante Credential Manager para obtener el UID del usuario activo.

### 9. Conexión con Firebase Authentication
- Asociación del UID del usuario autenticado como prefijo del path de subida en Firebase Storage (`users/{uid}/...`).

### 10. Prueba individual de autenticación
- Comprobación en la rama `feature/s13-alison-storage` de que tras la autenticación se puede subir una imagen a Storage y obtener su URL de descarga vinculada al UID.

---

## 4. Funcionalidades Implementadas en `FirebaseStorageManager.kt` y `NuevaIncidenciaFragment.kt`

1. **Selección de Imágenes:** Integración de `pickMediaLauncher` en `NuevaIncidenciaFragment.kt` mediante `PickVisualMediaRequest(PickVisualMedia.ImageOnly)` a través del botón "Galería".
2. **Carga de Archivos:** Método `FirebaseStorageManager.subirFotoIncidencia(fileUri, userId, incidenciaId)` que invoca `putFile(fileUri).await()`.
3. **Obtención de URL:** Tras completar la carga, se consulta `ref.downloadUrl.await()` para generar la URL pública HTTPS de la imagen.
4. **Visualización:** Asignación de la URL o URI local en el `ImageView` de previsualización tanto en el formulario como en el detalle del reporte (`DetalleIncidenciaFragment.kt`).

---

## 5. Dificultades Encontradas y Soluciones Aplicadas

1. **Dificultad:** La selección de imágenes de la galería retornaba un URI de contenido provisional (`content://`) que expiraba al reiniciar la app si no se subía a la nube inmediatamente.
   - **Solución:** Se diseñó el flujo de subida de modo que la imagen local se cargue inmediatamente a Firebase Storage al pulsar "Guardar Reporte", reemplazando la URI local con la URL permanente de descarga de Firebase.

2. **Dificultad:** La carga de imágenes pesadas ralentizaba la experiencia si se ejecutaba en el hilo principal.
   - **Solución:** Las operaciones de subida y consulta de URL se encapsularon en corrutinas suspendidas ejecutadas en el hilo `Dispatchers.IO`.

---

## 6. Evidencias e Integración
- **Rama de Git asignada:** `feature/s13-alison-storage`
- **Mensaje de Commit:** `S13 - Firebase Storage - Alison Rojo`
- **Pruebas verificadas:** Compilación limpia de `FirebaseStorageManager.kt`, integración del Photo Picker en `NuevaIncidenciaFragment` y resolución exitosa de URLs de descarga.
