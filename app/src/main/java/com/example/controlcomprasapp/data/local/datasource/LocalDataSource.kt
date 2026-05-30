package com.example.controlcomprasapp.data.local.datasource
import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.example.controlcomprasapp.data.local.db.DbHelper
import com.example.controlcomprasapp.domain.model.Locales

class LocalDataSource(context: Context) {
    private val dbHelper = DbHelper(context)
    fun insertOrGet(nombre: String): Long {
        val db = dbHelper.writableDatabase

        val values = ContentValues().apply {
            put("nombre", nombre)
        }

        val insertResult = db.insertWithOnConflict(
            "locales",
            null,
            values,
            SQLiteDatabase.CONFLICT_IGNORE
        )

        if (insertResult != -1L) {
            return insertResult
        }

        val cursor = db.rawQuery(
            "SELECT id FROM locales WHERE nombre = ?",
            arrayOf(nombre)
        )

        return if (cursor.moveToFirst()) {
            val id = cursor.getLong(cursor.getColumnIndexOrThrow("id"))
            cursor.close()
            id
        } else {
            cursor.close()
            -1L
        }
    }

    @SuppressLint("Range")
    fun getAllLocales(): List<Locales> {
        val lista = mutableListOf<Locales>()
        val db = dbHelper.readableDatabase

        // Consultamos toda la tabla "locales"
        val cursor = db.rawQuery("SELECT id, nombre FROM locales ORDER BY nombre ASC", null)

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
                val nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre"))
                lista.add(Locales(id, nombre))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return lista
    }
}