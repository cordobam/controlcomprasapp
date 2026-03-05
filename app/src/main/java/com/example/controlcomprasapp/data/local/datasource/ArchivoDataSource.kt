package com.example.controlcomprasapp.data.local.datasource

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.example.controlcomprasapp.data.local.db.DbHelper
class ArchivoDataSource(context: Context) {
    private val dbHelper = DbHelper(context)
    fun insertOrGet(nombre: String): Long {
        val db = dbHelper.writableDatabase

        val values = ContentValues().apply {
            put("nombre", nombre)
        }

        val insertResult = db.insertWithOnConflict(
            "archivos",
            null,
            values,
            SQLiteDatabase.CONFLICT_IGNORE
        )

        if (insertResult != -1L) {
            return insertResult
        }

        val cursor = db.rawQuery(
            "SELECT id FROM archivos WHERE nombre = ?",
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
}