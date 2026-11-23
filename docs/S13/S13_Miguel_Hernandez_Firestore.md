# S13: Cloud Firestore y Modelado de Datos en Nube
**Integrante:** Miguel Ángel Hernández Herrera  
**Matrícula:** 2024601990  
**Responsabilidad principal:** Cloud Firestore, diseño de colecciones y operaciones CRUD  

---

## 1. Resumen Ejecutivo de la Responsabilidad
Como Integrante 2 del equipo, mi responsabilidad principal en la práctica S13 Flipped fue investigar, diseñar e implementar la integración con **Cloud Firestore** para almacenar, consultar, actualizar y eliminar las incidencias reportadas por los usuarios en la nube, garantizando que cada registro esté vinculado al UID del usuario autenticado.

---

## 2. Investigación Técnica sobre Cloud Firestore

### 2.1. Ventajas de Firestore frente a bases de datos relacionales
Cloud Firestore es una base de datos NoSQL orientada a documentos en tiempo real. Organiza los datos en colecciones y documentos JSON, ofreciendo sincronización automática en tiempo real, soporte nativo de persistencia offline y escalabilidad automática.

### 2.2. Diseño de la Estructura de Colecciones
Para garantizar que la información de cada usuario esté aislada y protegida según los principios de seguridad de Firebase, se diseñó la siguiente jerarquía:

`users/{uid}/incidencias/{incidenciaId}`

#### Esquema del Documento `Incidencia`:
- `id` (`Number`): Identificador numérico local/Room de la incidencia.
- `titulo` (`String`): Título del reporte.
- `descripcion` (`String`): Descripción detallada del problema.
- `categoria` (`String`): Categoría (`VIALIDAD`, `AGUA`, `ALUMBRADO`, `BASURA`).
- `latitud` (`Number`): Coordenada GPS de latitud.
- `longitud` (`Number`): Coordenada GPS de longitud.
- `fotoUri` (`String`): URL de descarga de la imagen almacenada en Firebase Storage (o URI local).
- `fecha` (`String`): Fecha de registro en formato `dd/MM/yyyy`.
- `resuelta` (`Boolean`): Estado de la incidencia (true = Solucionada, false = Pendiente).
- `userId` (`String`): UID del propietario obtenido de Firebase Authentication.

---

## 3. Configuración individual de Firebase y proceso de Google Sign-In

Conforme a las instrucciones del docente, detallo el procedimiento individual que realizo en mi rama de prueba (`feature/s13-miguel-firestore`):

### 1. Registro o selección del proyecto Firebase
- Ingreso a Firebase Console y apertura del proyecto compartido `CityReport-Firebase`.

### 2. Registro de la aplicación Android
- Registro del nombre de paquete de la aplicación Android: `com.example.cityreport`.

### 3. Configuración del paquete
- Verificación de la coincidencia del paquete `com.example.cityreport` en `AndroidManifest.xml` y `build.gradle.kts`.

### 4. Obtención de `google-services.json`
- Descarga del archivo de configuración `google-services.json` e inclusión en la carpeta `app/`.

### 5. Configuración de SHA-1
- Generación de la huella digital SHA-1 mediante `./gradlew signingReport` y registro en la consola de Firebase.

### 6. Activación del proveedor Google
- En Firebase Console -> Authentication -> Sign-in method, habilitación de Google Authentication.

### 7. Configuración OAuth Web Client ID
- Copia del Web Client ID asignado y configuración en el proyecto Android.

### 8. Funcionamiento de Google Sign-In
- Validación de que al autenticarse mediante Credential Manager se obtenga el token de Google y se cree la sesión en Firebase Auth.

### 9. Conexión con Firebase Authentication
- Vinculación del UID devuelto por `FirebaseAuth.getInstance().currentUser` con las peticiones de lectura y escritura en Firestore.

### 10. Prueba individual de autenticación
- Verificación en la rama `feature/s13-miguel-firestore` de que el inicio de sesión genera un UID único que se asigna como ruta raíz de las incidencias en Firestore (`users/{uid}/incidencias`).

---

## 4. Operaciones CRUD Implementadas en `FirestoreRepository.kt`

- **Guardar / Insertar:** `FirestoreRepository.guardarIncidencia(incidencia, userId)` escribe el documento en `users/{userId}/incidencias/{id}` utilizando `.set(mapData).await()`.
- **Consultar / Leer:** `FirestoreRepository.obtenerIncidencias(userId)` realiza una lectura mediante `.collection("users").document(userId).collection("incidencias").get().await()` y mapea los documentos a objetos `Incidencia`.
- **Actualizar Estado:** `FirestoreRepository.actualizarEstado(incidenciaId, resuelta, userId)` actualiza el campo `resuelta` mediante `.update("resuelta", resuelta)`.
- **Eliminar:** `FirestoreRepository.eliminarIncidencia(incidenciaId, userId)` borra el documento mediante `.delete()`.

---

## 5. Dificultades Encontradas y Soluciones Aplicadas

1. **Dificultad:** Desincronización entre el ID entero generado por Room (`Int`) y las claves de documento de Firestore (`String`).
   - **Solución:** Se utilizó el valor en texto `incidencia.id.toString()` como identificador del documento en Firestore, manteniendo el ID entero dentro del mapa del documento para reconstruir el objeto `Incidencia` de forma transparente.

2. **Dificultad:** Operaciones de red bloqueantes en el hilo principal de la UI.
   - **Solución:** Todas las llamadas a Firestore se implementaron utilizando corrutinas en Kotlin con `Dispatchers.IO` y la extensión `kotlinx.coroutines.tasks.await()`.

---

## 6. Evidencias e Integración
- **Rama de Git asignada:** `feature/s13-miguel-firestore`
- **Mensaje de Commit:** `S13 - Cloud Firestore - Miguel Hernandez`
- **Pruebas verificadas:** Compilación exitosa de `FirestoreRepository.kt`, operaciones CRUD remotas y mapping bidireccional entre mapas de Firestore y la entidad `Incidencia`.
