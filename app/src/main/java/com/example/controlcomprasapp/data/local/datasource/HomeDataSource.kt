package com.example.controlcomprasapp.data.local.datasource

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import com.example.controlcomprasapp.data.local.db.DbHelper
import com.example.controlcomprasapp.data.local.dto.DescuentosDTO
import com.example.controlcomprasapp.data.local.dto.GastoMensualDTO
import com.example.controlcomprasapp.data.local.dto.ItemTicketDTO
import com.example.controlcomprasapp.data.local.dto.ProductoDTO
import com.example.controlcomprasapp.domain.model.Descuentos

class HomeDataSource(context: Context) {
    private val dbHelper = DbHelper(context)

    fun obtenerDescuentosMax(mes: String, anio: String): List<DescuentosDTO> {
        val db = dbHelper.readableDatabase
        var query = """SELECT d.nombre, t.fecha , max(d.total) as total_maximo FROM descuentos d INNER JOIN  ticket t ON t.id = d.ticket_id  WHERE strftime('%m', t.fecha) = ? AND strftime('%Y', t.fecha) = ? GROUP BY d.nombre,t.fecha ORDER BY total ASC LIMIT 5 """.trimIndent()

        val cursor = db.rawQuery(query,arrayOf(mes, anio))
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

    fun obtenerGastoXRubro(mes: String, anio: String): List<ItemTicketDTO>{
        val db = dbHelper.readableDatabase
        var query = """SELECT ti.seccion, SUM(ti.total) as total  FROM ticket_item ti INNER JOIN ticket t ON t.id = ti.ticket_id WHERE strftime('%m', t.fecha) = ? AND strftime('%Y', t.fecha) = ? GROUP BY ti.seccion ORDER BY total DESC LIMIT 5""".trimIndent()

        val cursor = db.rawQuery(query,arrayOf(mes, anio))
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

    fun obtenerProdcutosMasComprados(mes: String, anio: String): List<ProductoDTO>{
        val db = dbHelper.readableDatabase
        var query = """SELECT ti.nombre, COUNT(*) as cant_veces  FROM ticket_item ti INNER JOIN ticket t ON t.id = ti.ticket_id WHERE strftime('%m', t.fecha) = ? AND strftime('%Y', t.fecha) = ? GROUP BY ti.nombre ORDER BY cant_veces DESC LIMIT 5 """.trimIndent()


        val cursor = db.rawQuery(query,arrayOf(mes, anio))
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

    fun obtenerTotalGastadoPorMes(mes: String, anio: String): Double {
        val db = dbHelper.readableDatabase
        val query = """SELECT SUM(ti.total) as total FROM ticket_item ti INNER JOIN ticket t ON t.id = ti.ticket_id WHERE strftime('%m', t.fecha) = ? AND strftime('%Y', t.fecha) = ?""".trimIndent()

        val cursor = db.rawQuery(query, arrayOf(mes, anio))
        val total = if (cursor.moveToFirst()) cursor.getDouble(0) else 0.0
        cursor.close()
        return total
    }

    fun obtenerTotalAhorradoPorMes(mes: String, anio: String): Double {
        val db = dbHelper.readableDatabase
        val query = """SELECT SUM(d.total) as total FROM descuentos d INNER JOIN ticket t ON t.id = d.ticket_id WHERE strftime('%m', t.fecha) = ? AND strftime('%Y', t.fecha) = ?""".trimIndent()

        val cursor = db.rawQuery(query, arrayOf(mes, anio))
        val total = if (cursor.moveToFirst()) cursor.getDouble(0) else 0.0
        cursor.close()
        return total
    }

    fun obtenerCantidadTicketsPorMes(mes: String, anio: String): Int {
        val db = dbHelper.readableDatabase
        val query = """SELECT COUNT(*) as cantidad FROM ticket t WHERE strftime('%m', t.fecha) = ? AND strftime('%Y', t.fecha) = ?""".trimIndent()

        val cursor = db.rawQuery(query, arrayOf(mes, anio))
        val cantidad = if (cursor.moveToFirst()) cursor.getInt(0) else 0
        cursor.close()
        return cantidad
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun generarMeses(cantidad: Int = 3): List<MesFiltro> {
        val hoy = java.time.LocalDate.now()

        return (0 until cantidad).map { i ->
            val fecha = hoy.minusMonths(i.toLong())

            val nombreMes = fecha.month.name
                .lowercase()
                .replaceFirstChar { it.uppercase() }

            MesFiltro(
                mes = fecha.monthValue,
                anio = fecha.year,
                label = "$nombreMes ${fecha.year}"
            )
        }
    }

}