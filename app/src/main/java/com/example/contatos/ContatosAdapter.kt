package com.example.contatos

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView

class ContatosAdapter(private var contatos: List<Contato>, context: Context) :
    RecyclerView.Adapter<ContatosAdapter.ContatoViewHolder>() {

        private val db: ContatosDatabase = ContatosDatabase(context)

    class ContatoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val nomeTextView: TextView = itemView.findViewById(R.id.nomeTextView)
        val telefoneTextView: TextView = itemView.findViewById(R.id.telefoneTextView)
        val tipoTextView: TextView = itemView.findViewById(R.id.tipoTextView)

        val updateButton: ImageView = itemView.findViewById(R.id.updateButton)
        val deleteButton: ImageView = itemView.findViewById(R.id.deleteButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContatoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_contato, parent, false)
        return ContatoViewHolder(view)
    }

    override fun getItemCount(): Int = contatos.size

    override fun onBindViewHolder(holder: ContatoViewHolder, position: Int) {
        val contato = contatos[position]
        holder.nomeTextView.text = contato.nome
        holder.telefoneTextView.text =  contato.telefone
        holder.tipoTextView.text =  contato.tipo

        holder.updateButton.setOnClickListener{
            val intent = Intent(holder.itemView.context, UpdateContato::class.java).apply{
                putExtra("contato_id", contato.id)
            }

            holder.itemView.context.startActivity(intent)
        }

        holder.deleteButton.setOnClickListener{
            db.deleteContact(contato.id)
            refreshData(db.getAllContacts())
            Toast.makeText(holder.itemView.context, "Contato deletado com sucesso", Toast.LENGTH_SHORT).show()
        }
    }

    fun refreshData(newContacts: List<Contato>){
        contatos = newContacts
        notifyDataSetChanged()
    }

}