package com.example.cityreport

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.cityreport.data.remote.FirebaseAuthManager
import com.example.cityreport.databinding.FragmentWelcomeBinding
import kotlinx.coroutines.launch

class WelcomeFragment : Fragment() {

    private var _binding: FragmentWelcomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWelcomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        actualizarEstadoAuthUI()

        binding.btnGoogleSignIn.setOnClickListener {
            iniciarGoogleAuth()
        }

        binding.btnCerrarSesion.setOnClickListener {
            FirebaseAuthManager.cerrarSesion()
            actualizarEstadoAuthUI()
            Toast.makeText(requireContext(), "Sesión cerrada correctamente", Toast.LENGTH_SHORT).show()
        }

        binding.btnIniciar.setOnClickListener {
            findNavController().navigate(R.id.action_welcomeFragment_to_homeFragment)
        }
    }

    private fun actualizarEstadoAuthUI() {
        val perfil = FirebaseAuthManager.obtenerPerfilUsuario()
        if (perfil != null) {
            binding.cardUserProfile.visibility = View.VISIBLE
            binding.btnGoogleSignIn.visibility = View.GONE

            binding.tvUserName.text = perfil.nombre
            binding.tvUserEmail.text = perfil.email
            binding.tvUserUid.text = "UID: ${perfil.uid}"
        } else {
            binding.cardUserProfile.visibility = View.GONE
            binding.btnGoogleSignIn.visibility = View.VISIBLE
        }
    }

    private fun iniciarGoogleAuth() {
        val webClientId = getString(R.string.default_web_client_id_placeholder)
        lifecycleScope.launch {
            val result = FirebaseAuthManager.iniciarSesionConGoogle(requireContext(), webClientId)
            val user = result.getOrNull()
            if (user != null) {
                actualizarEstadoAuthUI()
                Toast.makeText(requireContext(), "Bienvenido ${user.displayName.orEmpty()}", Toast.LENGTH_SHORT).show()
            } else {
                val exc = result.exceptionOrNull()
                Toast.makeText(requireContext(), "Inicio de sesión: ${exc?.message ?: "Error desconocido"}", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
