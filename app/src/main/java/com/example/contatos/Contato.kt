package com.example.contatos

data class Contato(val id: Int, val nome: String, val telefones: List<Telefone?>? = null)
