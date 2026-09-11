package com.example.cityreport

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.cityreport.databinding.FragmentNuevaIncidenciaBinding
import com.example.cityreport.model.Incidencia
import com.example.cityreport.model.IncidenciaRepositoryImpl

class NuevaIncidenciaFragment : Fragment() {

    private var _binding: FragmentNuevaIncidenciaBinding? = null
    private val binding get() = _binding!!

    private val repository = IncidenciaRepositoryImpl()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNuevaIncidenciaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnGuardar.setOnClickListener {
            val titulo = binding.etTitulo.text.toString()
            val descripcion = binding.etDescripcion.text.toString()

            // Validación básica
            if (titulo.isNotBlank() && descripcion.isNotBlank()) {
                // Creamos el objeto del modelo
                val nuevaIncidencia = Incidencia(
                    id = 0, // El repositorio asignará el ID final
                    titulo = titulo,
                    descripcion = descripcion
                )

                // Guardamos la información
                repository.guardar(nuevaIncidencia)

                // Regresamos a la pantalla anterior (Home)
                findNavController().popBackStack()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}