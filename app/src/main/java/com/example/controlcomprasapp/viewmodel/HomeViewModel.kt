package com.example.controlcomprasapp.viewmodel

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.controlcomprasapp.data.local.datasource.MesFiltro
import com.example.controlcomprasapp.data.local.dto.DescuentosDTO
import com.example.controlcomprasapp.data.local.dto.GastoMensualDTO
import com.example.controlcomprasapp.data.local.dto.ItemTicketDTO
import com.example.controlcomprasapp.data.local.dto.ProductoDTO
import com.example.controlcomprasapp.data.repository.HomeRepository

@RequiresApi(Build.VERSION_CODES.O)
class HomeViewModel(private val repository: HomeRepository): ViewModel() {

    // Modo actual: false = Gasto Mes (COMPRA), true = Gasto TC (TARJETA)
    var modoTarjeta by mutableStateOf(false)
        private set

    // Estados modo COMPRA (actuales)
    var items by mutableStateOf<List<DescuentosDTO>>(emptyList())
        private set
    var items_gastos by mutableStateOf<List<ItemTicketDTO>>(emptyList())
        private set
    var items_prductos by mutableStateOf<List<ProductoDTO>>(emptyList())
        private set
    var items_mensual by mutableStateOf<List<GastoMensualDTO>>(emptyList())
        private set
    var items_mes by mutableStateOf<List<MesFiltro>>(emptyList())
        private set
    var totalGastado by mutableStateOf(0)
        private set
    var totalAhorrado by mutableStateOf(0)
        private set
    var cantidadTickets by mutableStateOf(0)
        private set
    var ticketPromedio by mutableStateOf(0)
        private set

    // Estados modo TARJETA (nuevos)
    var totalTarjeta by mutableStateOf(0.0)
        private set
    var cantidadTarjetas by mutableStateOf(0)
        private set
    var items_gastos_tarjeta by mutableStateOf<List<ItemTicketDTO>>(emptyList())
        private set
    var items_descuentos_tarjeta by mutableStateOf<List<DescuentosDTO>>(emptyList())
        private set
    var items_productos_tarjeta by mutableStateOf<List<ProductoDTO>>(emptyList())
        private set

    @RequiresApi(Build.VERSION_CODES.O)
    fun loadMeses(){
        items_mes = repository.obtenerMeses()
    }

    fun toggleModoTarjeta() {
        modoTarjeta = !modoTarjeta
        // Recargar mes actual si hay uno seleccionado
        val mesActual = items_mes.firstOrNull()
        mesActual?.let { cargarDatosPorMes(it.mes, it.anio) }
    }

    fun cargarDatosPorMes(mes: Int, anio: Int) {
        if (modoTarjeta) {
            cargarDatosTarjetaPorMes(mes, anio)
        } else {
            cargarDatosComprasPorMes(mes, anio)
        }
    }

    private fun cargarDatosComprasPorMes(mes: Int, anio: Int) {
        items = repository.obtenerDescuentosPorMes(mes, anio)
        items_gastos = repository.obtenerGastoXRubroPorMes(mes, anio)
        items_prductos = repository.obtenerProdcutosMasCompradosPorMes(mes, anio)

        val gastado = repository.obtenerTotalGastadoPorMes(mes, anio)
        totalGastado = gastado.toInt()
        totalAhorrado = repository.obtenerTotalAhorradoPorMes(mes, anio).toInt()
        cantidadTickets = repository.obtenerCantidadTicketsPorMes(mes, anio)
        ticketPromedio = if (cantidadTickets > 0) (gastado / cantidadTickets).toInt() else 0
    }

    private fun cargarDatosTarjetaPorMes(mes: Int, anio: Int) {
        items_descuentos_tarjeta = repository.obtenerDescuentosTarjetaPorMes(mes, anio)
        items_gastos_tarjeta = repository.obtenerGastoTarjetaXRubroPorMes(mes, anio)
        items_productos_tarjeta = repository.obtenerProdcutosTarjetaMasCompradosPorMes(mes, anio)

        val gastado = repository.obtenerTotalTarjetaPorMes(mes, anio)
        totalTarjeta = gastado
        cantidadTarjetas = repository.obtenerCantidadTicketsTarjetaPorMes(mes, anio)
    }
}