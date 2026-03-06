package com.example.controlcomprasapp.data.local.datasource

import android.content.ContentValues
import android.content.Context
import com.example.controlcomprasapp.data.local.db.DbHelper
import com.example.controlcomprasapp.domain.model.Descuentos

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
}