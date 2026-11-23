package com.example.cityreport

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SearchView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cityreport.databinding.FragmentHomeBinding
import com.example.cityreport.model.Incidencia
import com.example.cityreport.model.IncidenciaRepositoryImpl
import com.example.cityreport.presenter.HomePresenter
import com.example.cityreport.view.contracts.HomeContract
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.materialswitch.MaterialSwitch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class HomeFragment : Fragment(), HomeContract.View {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var presenter: HomeContract.Presenter
    private lateinit var adapter: IncidenciaAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        presenter = HomePresenter(IncidenciaRepositoryImpl.getInstance())
        presenter.attachView(this)

        setupUI()
    }

    override fun onResume() {
        super.onResume()
        presenter.cargarIncidencias()
    }

    private fun setupUI() {
        adapter = IncidenciaAdapter { incidencia ->
            presenter.onIncidenciaSeleccionada(incidencia)
        }

        binding.rvIncidencias.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@HomeFragment.adapter
        }

        binding.btnEmpezar.setOnClickListener {
            presenter.onNuevaIncidenciaClick()
        }

        binding.toolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_search -> true
                R.id.action_settings -> {
                    mostrarDialogoAjustes()
                    true
                }
                else -> false
            }
        }

        val searchItem = binding.toolbar.menu.findItem(R.id.action_search)
        val searchView = searchItem?.actionView as? SearchView
        searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                presenter.filtrarIncidencias(query.orEmpty())
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                presenter.filtrarIncidencias(newText.orEmpty())
                return true
            }
        })
    }

    private fun mostrarDialogoAjustes() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_ajustes, null)
        val switchTemaOscuro = dialogView.findViewById<MaterialSwitch>(R.id.switchTemaOscuro)
        val switchNotificaciones = dialogView.findViewById<MaterialSwitch>(R.id.switchNotificaciones)

        val prefRepo = CityReportApp.instance.preferencesRepository

        lifecycleScope.launch {
            switchTemaOscuro.isChecked = prefRepo.isDarkThemeFlow.first()
            switchNotificaciones.isChecked = prefRepo.isNotificationsEnabledFlow.first()
        }

        switchTemaOscuro.setOnCheckedChangeListener { _, isChecked ->
            lifecycleScope.launch {
                prefRepo.setDarkTheme(isChecked)
                if (isChecked) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                }
            }
        }

        switchNotificaciones.setOnCheckedChangeListener { _, isChecked ->
            lifecycleScope.launch {
                prefRepo.setNotificationsEnabled(isChecked)
            }
        }

        MaterialAlertDialogBuilder(requireContext())
            .setView(dialogView)
            .setPositiveButton("Cerrar") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    override fun mostrarIncidencias(lista: List<Incidencia>) {
        adapter.submitList(lista)
    }

    override fun mostrarEstadoVacio(visible: Boolean) {
        if (visible) {
            binding.layoutEmpty.visibility = View.VISIBLE
            binding.rvIncidencias.visibility = View.GONE
        } else {
            binding.layoutEmpty.visibility = View.GONE
            binding.rvIncidencias.visibility = View.VISIBLE
        }
    }

    override fun navegarADetalle(id: Int) {
        val bundle = bundleOf("idIncidencia" to id)
        findNavController().navigate(R.id.toDetalleIncidencia, bundle)
    }

    override fun navegarANuevaIncidencia() {
        findNavController().navigate(R.id.toNuevaIncidencia)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        presenter.detachView()
        _binding = null
    }
}
