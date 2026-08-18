package com.example.controlcomprasapp.data.local.datasource

import android.content.ContentValues
import android.content.Context
import com.example.controlcomprasapp.data.local.db.DbHelper
import com.example.controlcomprasapp.domain.model.ItemTicket
import com.example.controlcomprasapp.domain.model.Tickets

class TicketLocalDataSource(context: Context) {

    private val dbHelper = DbHelper(context)

    fun guardarTicket(ticket: Tickets): Long {
        val db = dbHelper.writableDatabase

        val values = ContentValues().apply {
            put("fecha", ticket.fecha)
            put("id_local", ticket.id_local)
            put("id_archivo", ticket.id_archivo)
            put("tipo", ticket.tipo)
            ticket.banco?.let { put("banco", it) }
            ticket.marca?.let { put("marca", it) }
            ticket.fechaVencimiento?.let { put("fecha_vencimiento", it) }
        }

        val ticketId = db.insert("ticket", null, values)

        return ticketId
    }
    fun guardarItems(items: List<ItemTicket>, ticketId: Long) {
        val db = dbHelper.writableDatabase

        items.forEach { item ->
            val values = ContentValues().apply {
                put("nombre", item.nombre)
                put("ticket_id", ticketId)
                put("cantidad", item.cantidad)
                put("precio", item.precioUnitario)
                put("total", item.total)
                put("seccion", item.seccion)
                item.fechaConsumo?.let { put("fecha_consumo", it) }
                item.cuotasTotal?.let { put("cuotas_total", it) }
                item.cuotaActual?.let { put("cuota_actual", it) }
            }
            db.insert("ticket_item", null, values)
        }
    }

    fun obtenerItems(filter: ProductFilter): List<ItemTicket> {
        val db = dbHelper.readableDatabase
        var query = """SELECT ti.* FROM ticket_item ti JOIN ticket t ON t.id = ti.ticket_id WHERE 1=1""".trimIndent()

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
        val lista = mutableListOf<ItemTicket>()

        while (cursor.moveToNext()) {
            val fechaConsumoIdx = cursor.getColumnIndex("fecha_consumo")
            val cuotasTotalIdx = cursor.getColumnIndex("cuotas_total")
            val cuotaActualIdx = cursor.getColumnIndex("cuota_actual")
            lista.add(
                ItemTicket(
                    ticket_id = cursor.getInt(cursor.getColumnIndexOrThrow("ticket_id")),
                    nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre")),
                    cantidad = cursor.getInt(cursor.getColumnIndexOrThrow("cantidad")),
                    precioUnitario = cursor.getDouble(cursor.getColumnIndexOrThrow("precio")),
                    total = cursor.getDouble(cursor.getColumnIndexOrThrow("total")),
                    seccion = cursor.getString(cursor.getColumnIndexOrThrow("seccion")),
                    fechaConsumo = if (fechaConsumoIdx != -1 && !cursor.isNull(fechaConsumoIdx)) cursor.getString(fechaConsumoIdx) else null,
                    cuotasTotal = if (cuotasTotalIdx != -1 && !cursor.isNull(cuotasTotalIdx)) cursor.getInt(cuotasTotalIdx) else null,
                    cuotaActual = if (cuotaActualIdx != -1 && !cursor.isNull(cuotaActualIdx)) cursor.getInt(cuotaActualIdx) else null
                )
            )
        }

        cursor.close()
        return lista
    }
}
