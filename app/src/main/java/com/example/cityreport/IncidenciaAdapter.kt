package com.example.cityreport

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.cityreport.databinding.ItemIncidenciaBinding
import com.example.cityreport.model.Incidencia

class IncidenciaAdapter : ListAdapter<Incidencia, IncidenciaAdapter.ViewHolder>(IncidenciaDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemIncidenciaBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(private val binding: ItemIncidenciaBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(incidencia: Incidencia) {
            // ViewBinding nos permite acceder a los TextView directamente
            binding.tvTitulo.text = incidencia.titulo
            binding.tvDescripcion.text = incidencia.descripcion
        }
    }

    class IncidenciaDiffCallback : DiffUtil.ItemCallback<Incidencia>() {
        override fun areItemsTheSame(oldItem: Incidencia, newItem: Incidencia): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Incidencia, newItem: Incidencia): Boolean {
            return oldItem == newItem
        }
    }
}