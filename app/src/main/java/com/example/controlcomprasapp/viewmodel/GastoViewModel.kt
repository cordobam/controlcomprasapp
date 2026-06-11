package com.example.controlcomprasapp.viewmodel

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.controlcomprasapp.data.repository.GastoRepository
import com.example.controlcomprasapp.domain.model.GastoFijo
import com.example.controlcomprasapp.domain.model.GastoMensual
import com.example.controlcomprasapp.domain.model.Ingreso
import java.time.LocalDate

@RequiresApi(Build.VERSION_CODES.O)
class GastoViewModel (private val repository: GastoRepository) : ViewModel() {

    var gastosFijos by mutableStateOf<List<GastoFijo>>(emptyList())
        private set

    var gastosDelMes by mutableStateOf<List<GastoMensual>>(emptyList())
        private set

    var ingresosDelMes by mutableStateOf<List<Ingreso>>(emptyList())
        private set

    var mesSeleccionado by mutableStateOf(LocalDate.now().monthValue)
        private set

    var anioSeleccionado by mutableStateOf(LocalDate.now().year)
        private set

    val saldo: Double
        get() = ingresosDelMes.sumOf { it.monto } - gastosDelMes.sumOf { it.monto }

    val totalGastosPagados: Double
        get() = gastosDelMes.filter { it.pagado }.sumOf { it.monto }

    val totalGastosPendientes: Double
        get() = gastosDelMes.filter { !it.pagado }.sumOf { it.monto }

    fun cargarMes(mes: Int, anio: Int) {
        mesSeleccionado = mes
        anioSeleccionado = anio
        repository.inicializarGastosFijosDelMes(mes, anio)
        gastosFijos = repository.obtenerGastosFijos()
        gastosDelMes = repository.obtenerGastosDelMes(mes, anio)
        ingresosDelMes = repository.obtenerIngresosDelMes(mes, anio)
    }

    fun agregarGastoFijo(nombre: String, monto: Double) {
        repository.insertarGastoFijo(nombre, monto)
        repository.inicializarGastosFijosDelMes(mesSeleccionado, anioSeleccionado)
        gastosFijos = repository.obtenerGastosFijos()
        gastosDelMes = repository.obtenerGastosDelMes(mesSeleccionado, anioSeleccionado)
    }

    fun agregarGastoExcepcional(nombre: String, monto: Double) {
        repository.insertarGastoMensual(
            idGastoFijo = null,
            nombre = nombre,
            monto = monto,
            mes = mesSeleccionado,
            anio = anioSeleccionado,
            esFijo = false,
            pagado = false
        )
        gastosDelMes = repository.obtenerGastosDelMes(mesSeleccionado, anioSeleccionado)
    }

    fun agregarIngreso(nombre: String, monto: Double) {
        repository.insertarIngreso(nombre, monto, mesSeleccionado, anioSeleccionado)
        ingresosDelMes = repository.obtenerIngresosDelMes(mesSeleccionado, anioSeleccionado)
    }

    fun togglePagado(gasto: GastoMensual) {
        repository.actualizarPagadoGasto(gasto.id, !gasto.pagado)
        gastosDelMes = repository.obtenerGastosDelMes(mesSeleccionado, anioSeleccionado)
    }

    fun actualizarMontoGastoMensual(id: Long, nuevoMonto: Double) {
        repository.actualizarMontoGastoMensual(id, nuevoMonto)
        gastosDelMes = repository.obtenerGastosDelMes(mesSeleccionado, anioSeleccionado)
    }

    fun eliminarGasto(gasto: GastoMensual) {
        repository.eliminarGastoMensual(gasto.id)
        gastosDelMes = repository.obtenerGastosDelMes(mesSeleccionado, anioSeleccionado)
    }

    fun eliminarIngreso(ingreso: Ingreso) {
        repository.eliminarIngreso(ingreso.id)
        ingresosDelMes = repository.obtenerIngresosDelMes(mesSeleccionado, anioSeleccionado)
    }

}

