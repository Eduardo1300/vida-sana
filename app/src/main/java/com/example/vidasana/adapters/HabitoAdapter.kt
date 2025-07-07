package com.example.vidasana.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.vidasana.databinding.ItemHabitoBinding
import com.example.vidasana.database.HabitoDatabase
import com.example.vidasana.models.Habito
import kotlinx.coroutines.*

class HabitoAdapter(
    private val lista: List<Habito>,
    private val onHabitoEliminado: () -> Unit
) : RecyclerView.Adapter<HabitoAdapter.HabitoViewHolder>() {

    inner class HabitoViewHolder(val binding: ItemHabitoBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HabitoViewHolder {
        val binding = ItemHabitoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HabitoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HabitoViewHolder, position: Int) {
        val habito = lista[position]
        holder.binding.tvTipo.text = habito.tipo
        holder.binding.tvCantidad.text = habito.cantidad
        holder.binding.tvFecha.text = habito.fecha

        holder.binding.btnEliminar.setOnClickListener {
            val context = holder.itemView.context
            CoroutineScope(Dispatchers.IO).launch {
                HabitoDatabase.getDatabase(context).habitoDao().eliminar(habito)
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Hábito eliminado", Toast.LENGTH_SHORT).show()
                    onHabitoEliminado()
                }
            }
        }
    }

    override fun getItemCount() = lista.size
}
