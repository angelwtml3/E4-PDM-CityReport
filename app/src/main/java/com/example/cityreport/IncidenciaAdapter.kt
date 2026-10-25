package com.example.cityreport

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.cityreport.databinding.ItemIncidenciaBinding
import com.example.cityreport.model.Incidencia

class IncidenciaAdapter(
    private val onItemSelected: (Incidencia) -> Unit
) : ListAdapter<Incidencia, IncidenciaAdapter.ViewHolder>(IncidenciaDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemIncidenciaBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding, onItemSelected)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(
        private val binding: ItemIncidenciaBinding,
        private val onItemSelected: (Incidencia) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(incidencia: Incidencia) {
            binding.tvTitulo.text = incidencia.titulo
            binding.tvDescripcion.text = incidencia.descripcion
            binding.tvCategoria.text = incidencia.categoria.nombreDisplay
            binding.tvFecha.text = incidencia.fecha.ifBlank { "Reciente" }

            // Imagen / Thumbnail
            if (!incidencia.fotoUri.isNullOrBlank()) {
                try {
                    binding.ivThumb.visibility = View.VISIBLE
                    binding.ivThumb.setImageURI(Uri.parse(incidencia.fotoUri))
                } catch (e: Exception) {
                    binding.ivThumb.visibility = View.GONE
                }
            } else {
                binding.ivThumb.visibility = View.GONE
            }

            // Lógica de estado
            val context = binding.root.context
            if (incidencia.resuelta) {
                binding.tvStatus.text = "SOLUCIONADA"
                binding.tvStatus.setTextColor(ContextCompat.getColor(context, R.color.status_resolved))
                binding.tvStatus.backgroundTintList =
                    ContextCompat.getColorStateList(context, R.color.status_resolved_container)
            } else {
                binding.tvStatus.text = "PENDIENTE"
                binding.tvStatus.setTextColor(ContextCompat.getColor(context, R.color.status_pending))
                binding.tvStatus.backgroundTintList =
                    ContextCompat.getColorStateList(context, R.color.status_pending_container)
            }

            binding.root.setOnClickListener { onItemSelected(incidencia) }
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
