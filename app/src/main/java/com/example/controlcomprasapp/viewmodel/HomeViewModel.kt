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


    /*fun loadDescuentosMax(){
        items = repository.obtenerDescuentos()
    }

    fun loadGastoXRubro(){
        items_gastos = repository.obtenerGastoXRubro()
    }

    fun loadProdMasComprados(){
        items_prductos = repository.obtenerProdcutosMasComprados()
    }

    fun loadGastosMensuales(){
        items_mensual = repository.obtenerGastoXMes()
    }*/

    @RequiresApi(Build.VERSION_CODES.O)
    fun loadMeses(){
        items_mes = repository.obtenerMeses()
    }

    fun cargarDatosPorMes(mes: Int, anio: Int) {
        items = repository.obtenerDescuentosPorMes(mes, anio)
        items_gastos = repository.obtenerGastoXRubroPorMes(mes, anio)
        items_prductos = repository.obtenerProdcutosMasCompradosPorMes(mes, anio)
    }
}