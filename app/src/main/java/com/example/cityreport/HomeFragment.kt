package com.example.cityreport

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cityreport.databinding.FragmentHomeBinding
import com.example.cityreport.model.Incidencia
import com.example.cityreport.model.IncidenciaRepositoryImpl
import com.example.cityreport.presenter.HomePresenter
import com.example.cityreport.view.contracts.HomeContract

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
