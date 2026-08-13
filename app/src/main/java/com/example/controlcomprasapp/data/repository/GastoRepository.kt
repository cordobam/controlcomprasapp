package com.example.controlcomprasapp.data.repository

import com.example.controlcomprasapp.data.local.datasource.GastoDataSource
import com.example.controlcomprasapp.domain.model.GastoFijo
import com.example.controlcomprasapp.domain.model.GastoMensual
import com.example.controlcomprasapp.domain.model.Ingreso

class GastoRepository(private val dataSource: GastoDataSource) {
    fun obtenerGastosFijos(): List<GastoFijo> = dataSource.obtenerGastosFijos()

    fun insertarGastoFijo(nombre: String, monto: Double) =
        dataSource.insertarGastoFijo(nombre, monto)

    fun actualizarMontoGastoFijo(id: Long, monto: Double) =
        dataSource.actualizarMontoGastoFijo(id, monto)

    fun eliminarGastoFijo(id: Long) = dataSource.eliminarGastoFijo(id)

    fun obtenerGastosDelMes(mes: Int, anio: Int): List<GastoMensual> =
        dataSource.obtenerGastosDelMes(mes, anio)

    fun insertarGastoMensual(
        idGastoFijo: Long?,
        nombre: String,
        monto: Double,
        mes: Int,
        anio: Int,
        esFijo: Boolean,
        pagado: Boolean
    ) = dataSource.insertarGastoMensual(idGastoFijo, nombre, monto, mes, anio, esFijo, pagado)

    fun actualizarPagadoGasto(id: Long, pagado: Boolean) =
        dataSource.actualizarPagadoGasto(id, pagado)

    fun actualizarMontoGastoMensual(id: Long, monto: Double) =
        dataSource.actualizarMontoGastoMensual(id, monto)

    fun eliminarGastoMensual(id: Long) = dataSource.eliminarGastoMensual(id)

    fun inicializarGastosFijosDelMes(mes: Int, anio: Int) =
        dataSource.inicializarGastosFijosDelMes(mes, anio)

    fun copiarGastosFijosDelMesAnterior(mes: Int, anio: Int): Int =
        dataSource.copiarGastosFijosDelMesAnterior(mes, anio)

    fun obtenerIngresosDelMes(mes: Int, anio: Int): List<Ingreso> =
        dataSource.obtenerIngresosDelMes(mes, anio)

    fun insertarIngreso(nombre: String, monto: Double, mes: Int, anio: Int) =
        dataSource.insertarIngreso(nombre, monto, mes, anio)

    fun eliminarIngreso(id: Long) = dataSource.eliminarIngreso(id)
}