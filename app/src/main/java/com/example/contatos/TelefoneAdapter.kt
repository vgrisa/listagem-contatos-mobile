package com.example.contatos

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView

class TelefoneAdapter(private var telefones: MutableList<Telefone>, context: Context) :
    RecyclerView.Adapter<TelefoneAdapter.TelefoneViewHolder>() {

    private val db: ContatosDatabase = ContatosDatabase(context)

    class TelefoneViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val telefoneTextView: TextView = itemView.findViewById(R.id.telefoneTextView)
        val tipoTextView: TextView = itemView.findViewById(R.id.tipoTextView)
        val deleteButton: ImageView = itemView.findViewById(R.id.deleteButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TelefoneViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_telefone, parent, false)
        return TelefoneViewHolder(view)
    }

    override fun getItemCount(): Int = telefones.size

    override fun onBindViewHolder(holder: TelefoneViewHolder, position: Int) {
        val telefone = telefones[position]
        holder.telefoneTextView.text = telefone.telefone
        holder.tipoTextView.text = telefone.tipo

        holder.deleteButton.setOnClickListener {
            // Remove o telefone da lista e do banco de dados, se necessário
            telefones.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, telefones.size)

            Toast.makeText(holder.itemView.context, "Telefone removido da lista", Toast.LENGTH_SHORT).show()
        }
    }

    // Função para atualizar a lista de telefones (caso seja necessário atualizar toda a lista)
    fun refreshData(newPhones: List<Telefone>) {
        telefones.clear()
        telefones.addAll(newPhones)
        notifyDataSetChanged()
    }
}
