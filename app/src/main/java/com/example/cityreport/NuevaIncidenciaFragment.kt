package com.example.cityreport

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.cityreport.databinding.FragmentNuevaIncidenciaBinding
import com.example.cityreport.model.Categoria
import com.example.cityreport.model.IncidenciaRepositoryImpl
import com.example.cityreport.presenter.NuevaIncidenciaPresenter
import com.example.cityreport.util.NotificationHelper
import com.example.cityreport.view.contracts.NuevaIncidenciaContract
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class NuevaIncidenciaFragment : Fragment(), NuevaIncidenciaContract.View, OnMapReadyCallback {

    private var _binding: FragmentNuevaIncidenciaBinding? = null
    private val binding get() = _binding!!

    private lateinit var presenter: NuevaIncidenciaContract.Presenter
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    // CameraX
    private var imageCapture: ImageCapture? = null
    private lateinit var cameraExecutor: ExecutorService
    private var fotoCapturadaUri: String? = null

    // Ubicación
    private var latitudActual: Double = 19.4326
    private var longitudActual: Double = -99.1332
    private var googleMap: GoogleMap? = null

    // Categoría seleccionada
    private var categoriaSeleccionada: Categoria = Categoria.VIALIDAD

    // Launcher de Permisos Cámara
    private val requestCameraPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            iniciarCamara()
        } else {
            Toast.makeText(requireContext(), "Permiso de cámara denegado", Toast.LENGTH_SHORT).show()
        }
    }

    // Launcher de Permisos Ubicación
    private val requestLocationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
        val coarseGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false
        if (fineGranted || coarseGranted) {
            obtenerUbicacionGPS()
        } else {
            Toast.makeText(
                requireContext(),
                "Permiso de ubicación denegado. Se usarán coordenadas por defecto.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    // Launcher de Permisos Notificaciones (Android 13+)
    private val requestNotificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { _ ->
        // No se requiere acción adicional tras la solicitud
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNuevaIncidenciaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        presenter = NuevaIncidenciaPresenter(IncidenciaRepositoryImpl.getInstance())
        presenter.attachView(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        cameraExecutor = Executors.newSingleThreadExecutor()

        binding.mapView.onCreate(savedInstanceState)
        binding.mapView.getMapAsync(this)

        setupToolbar()
        setupListeners()
        presenter.cargarCategorias()

        // Solicitar permisos de notificación si aplica
        solicitarPermisosNotificacion()
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun setupListeners() {
        binding.btnAbrirCamara.setOnClickListener {
            solicitarPermisosCamara()
        }

        binding.btnTomarFoto.setOnClickListener {
            capturarFoto()
        }

        binding.btnRepetirFoto.setOnClickListener {
            solicitarPermisosCamara()
        }

        binding.btnObtenerUbicacion.setOnClickListener {
            solicitarPermisosUbicacion()
        }

        binding.btnGuardar.setOnClickListener {
            val titulo = binding.etTitulo.text?.toString().orEmpty()
            val descripcion = binding.etDescripcion.text?.toString().orEmpty()

            presenter.guardarReporte(
                titulo = titulo,
                descripcion = descripcion,
                categoria = categoriaSeleccionada,
                lat = latitudActual,
                lng = longitudActual,
                fotoUri = fotoCapturadaUri
            )
        }
    }

    // --- Implementación de NuevaIncidenciaContract.View ---

    override fun mostrarCategorias(categorias: List<Categoria>) {
        val nombres = categorias.map { it.nombreDisplay }
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, nombres)
        binding.actCategoria.setAdapter(adapter)

        binding.actCategoria.setText(categorias.first().nombreDisplay, false)
        categoriaSeleccionada = categorias.first()

        binding.actCategoria.setOnItemClickListener { _, _, position, _ ->
            categoriaSeleccionada = categorias[position]
        }
    }

    override fun mostrarCoordenadas(lat: Double, lng: Double) {
        latitudActual = lat
        longitudActual = lng
        binding.tvCoordenadas.text = String.format(Locale.getDefault(), "Coordenadas: Lat %.4f, Lng %.4f", lat, lng)
        binding.tvPrecision.text = "Ubicación obtenida exitosamente vía GPS."

        val latLng = LatLng(lat, lng)
        googleMap?.run {
            clear()
            addMarker(MarkerOptions().position(latLng).title("Ubicación del reporte"))
            moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
        }
    }

    override fun mostrarFotoCapturada(fotoUri: String) {
        fotoCapturadaUri = fotoUri
        binding.cameraPreviewView.visibility = View.GONE
        binding.layoutPlaceholderFoto.visibility = View.GONE
        binding.ivFotoPreview.visibility = View.VISIBLE
        binding.ivFotoPreview.setImageURI(Uri.parse(fotoUri))

        binding.btnTomarFoto.visibility = View.GONE
        binding.btnAbrirCamara.visibility = View.GONE
        binding.btnRepetirFoto.visibility = View.VISIBLE
    }

    override fun mostrarErrorValidacion(mensaje: String) {
        Toast.makeText(requireContext(), mensaje, Toast.LENGTH_SHORT).show()
    }

    override fun mostrarMensajeExito(mensaje: String) {
        Toast.makeText(requireContext(), mensaje, Toast.LENGTH_SHORT).show()
    }

    override fun reporteGuardadoExitosamente() {
        NotificationHelper.mostrarNotificacionReporteCreado(
            requireContext(),
            binding.etTitulo.text?.toString().orEmpty().ifBlank { "Nuevo Reporte" },
            (System.currentTimeMillis() % 10000).toInt()
        )
        findNavController().popBackStack()
    }

    override fun solicitarPermisosCamara() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            iniciarCamara()
        } else {
            requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    override fun solicitarPermisosUbicacion() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            obtenerUbicacionGPS()
        } else {
            requestLocationPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    override fun solicitarPermisosNotificacion() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestNotificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    // --- CameraX Logic ---

    private fun iniciarCamara() {
        binding.layoutPlaceholderFoto.visibility = View.GONE
        binding.ivFotoPreview.visibility = View.GONE
        binding.cameraPreviewView.visibility = View.VISIBLE

        binding.btnAbrirCamara.visibility = View.GONE
        binding.btnRepetirFoto.visibility = View.GONE
        binding.btnTomarFoto.visibility = View.VISIBLE

        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(binding.cameraPreviewView.surfaceProvider)
            }

            imageCapture = ImageCapture.Builder().build()
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture)
            } catch (exc: Exception) {
                Toast.makeText(requireContext(), "Error al iniciar la cámara: ${exc.message}", Toast.LENGTH_SHORT).show()
            }
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun capturarFoto() {
        val imageCapture = imageCapture ?: return

        val photoFile = File(
            requireContext().cacheDir,
            "JPEG_" + SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(System.currentTimeMillis()) + ".jpg"
        )

        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(requireContext()),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Toast.makeText(requireContext(), "Error al guardar foto: ${exc.message}", Toast.LENGTH_SHORT).show()
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    val savedUri = Uri.fromFile(photoFile)
                    presenter.onFotoCapturada(savedUri.toString())
                }
            }
        )
    }

    // --- FusedLocation Logic ---

    @SuppressLint("MissingPermission")
    private fun obtenerUbicacionGPS() {
        fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
            .addOnSuccessListener { location ->
                if (location != null) {
                    presenter.onUbicacionObtenida(location.latitude, location.longitude)
                } else {
                    fusedLocationClient.lastLocation.addOnSuccessListener { lastLoc ->
                        if (lastLoc != null) {
                            presenter.onUbicacionObtenida(lastLoc.latitude, lastLoc.longitude)
                        } else {
                            Toast.makeText(requireContext(), "No se pudo obtener la ubicación actual", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Error al consultar GPS: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    // --- Google Maps Callback ---

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        try {
            val initialLatLng = LatLng(latitudActual, longitudActual)
            map.addMarker(MarkerOptions().position(initialLatLng).title("Ubicación por defecto"))
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(initialLatLng, 14f))
            binding.layoutMapFallback.visibility = View.GONE
        } catch (e: Exception) {
            // Si la API key no está configurada, mostrar fallback visual
            binding.layoutMapFallback.visibility = View.VISIBLE
        }
    }

    // --- MapView Lifecycle ---

    override fun onResume() {
        super.onResume()
        binding.mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        binding.mapView.onPause()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.mapView.onDestroy()
        cameraExecutor.shutdown()
        presenter.detachView()
        _binding = null
    }

    override fun onLowMemory() {
        super.onLowMemory()
        binding.mapView.onLowMemory()
    }
}
