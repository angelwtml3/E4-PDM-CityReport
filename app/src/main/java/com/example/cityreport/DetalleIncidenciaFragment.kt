package com.example.cityreport

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.cityreport.databinding.FragmentDetalleIncidenciaBinding

class DetalleIncidenciaFragment : Fragment() {

    private var _binding: FragmentDetalleIncidenciaBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetalleIncidenciaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Configuración de la toolbar para retroceso
        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        // Datos simulados (en una app real se pasarían por SafeArgs o ViewModel)
        binding.tvTituloDetalle.text = "Bache en calle Principal"
        binding.tvDescripcionDetalle.text = "Este es un reporte detallado sobre el bache en la calle principal. Afecta a muchos vehículos diariamente y es peligroso por la noche. Se requiere intervención urgente de la cuadrilla de bacheo."
        
        binding.btnCerrar.setOnClickListener {
            // Acción de cerrar incidencia
            findNavController().popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}