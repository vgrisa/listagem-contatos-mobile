package com.example.contatos

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.contatos.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var db: ContatosDatabase
    private lateinit var contatosAdapter: ContatosAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = ContatosDatabase(this)
        contatosAdapter = ContatosAdapter(db.getAllContacts(), this)

        binding.contatosRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.contatosRecyclerView.adapter = contatosAdapter

        binding.addContatoBotao.setOnClickListener{
            val intent = Intent(this, AddContatos::class.java)
            startActivity(intent)
        }

    }

    override fun onResume() {
        super.onResume()
        contatosAdapter.refreshData(db.getAllContacts())
    }
}