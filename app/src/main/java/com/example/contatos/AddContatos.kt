package com.example.contatos

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.contatos.databinding.ActivityAddContatosBinding

class AddContatos : AppCompatActivity() {

    private lateinit var binding: ActivityAddContatosBinding
    private lateinit var db: ContatosDatabase
    private val telefones: MutableList<Telefone> = mutableListOf()
    private lateinit var telefonesAdapter: TelefoneAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddContatosBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = ContatosDatabase(this)

        binding.saveButton.setOnClickListener {
            val nome = binding.nomeContato.text.toString()

            if (nome.isBlank()) {
                Toast.makeText(this, "Campo nome é obrigatório", Toast.LENGTH_SHORT).show()
            } else if (telefones.isEmpty()) {
                Toast.makeText(this, "É necessário adicionar pelo menos um telefone", Toast.LENGTH_SHORT).show()
            } else {
                // Cria o contato sem telefones para primeiro salvar no banco
                val contato = Contato(0, nome, telefones = emptyList())

                // Insere o contato no banco e recupera o ID
                val contatoId = db.insertContact(contato)

                // Insere cada telefone associado ao ID do contato
                telefones.forEach { telefone ->
                    val telefoneComIdContato = Telefone(0, contatoId, telefone.telefone, telefone.tipo)
                    db.insertTelefone(telefoneComIdContato)
                }

                finish()
                Toast.makeText(this, "Contato Salvo", Toast.LENGTH_SHORT).show()
            }
        }

        binding.addContatoBotao.setOnClickListener {
            val telefone = binding.telefoneContato.text.toString()
            val tipo = binding.tipoContato.text.toString()

            val telefoneC = Telefone(0, 0, telefone, tipo)


            if(telefone == null || telefone == ""){
                Toast.makeText(this, "Campo telefone é obrigatorio", Toast.LENGTH_SHORT).show()
            }
            else if(tipo == null || tipo == ""){
                Toast.makeText(this, "Campo tipo é obrigatorio", Toast.LENGTH_SHORT).show()
            }
            else{
                telefones.add(telefoneC)

                telefonesAdapter = TelefoneAdapter(telefones, this)
                binding.telefonesRecyclerView.layoutManager = LinearLayoutManager(this)
                binding.telefonesRecyclerView.adapter = telefonesAdapter

                binding.telefoneContato.text.clear()
                binding.tipoContato.text.clear()
            }
        }

    }
}