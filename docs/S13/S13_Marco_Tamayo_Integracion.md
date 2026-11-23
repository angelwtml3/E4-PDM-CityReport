# S13: Integración General, Persistencia y Sincronización
**Integrante:** Marco Antonio Tamayo Álvarez  
**Matrícula:** 2025600436  
**Responsabilidad principal:** Integración general de módulos, mantenimiento de Room/DataStore y sincronización en la nube  

---

## 1. Resumen Ejecutivo de la Responsabilidad
Como Integrante 4 del equipo, mi responsabilidad principal en la práctica S13 Flipped fue coordinar la arquitectura global del proyecto, integrando las contribuciones de los tres módulos anteriores (Authentication, Firestore y Storage) con las funcionalidades locales desarrolladas en S12 (Room Database y Preferences DataStore).

Garanticé que el sistema funcione en **modo híbrido y offline-first**: los reportes se guardan de forma instantánea en Room y las preferencias en DataStore, y cuando existe conexión a Internet y el usuario está autenticado, los datos y fotografías se sincronizan automáticamente con Cloud Firestore y Firebase Storage.

---

## 2. Análisis de la Arquitectura de Integración

### 2.1. Conservación de las Funcionalidades de S12
- **Room Database (`city_report.db`):** Continúa actuando como la fuente primaria de verdad local. Las consultas para el `RecyclerView` leen de Room, asegurando que el usuario pueda consultar sus incidencias sin importar su estado de conectividad.
- **Preferences DataStore (`user_preferences`):** Mantiene la administración del Tema Oscuro (`dark_theme`) y el estado de Notificaciones (`notifications_enabled`) localmente en el dispositivo.

### 2.2. Coordinación mediante `SyncRepository.kt`
Se creó la clase `SyncRepository.kt` para orquestar la sincronización entre capas:
1. **Guardado Local:** Invoca `IncidenciaRepositoryImpl` para persistir la incidencia en Room.
2. **Carga Multimedia:** Si existe un URI de imagen y el usuario está autenticado, llama a `FirebaseStorageManager.subirFotoIncidencia()`.
3. **Sincronización Cloud:** Actualiza el registro con la URL remota y sincroniza la estructura del documento en `FirestoreRepository.guardarIncidencia()`.
4. **Resincronización Nube-Local:** El método `sincronizarConNube()` descarga los documentos de Firestore del usuario y los consolida en Room.

---

## 3. Configuración individual de Firebase y proceso de Google Sign-In

Conforme al requerimiento individual del docente, expongo el proceso que ejecuto en mi rama de prueba (`feature/s13-marco-integration`):

### 1. Registro o selección del proyecto Firebase
- Selección del proyecto `CityReport-Firebase` en Firebase Console.

### 2. Registro de la aplicación Android
- Confirmación del registro de la app Android con paquete `com.example.cityreport`.

### 3. Configuración del paquete
- Validación de que todas las declaraciones del paquete `com.example.cityreport` estén unificadas en los manifiestos y scripts de compilación.

### 4. Obtención de `google-services.json`
- Inclusión del archivo `google-services.json` en el directorio `app/`.

### 5. Configuración de SHA-1
- Ejecución de `./gradlew signingReport` para extraer la clave SHA-1 e ingresarla en Firebase Console.

### 6. Activación del proveedor Google
- Verificación del estado activo del proveedor Google en la consola de Firebase Authentication.

### 7. Configuración OAuth Web Client ID
- Verificación de la presencia del Web Client ID en las configuraciones de cadena de recursos.

### 8. Funcionamiento de Google Sign-In
- Prueba del flujo completo de inicio de sesión con Credential Manager y manejo de tokens.

### 9. Conexión con Firebase Authentication
- Verificación de la propagación del UID de sesión hacia los repositorios de sincronización (`SyncRepository`).

### 10. Prueba individual de autenticación
- Comprobación en la rama `feature/s13-marco-integration` de que la autenticación dispara la sincronización en segundo plano entre Room y Firestore sin bloquear la interfaz.

---

## 4. Dificultades Encontradas y Soluciones Aplicadas

1. **Dificultad:** Evitar duplicación de registros cuando una incidencia se guarda primero en Room y luego se sincroniza en Firestore.
   - **Solución:** Se utilizó el identificador de la entidad de Room como clave única del documento en Firestore. Al recibir datos de la nube, Room realiza un `OnConflictStrategy.REPLACE`, evitando registros duplicados.

2. **Dificultad:** Preservar la experiencia de usuario si falla la conexión a Internet al intentar subir la foto a Storage.
   - **Solución:** `SyncRepository` guarda el reporte en Room con el URI de la foto local. Si la subida a Storage falla por red, la incidencia se mantiene visible localmente y queda disponible para resincronizarse posteriormente.

---

## 5. Evidencias e Integración
- **Rama de Git asignada:** `feature/s13-marco-integration`
- **Mensaje de Commit:** `S13 - Integracion y sincronizacion - Marco Tamayo`
- **Pruebas verificadas:** Compilación limpia del proyecto, 5 pruebas unitarias automatizadas superadas (`5 passed, 0 failed`) e integración armónica entre Room, DataStore, Auth, Firestore y Storage.
