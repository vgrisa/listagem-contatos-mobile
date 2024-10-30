package com.example.contatos

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class ContatosDatabase (context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object {
        private const val DATABASE_NAME = "contatosapp.db"
        private const val DATABASE_VERSION = 4
        private const val TABLE_NAME = "todoscontatos"
        private const val COLUMN_ID = "id"
        private const val COLUMN_NOME = "nome"


        private const val TABLE_NAME_TELEFONE = "todostelefones"
        private const val COLUMN_TELEFONE_ID = "id"
        private const val COLUMN_CLIENTE_ID = "cliente_id"
        private const val COLUMN_TELEFONE = "telefone"
        private const val COLUMN_TIPO = "tipo"


    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTableQuery = "CREATE TABLE $TABLE_NAME ($COLUMN_ID INTEGER PRIMARY KEY, $COLUMN_NOME TEXT)"

        val createTefoneTableQuery = "CREATE TABLE $TABLE_NAME_TELEFONE ($COLUMN_TELEFONE_ID INTEGER PRIMARY KEY, $COLUMN_CLIENTE_ID INTEGER REFERENCES $TABLE_NAME($COLUMN_ID) ON DELETE CASCADE, $COLUMN_TELEFONE TEXT, $COLUMN_TIPO TEXT)"
        db?.execSQL(createTableQuery)
        db?.execSQL(createTefoneTableQuery)
    }


    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        val dropTableQuery = "DROP TABLE IF EXISTS $TABLE_NAME"
        db?.execSQL(dropTableQuery)

        val dropTelefoneTableQuery = "DROP TABLE IF EXISTS $TABLE_NAME_TELEFONE"
        db?.execSQL(dropTelefoneTableQuery)
        onCreate(db)
    }

    fun insertContact(contact: Contato): Int {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NOME, contact.nome)
        }
        val contatoId = db.insert(TABLE_NAME, null, values).toInt()
        db.close()
        return contatoId  // Retorna o ID do contato recém-criado
    }

    fun insertTelefone(telefone: Telefone) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_CLIENTE_ID, telefone.contatoId)
            put(COLUMN_TELEFONE, telefone.telefone)
            put(COLUMN_TIPO, telefone.tipo)
        }
        db.insert(TABLE_NAME_TELEFONE, null, values)
        db.close()
    }

    fun getAllContacts(): List<Contato> {
        val contatos = mutableListOf<Contato>()
        val db = readableDatabase
        val query = "SELECT * FROM $TABLE_NAME ORDER BY $COLUMN_NOME COLLATE NOCASE ASC"
        val cursor = db.rawQuery(query, null)

        while (cursor.moveToNext()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID))
            val nome = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOME))

            val contact = Contato(id, nome)
            contatos.add(contact)
        }

        cursor.close()
        db.close()
        return contatos
    }

    fun updateContact(contatoId: Int, contatoNome: String, telefones: List<Telefone>) {
        val db = writableDatabase

        // Atualiza o nome do contato
        val values = ContentValues().apply {
            put(COLUMN_NOME, contatoNome)
        }
        val whereClause = "$COLUMN_ID = ?"
        val whereArgs = arrayOf(contatoId.toString())
        db.update(TABLE_NAME, values, whereClause, whereArgs)

        // Remove os telefones antigos do contato
        val deleteTelefoneClause = "$COLUMN_CLIENTE_ID = ?"
        db.delete(TABLE_NAME_TELEFONE, deleteTelefoneClause, whereArgs)

        // Insere a lista atualizada de telefones
        telefones.forEach { telefone ->
            val telefoneValues = ContentValues().apply {
                put(COLUMN_CLIENTE_ID, contatoId)
                put(COLUMN_TELEFONE, telefone.telefone)
                put(COLUMN_TIPO, telefone.tipo)
            }
            db.insert(TABLE_NAME_TELEFONE, null, telefoneValues)
        }

        db.close()
    }

    fun getContactByID(contactId: Int): Contato? {
        val db = readableDatabase
        val queryContact = "SELECT * FROM $TABLE_NAME WHERE $COLUMN_ID = ?"
        val cursor = db.rawQuery(queryContact, arrayOf(contactId.toString()))

        if (cursor.moveToFirst()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID))
            val nome = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOME))

            val telefoneList = mutableListOf<Telefone>()
            val queryTelefone = "SELECT * FROM $TABLE_NAME_TELEFONE WHERE $COLUMN_CLIENTE_ID = ? ORDER BY $COLUMN_TELEFONE_ID ASC"
            val cursorTelefone = db.rawQuery(queryTelefone, arrayOf(id.toString())) // use id para o cliente

            while (cursorTelefone.moveToNext()) {
                val telefoneId = cursorTelefone.getInt(cursorTelefone.getColumnIndexOrThrow(COLUMN_TELEFONE_ID))
                val clienteId = cursorTelefone.getInt(cursorTelefone.getColumnIndexOrThrow(COLUMN_CLIENTE_ID))
                val telefone = cursorTelefone.getString(cursorTelefone.getColumnIndexOrThrow(COLUMN_TELEFONE))
                val tipo = cursorTelefone.getString(cursorTelefone.getColumnIndexOrThrow(COLUMN_TIPO))

                val telefoneC = Telefone(telefoneId, clienteId, telefone, tipo)
                telefoneList.add(telefoneC)
            }

            cursorTelefone.close() // Fechar o cursor de telefones
            cursor.close() // Fechar o cursor de contato
            db.close() // Fechar o banco de dados

            return Contato(id, nome, telefoneList)
        } else {
            cursor.close() // Fechar o cursor se não encontrar
            db.close()
            return null // Retornar null se não encontrar o contato
        }
    }


    fun deleteContact(contatoId: Int) {
        val db = writableDatabase

        // Deletar os telefones associados ao contato
        val deleteTelefonesQuery = "DELETE FROM $TABLE_NAME_TELEFONE WHERE $COLUMN_CLIENTE_ID = ?"
        val telefoneWhereArgs = arrayOf(contatoId.toString())
        db.execSQL(deleteTelefonesQuery, telefoneWhereArgs)

        // Agora, deletar o contato
        val whereClause = "$COLUMN_ID = ?"
        val whereArgs = arrayOf(contatoId.toString())
        val deletedRows = db.delete(TABLE_NAME, whereClause, whereArgs)

        db.close()
    }
}
