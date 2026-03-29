package com.example.controlcomprasapp.data.local.datasource

import android.content.Context
import com.example.controlcomprasapp.data.local.db.DbHelper
import com.example.controlcomprasapp.data.local.dto.DescuentosDTO
import com.example.controlcomprasapp.data.local.dto.GastoMensualDTO
import com.example.controlcomprasapp.data.local.dto.ItemTicketDTO
import com.example.controlcomprasapp.data.local.dto.ProductoDTO
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

    fun obtenerGastoXRubro(): List<ItemTicketDTO>{
        val db = dbHelper.readableDatabase
        var query = """SELECT t.seccion, sum(t.total) as total  FROM ticket_item t  GROUP BY t.seccion ORDER BY sum(t.total) DESC LIMIT 5 """.trimIndent()


        val cursor = db.rawQuery(query,null)
        val lista = mutableListOf<ItemTicketDTO>()

        while (cursor.moveToNext()) {
            lista.add(
                ItemTicketDTO(
                    seccion =cursor.getString(cursor.getColumnIndexOrThrow("seccion")),
                    total =  cursor.getDouble(cursor.getColumnIndexOrThrow("total"))
                )
            )
        }

        cursor.close()
        return lista
    }

    fun obtenerProdcutosMasComprados(): List<ProductoDTO>{
        val db = dbHelper.readableDatabase
        var query = """SELECT t.nombre, count(*) as cant_veces  FROM ticket_item t  GROUP BY t.nombre ORDER BY count(*) ASC LIMIT 5 """.trimIndent()


        val cursor = db.rawQuery(query,null)
        val lista = mutableListOf<ProductoDTO>()

        while (cursor.moveToNext()) {
            lista.add(
                ProductoDTO(
                    nombre =cursor.getString(cursor.getColumnIndexOrThrow("nombre")),
                    cant_veces = cursor.getInt(cursor.getColumnIndexOrThrow("cant_veces"))
                )
            )
        }

        cursor.close()
        return lista
    }

    fun obtenerGastoXMes(): List<GastoMensualDTO>{
        val db = dbHelper.readableDatabase
        var query = """SELECT substr(tt.fecha, 1, 7) as mes, sum(t.total) as monto  FROM ticket_item t INNER JOIN ticket tt ON t.ticket_id = tt.id  GROUP BY  substr(tt.fecha, 1, 7) ORDER BY sum(t.total) DESC LIMIT 5 """.trimIndent()


        val cursor = db.rawQuery(query,null)
        val lista = mutableListOf<GastoMensualDTO>()

        while (cursor.moveToNext()) {
            lista.add(
                GastoMensualDTO(
                    fecha = cursor.getString(cursor.getColumnIndexOrThrow("mes")),
                    monto =  cursor.getDouble(cursor.getColumnIndexOrThrow("monto"))
                )
            )
        }

        cursor.close()
        return lista
    }

}