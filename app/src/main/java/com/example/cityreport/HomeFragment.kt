package com.example.cityreport

import android.os.Bundle
import android.util.Log // <-- Importante para el Log.d
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.cityreport.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Asignación directa usando ViewBinding exigida en el curso[cite: 1]
        binding.tvBienvenida.text = "¡Bienvenido a CityReport!"

        binding.btnEmpezar.setOnClickListener {
            // Mensaje de depuración en consola exigido por la práctica[cite: 1]
            Log.d("CityReport", "Botón presionado")

            // Aquí puedes dejar la navegación hacia la siguiente pantalla cuando gustes:
            // findNavController().navigate(R.id.toNuevaIncidencia)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}