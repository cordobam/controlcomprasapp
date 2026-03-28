package com.example.controlcomprasapp.data.local.datasource

import android.content.Context
import com.example.controlcomprasapp.data.local.db.DbHelper
import com.example.controlcomprasapp.data.local.dto.DescuentosDTO
import com.example.controlcomprasapp.domain.model.Descuentos

class HomeDataSource(context: Context) {
    private val dbHelper = DbHelper(context)

    fun obtenerDescuentosMax(): List<DescuentosDTO> {
        val db = dbHelper.readableDatabase
        var query = """SELECT d.nombre, t.fecha , max(d.total) as total_maximo FROM descuentos d INNER JOIN ticket t ON t.id = d.ticket_id GROUP BY d.nombre,t.fecha ORDER BY total ASC LIMIT 5 """.trimIndent()


        val cursor = db.rawQuery(query,null)
        val lista = mutableListOf<DescuentosDTO>()

        while (cursor.moveToNext()) {
            lista.add(
                DescuentosDTO(
                     nombre =cursor.getString(cursor.getColumnIndexOrThrow("nombre")),
                     fecha = cursor.getString(cursor.getColumnIndexOrThrow("fecha")),
                     total =  cursor.getDouble(cursor.getColumnIndexOrThrow("total_maximo"))
                )
            )
        }

        cursor.close()
        return lista
    }

}