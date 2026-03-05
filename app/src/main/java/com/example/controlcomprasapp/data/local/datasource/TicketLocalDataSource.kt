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
            }
            db.insert("ticket_item", null, values)
        }
    }

    fun obtenerItems(): List<ItemTicket> {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM ticket_item", null)

        val lista = mutableListOf<ItemTicket>()

        while (cursor.moveToNext()) {
            lista.add(
                ItemTicket(
                    nombre = cursor.getString(1),
                    ticket_id = cursor.getInt(2),
                    cantidad = cursor.getInt(3),
                    precioUnitario = cursor.getDouble(4),
                    total = cursor.getDouble(5)
                )
            )
        }

        cursor.close()
        return lista
    }
}
