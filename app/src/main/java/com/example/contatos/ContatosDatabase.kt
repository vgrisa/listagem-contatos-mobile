package com.example.contatos

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class ContatosDatabase (context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object {
        private const val DATABASE_NAME = "contatosapp.db"
        private const val DATABASE_VERSION = 1
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
        val notesList = mutableListOf<Contato>()
        val db = readableDatabase
        val query = "SELECT * FROM $TABLE_NAME ORDER BY $COLUMN_NOME COLLATE NOCASE ASC"
        val cursor = db.rawQuery(query, null)

        while (cursor.moveToNext()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID))
            val nome = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOME))

            val contact = Contato(id, nome)
            notesList.add(contact)
        }

        cursor.close()
        db.close()
        return notesList
    }

    fun updateContact(contact: Contato) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NOME, contact.nome)
        }

        val whereClause = "$COLUMN_ID = ?"
        val whereArgs = arrayOf(contact.id.toString())
        db.update(TABLE_NAME, values, whereClause, whereArgs)
        db.close()
    }

    fun getContactByID(contactId: Int): Contato {
        val db = readableDatabase
        val query = "SELECT * FROM $TABLE_NAME WHERE $COLUMN_ID = $contactId"
        val cursor = db.rawQuery(query, null)
        cursor.moveToFirst()

        val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID))
        val nome = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOME))

        val telefoneList = mutableListOf<Telefone>()
        val queryTelefone = "SELECT * FROM $TABLE_NAME_TELEFONE ORDER BY $COLUMN_TELEFONE_ID COLLATE NOCASE ASC"
        val cursorTelefone = db.rawQuery(queryTelefone, null)

        while (cursorTelefone.moveToNext()) {
            val telefoneId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_TELEFONE_ID))
            val clienteId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CLIENTE_ID))
            val telefone = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TELEFONE))
            val tipo = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TIPO))

            val telefoneC = Telefone(telefoneId, clienteId, telefone, tipo)
            telefoneList.add(telefoneC)
        }

        cursor.close()
        db.close()

        return Contato(id, nome, telefoneList)
    }

    fun deleteContact(contatoId: Int) {
        val db = writableDatabase
        val whereClause = "$COLUMN_ID = ?"
        val whereArgs = arrayOf(contatoId.toString())
        db.delete(TABLE_NAME, whereClause, whereArgs)
        db.close()
    }

    fun deleteTelefone(telefoneId: Int, clienteId: Int) {
        val db = writableDatabase
        val whereClause = "$COLUMN_TELEFONE_ID = ? AND $COLUMN_CLIENTE_ID = ?"
        val whereArgs = arrayOf(telefoneId.toString(), clienteId.toString())
        db.delete(TABLE_NAME_TELEFONE, whereClause, whereArgs)
        db.close()
    }
}
