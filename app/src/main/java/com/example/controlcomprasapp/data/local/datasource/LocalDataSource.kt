package com.example.controlcomprasapp.data.local.datasource
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.example.controlcomprasapp.data.local.db.DbHelper
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
}