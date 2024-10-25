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
        private const val COLUMN_TELEFONE = "telefone"
        private const val COLUMN_TIPO = "tipo"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTableQuery = "CREATE TABLE $TABLE_NAME ($COLUMN_ID INTEGER PRIMARY KEY, $COLUMN_NOME TEXT, $COLUMN_TELEFONE TEXT, $COLUMN_TIPO TEXT)"
        db?.execSQL(createTableQuery)
    }


    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        val dropTableQuery = "DROP TABLE IF EXISTS $TABLE_NAME"
        db?.execSQL(dropTableQuery)
        onCreate(db)
    }

    fun insertContact(contact: Contato) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NOME, contact.nome)
            put(COLUMN_TELEFONE, contact.telefone)
            put(COLUMN_TIPO, contact.tipo)
        }
        db.insert(TABLE_NAME, null, values)
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
            val telefone = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TELEFONE))
            val tipo = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TIPO))

            val contact = Contato(id, nome, telefone, tipo)
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
            put(COLUMN_TELEFONE, contact.telefone)
            put(COLUMN_TIPO, contact.tipo)
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
        val telefone = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TELEFONE))
        val tipo = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TIPO))

        cursor.close()
        db.close()

        return Contato(id, nome, telefone, tipo)
    }

    fun deleteContact(contatoId: Int) {
        val db = writableDatabase
        val whereClause = "$COLUMN_ID = ?"
        val whereArgs = arrayOf(contatoId.toString())
        db.delete(TABLE_NAME, whereClause, whereArgs)
        db.close()
    }
}
