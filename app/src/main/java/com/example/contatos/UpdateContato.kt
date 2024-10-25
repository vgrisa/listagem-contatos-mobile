package com.example.contatos

import android.R
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.contatos.databinding.ActivityUpdateContatoBinding
import com.google.android.material.textfield.TextInputLayout


class UpdateContato : AppCompatActivity() {
    private lateinit var binding: ActivityUpdateContatoBinding
    private lateinit var db: ContatosDatabase
    private var contatoId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpdateContatoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = ContatosDatabase(this)

        contatoId = intent.getIntExtra("contato_id", -1)
        if (contatoId == -1) {
            finish()
            return
        }

        val contact = db.getContactByID(contatoId)
        binding.updateNomeContato.setText(contact.nome)
        binding.updateTelefoneContato.setText(contact.telefone)
        binding.updateTipoContato.setText(contact.tipo)

        binding.updateSaveButton.setOnClickListener { it: View ->
            val newNome = binding.updateNomeContato.text.toString()
            val newTelefone = binding.updateTelefoneContato.text.toString()
            val newTipo = binding.updateTipoContato.text.toString()

            if(newNome == null || newNome == ""){
                Toast.makeText(this, "Campo nome é obrigatorio", Toast.LENGTH_SHORT).show()
            }
            else if(newTelefone == null || newTelefone == ""){
                Toast.makeText(this, "Campo telefone é obrigatorio", Toast.LENGTH_SHORT).show()
            }
            else if(newTipo == null || newTipo == ""){
                Toast.makeText(this, "Campo tipo é obrigatorio", Toast.LENGTH_SHORT).show()
            }
            else{
                val updatedContato = Contato(contatoId, newNome, newTelefone, newTipo)
                db.updateContact(updatedContato)
                finish()
                Toast.makeText(this, "Contato editado com sucesso", Toast.LENGTH_SHORT).show()
            }

        }
    }
}