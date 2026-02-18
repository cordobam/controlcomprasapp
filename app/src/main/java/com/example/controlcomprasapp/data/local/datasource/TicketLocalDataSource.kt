package com.example.controlcomprasapp.data.local.datasource

import android.content.ContentValues
import android.content.Context
import com.example.controlcomprasapp.data.local.db.DbHelper
import com.example.controlcomprasapp.domain.model.ItemTicket

class TicketLocalDataSource(context: Context) {

    private val dbHelper = DbHelper(context)

    fun guardarItems(items: List<ItemTicket>) {
        val db = dbHelper.writableDatabase

        items.forEach { item ->
            val values = ContentValues().apply {
                put("nombre", item.nombre)
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
                    cantidad = cursor.getInt(2),
                    precioUnitario = cursor.getDouble(3),
                    total = cursor.getDouble(4)
                )
            )
        }

        cursor.close()
        return lista
    }
}
