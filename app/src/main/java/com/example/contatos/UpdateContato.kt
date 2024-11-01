package com.example.contatos

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.contatos.databinding.ActivityUpdateContatoBinding

class UpdateContato : AppCompatActivity() {
    private lateinit var binding: ActivityUpdateContatoBinding
    private lateinit var db: ContatosDatabase
    private var contatoId: Int = -1
    private val telefones: MutableList<Telefone> = mutableListOf()
    private lateinit var telefonesAdapter: TelefoneAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpdateContatoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = ContatosDatabase(this)

        // Configura o Spinner com os tipos de telefone
        ArrayAdapter.createFromResource(
            this,
            R.array.tipo_contato_options,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.updateTipoContato.adapter = adapter
        }

        // Recupera o contatoId do Intent
        contatoId = intent.getIntExtra("contato_id", -1)

        // Verifica se o contatoId é válido
        if (contatoId == -1) {
            Toast.makeText(this, "Contato não encontrado", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Carrega as informações do contato e seus telefones
        val contact = db.getContactByID(contatoId)
        if (contact != null) {
            binding.updateNomeContato.setText(contact.nome)
            telefones.addAll(contact.telefones)
        }

        // Configura o RecyclerView para mostrar os telefones
        telefonesAdapter = TelefoneAdapter(telefones, this)
        binding.telefonesRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.telefonesRecyclerView.adapter = telefonesAdapter

        // Adiciona um novo telefone
        binding.addContatoBotao.setOnClickListener {
            val telefone = binding.updateTelefoneContato.text.toString()
            val tipo = binding.updateTipoContato.selectedItem.toString()

            if (telefone.isBlank()) {
                Toast.makeText(this, "Campo telefone é obrigatório", Toast.LENGTH_SHORT).show()
            } else if (tipo.isBlank()) {
                Toast.makeText(this, "Campo tipo é obrigatório", Toast.LENGTH_SHORT).show()
            } else {
                // Adiciona o telefone à lista
                val telefoneC = Telefone(0, contatoId, telefone, tipo)
                telefones.add(telefoneC)
                telefonesAdapter.notifyDataSetChanged()

                // Limpa os campos de entrada
                binding.updateTelefoneContato.text.clear()
                binding.updateTipoContato.setSelection(0)
            }
        }

        // Salva as alterações
        binding.updateSaveButton.setOnClickListener {
            val newNome = binding.updateNomeContato.text.toString()

            if (newNome.isBlank()) {
                Toast.makeText(this, "Campo nome é obrigatório", Toast.LENGTH_SHORT).show()
            } else if (telefones.isEmpty()) {
                Toast.makeText(this, "O contato precisa ter pelo menos um telefone", Toast.LENGTH_SHORT).show()
            } else {
                // Atualiza o contato e a lista de telefones no banco de dados
                db.updateContact(contatoId, newNome, telefones)

                finish()
                Toast.makeText(this, "Contato editado com sucesso", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
