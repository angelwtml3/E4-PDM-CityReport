package com.example.cityreport

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.cityreport.databinding.FragmentDetalleIncidenciaBinding
import com.example.cityreport.model.Incidencia
import com.example.cityreport.model.IncidenciaRepositoryImpl
import com.example.cityreport.presenter.DetalleIncidenciaPresenter
import com.example.cityreport.view.contracts.DetalleIncidenciaContract
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import java.util.Locale

class DetalleIncidenciaFragment : Fragment(), DetalleIncidenciaContract.View, OnMapReadyCallback {

    private var _binding: FragmentDetalleIncidenciaBinding? = null
    private val binding get() = _binding!!

    private lateinit var presenter: DetalleIncidenciaContract.Presenter
    private var idIncidencia: Int = -1
    private var incidenciaActual: Incidencia? = null
    private var googleMap: GoogleMap? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetalleIncidenciaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        presenter = DetalleIncidenciaPresenter(IncidenciaRepositoryImpl.getInstance())
        presenter.attachView(this)

        idIncidencia = arguments?.getInt("idIncidencia") ?: -1

        binding.mapViewDetalle.onCreate(savedInstanceState)
        binding.mapViewDetalle.getMapAsync(this)

        setupListeners()
        presenter.cargarDetalle(idIncidencia)
    }

    private fun setupListeners() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        binding.btnVolver.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.btnCerrar.setOnClickListener {
            val resuelta = incidenciaActual?.resuelta == true
            presenter.cambiarEstadoResuelta(idIncidencia, !resuelta)
        }
    }

    override fun mostrarDetalle(incidencia: Incidencia) {
        incidenciaActual = incidencia

        binding.tvTituloDetalle.text = incidencia.titulo
        binding.tvDescripcionDetalle.text = incidencia.descripcion
        binding.tvCategoriaDetalle.text = incidencia.categoria.nombreDisplay
        binding.tvFechaDetalle.text = "Reportado el ${incidencia.fecha.ifBlank { "Reciente" }}"
        binding.tvCoordenadasDetalle.text = String.format(
            Locale.getDefault(),
            "Latitud: %.4f | Longitud: %.4f",
            incidencia.latitud,
            incidencia.longitud
        )

        // Foto
        if (!incidencia.fotoUri.isNullOrBlank()) {
            try {
                binding.ivDetalle.visibility = View.VISIBLE
                binding.tvSinFoto.visibility = View.GONE
                binding.ivDetalle.setImageURI(Uri.parse(incidencia.fotoUri))
            } catch (e: Exception) {
                binding.ivDetalle.visibility = View.GONE
                binding.tvSinFoto.visibility = View.VISIBLE
            }
        } else {
            binding.ivDetalle.visibility = View.GONE
            binding.tvSinFoto.visibility = View.VISIBLE
        }

        // Estado
        actualizarEstadoUI(incidencia.resuelta)

        // Actualizar Mapa
        val latLng = LatLng(incidencia.latitud, incidencia.longitud)
        googleMap?.run {
            clear()
            addMarker(MarkerOptions().position(latLng).title(incidencia.titulo))
            moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
        }
    }

    private fun actualizarEstadoUI(resuelta: Boolean) {
        val context = requireContext()
        if (resuelta) {
            binding.tvStatusDetalle.text = "SOLUCIONADA"
            binding.tvStatusDetalle.setTextColor(ContextCompat.getColor(context, R.color.status_resolved))
            binding.tvStatusDetalle.backgroundTintList =
                ContextCompat.getColorStateList(context, R.color.status_resolved_container)
            binding.btnCerrar.text = "Reabrir Incidencia"
        } else {
            binding.tvStatusDetalle.text = "PENDIENTE"
            binding.tvStatusDetalle.setTextColor(ContextCompat.getColor(context, R.color.status_pending))
            binding.tvStatusDetalle.backgroundTintList =
                ContextCompat.getColorStateList(context, R.color.status_pending_container)
            binding.btnCerrar.text = "Marcar como Solucionada"
        }
    }

    override fun mostrarError(mensaje: String) {
        Toast.makeText(requireContext(), mensaje, Toast.LENGTH_SHORT).show()
    }

    override fun notificarEstadoActualizado(resuelta: Boolean) {
        val mensaje = if (resuelta) "Incidencia marcada como solucionada" else "Incidencia reabierta"
        Toast.makeText(requireContext(), mensaje, Toast.LENGTH_SHORT).show()
        incidenciaActual = incidenciaActual?.copy(resuelta = resuelta)
        actualizarEstadoUI(resuelta)
    }

    override fun cerrarPantalla() {
        findNavController().popBackStack()
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        incidenciaActual?.let { inc ->
            try {
                val latLng = LatLng(inc.latitud, inc.longitud)
                map.addMarker(MarkerOptions().position(latLng).title(inc.titulo))
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
                binding.layoutMapDetalleFallback.visibility = View.GONE
            } catch (e: Exception) {
                binding.layoutMapDetalleFallback.visibility = View.VISIBLE
            }
        }
    }

    override fun onResume() {
        super.onResume()
        binding.mapViewDetalle.onResume()
    }

    override fun onPause() {
        super.onPause()
        binding.mapViewDetalle.onPause()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.mapViewDetalle.onDestroy()
        presenter.detachView()
        _binding = null
    }

    override fun onLowMemory() {
        super.onLowMemory()
        binding.mapViewDetalle.onLowMemory()
    }
}
