package com.example.contatos

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.contatos.databinding.ActivityAddContatosBinding

class AddContatos : AppCompatActivity() {

    private lateinit var binding: ActivityAddContatosBinding
    private lateinit var db: ContatosDatabase


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddContatosBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = ContatosDatabase(this)

        binding.saveButton.setOnClickListener {
            val nome = binding.nomeContato.text.toString()
            val telefone = binding.telefoneContato.text.toString()
            val tipo = binding.tipoContato.text.toString()

            val contato = Contato(0, nome, telefone, tipo)


            if(nome == null || nome == ""){
                Toast.makeText(this, "Campo nome é obrigatorio", Toast.LENGTH_SHORT).show()
            }
            else if(telefone == null || telefone == ""){
                Toast.makeText(this, "Campo telefone é obrigatorio", Toast.LENGTH_SHORT).show()
            }
            else if(tipo == null || tipo == ""){
                Toast.makeText(this, "Campo tipo é obrigatorio", Toast.LENGTH_SHORT).show()
            }
            else{
                db.insertContact(contato)
                finish()
                Toast.makeText(this, "Contato Salvo", Toast.LENGTH_SHORT).show()
            }
        }
    }
}