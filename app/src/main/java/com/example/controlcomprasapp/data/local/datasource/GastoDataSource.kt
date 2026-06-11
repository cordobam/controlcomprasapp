package com.example.controlcomprasapp.data.local.datasource

import android.content.Context
import com.example.controlcomprasapp.data.local.db.DbHelper
import com.example.controlcomprasapp.domain.model.GastoFijo
import com.example.controlcomprasapp.domain.model.GastoMensual
import com.example.controlcomprasapp.domain.model.Ingreso

class GastoDataSource(context: Context) {
    private val dbHelper = DbHelper(context)

    fun obtenerGastosFijos(): List<GastoFijo> {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM gastos_fijos ORDER BY nombre", null)
        val lista = mutableListOf<GastoFijo>()
        while (cursor.moveToNext()) {
            lista.add(
                GastoFijo(
                    id = cursor.getLong(cursor.getColumnIndexOrThrow("id")),
                    nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre")),
                    monto = cursor.getDouble(cursor.getColumnIndexOrThrow("monto"))
                )
            )
        }
        cursor.close()
        return lista
    }

    fun insertarGastoFijo(nombre: String, monto: Double) {
        val db = dbHelper.writableDatabase
        db.execSQL(
            "INSERT INTO gastos_fijos (nombre, monto) VALUES (?, ?)",
            arrayOf(nombre, monto)
        )
    }

    fun actualizarMontoGastoFijo(id: Long, monto: Double) {
        val db = dbHelper.writableDatabase
        db.execSQL(
            "UPDATE gastos_fijos SET monto = ? WHERE id = ?",
            arrayOf(monto, id)
        )
    }

    fun eliminarGastoFijo(id: Long) {
        val db = dbHelper.writableDatabase
        db.execSQL("DELETE FROM gastos_fijos WHERE id = ?", arrayOf(id))
        db.execSQL("DELETE FROM gastos_mensuales WHERE id_gasto_fijo = ?", arrayOf(id))
    }

    fun obtenerGastosDelMes(mes: Int, anio: Int): List<GastoMensual> {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM gastos_mensuales WHERE mes = ? AND anio = ? ORDER BY es_fijo DESC, nombre",
            arrayOf(mes.toString(), anio.toString())
        )
        val lista = mutableListOf<GastoMensual>()
        while (cursor.moveToNext()) {
            lista.add(
                GastoMensual(
                    id = cursor.getLong(cursor.getColumnIndexOrThrow("id")),
                    idGastoFijo = cursor.getLong(cursor.getColumnIndexOrThrow("id_gasto_fijo"))
                        .let { if (cursor.isNull(cursor.getColumnIndexOrThrow("id_gasto_fijo"))) null else it },
                    nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre")),
                    monto = cursor.getDouble(cursor.getColumnIndexOrThrow("monto")),
                    mes = cursor.getInt(cursor.getColumnIndexOrThrow("mes")),
                    anio = cursor.getInt(cursor.getColumnIndexOrThrow("anio")),
                    esFijo = cursor.getInt(cursor.getColumnIndexOrThrow("es_fijo")) == 1,
                    pagado = cursor.getInt(cursor.getColumnIndexOrThrow("pagado")) == 1
                )
            )
        }
        cursor.close()
        return lista
    }

    fun insertarGastoMensual(
        idGastoFijo: Long?,
        nombre: String,
        monto: Double,
        mes: Int,
        anio: Int,
        esFijo: Boolean,
        pagado: Boolean
    ) {
        val db = dbHelper.writableDatabase
        db.execSQL(
            """INSERT INTO gastos_mensuales 
               (id_gasto_fijo, nombre, monto, mes, anio, es_fijo, pagado) 
               VALUES (?, ?, ?, ?, ?, ?, ?)""",
            arrayOf(idGastoFijo, nombre, monto, mes, anio, if (esFijo) 1 else 0, if (pagado) 1 else 0)
        )
    }

    fun actualizarPagadoGasto(id: Long, pagado: Boolean) {
        val db = dbHelper.writableDatabase
        db.execSQL(
            "UPDATE gastos_mensuales SET pagado = ? WHERE id = ?",
            arrayOf(if (pagado) 1 else 0, id)
        )
    }

    fun actualizarMontoGastoMensual(id: Long, monto: Double) {
        val db = dbHelper.writableDatabase
        db.execSQL(
            "UPDATE gastos_mensuales SET monto = ? WHERE id = ?",
            arrayOf(monto, id)
        )
    }

    fun eliminarGastoMensual(id: Long) {
        val db = dbHelper.writableDatabase
        db.execSQL("DELETE FROM gastos_mensuales WHERE id = ?", arrayOf(id))
    }

    fun inicializarGastosFijosDelMes(mes: Int, anio: Int) {
        val db = dbHelper.writableDatabase
        val gastosFijos = obtenerGastosFijos()
        for (gf in gastosFijos) {
            val cursor = db.rawQuery(
                """SELECT id FROM gastos_mensuales 
                   WHERE id_gasto_fijo = ? AND mes = ? AND anio = ?""",
                arrayOf(gf.id.toString(), mes.toString(), anio.toString())
            )
            val existe = cursor.moveToFirst()
            cursor.close()
            if (!existe) {
                db.execSQL(
                    """INSERT INTO gastos_mensuales 
                       (id_gasto_fijo, nombre, monto, mes, anio, es_fijo, pagado) 
                       VALUES (?, ?, ?, ?, ?, 1, 0)""",
                    arrayOf(gf.id, gf.nombre, gf.monto, mes, anio)
                )
            }
        }
    }

    fun obtenerIngresosDelMes(mes: Int, anio: Int): List<Ingreso> {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM ingresos WHERE mes = ? AND anio = ? ORDER BY nombre",
            arrayOf(mes.toString(), anio.toString())
        )
        val lista = mutableListOf<Ingreso>()
        while (cursor.moveToNext()) {
            lista.add(
                Ingreso(
                    id = cursor.getLong(cursor.getColumnIndexOrThrow("id")),
                    nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre")),
                    monto = cursor.getDouble(cursor.getColumnIndexOrThrow("monto")),
                    mes = cursor.getInt(cursor.getColumnIndexOrThrow("mes")),
                    anio = cursor.getInt(cursor.getColumnIndexOrThrow("anio"))
                )
            )
        }
        cursor.close()
        return lista
    }

    fun insertarIngreso(nombre: String, monto: Double, mes: Int, anio: Int) {
        val db = dbHelper.writableDatabase
        db.execSQL(
            "INSERT INTO ingresos (nombre, monto, mes, anio) VALUES (?, ?, ?, ?)",
            arrayOf(nombre, monto, mes, anio)
        )
    }

    fun eliminarIngreso(id: Long) {
        val db = dbHelper.writableDatabase
        db.execSQL("DELETE FROM ingresos WHERE id = ?", arrayOf(id))
    }
}