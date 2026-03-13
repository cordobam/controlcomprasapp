package com.example.controlcomprasapp.data.local.datasource

import android.content.ContentValues
import android.content.Context
import com.example.controlcomprasapp.data.local.db.DbHelper
import com.example.controlcomprasapp.domain.model.Descuentos
import com.example.controlcomprasapp.domain.model.ItemTicket

class DescuentoDataSource(context: Context) {
    private val dbHelper = DbHelper(context)

    fun guardarDescuentos(descuentos:List<Descuentos>, ticketId:Long, archivoId:Long){
        val db = dbHelper.writableDatabase
        descuentos.forEach { d ->
            val values = ContentValues().apply {
                put("ticket_id",ticketId)
                put("nombre",d.nombre)
                put("total",d.total)
                put("id_archivo",archivoId)
            }

            db.insert("descuentos" , null , values)
        }
    }

    fun obtenerDescuentos(filter: ProductFilter): List<Descuentos> {
        val db = dbHelper.readableDatabase
        var query = """SELECT d.* FROM descuentos d JOIN ticket t ON t.id = d.ticket_id WHERE 1=1""".trimIndent()

        val args = mutableListOf<String>()
        //
        filter.fechaDesde?.let {
            query += " AND t.fecha >= ?"
            args.add(it)
        }

        filter.fechaHasta?.let {
            query += " AND t.fecha <= ?"
            args.add(it)
        }

        filter.localId?.let {
            query += " AND t.id_local = ?"
            args.add(it.toString())
        }

        val cursor = db.rawQuery(query, args.toTypedArray())
        val lista = mutableListOf<Descuentos>()

        while (cursor.moveToNext()) {
            lista.add(
                Descuentos(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                    ticket_id = cursor.getInt(cursor.getColumnIndexOrThrow("ticket_id")),
                    nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre")),
                    total = cursor.getDouble(cursor.getColumnIndexOrThrow("total")),
                    id_archivo = cursor.getInt(cursor.getColumnIndexOrThrow("id_archivo"))
                )
            )
        }

        cursor.close()
        return lista
    }
}