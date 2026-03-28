package com.example.controlcomprasapp.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.controlcomprasapp.data.local.dto.DescuentosDTO
import com.example.controlcomprasapp.data.local.dto.GastoMensualDTO
import com.example.controlcomprasapp.data.local.dto.ItemTicketDTO
import com.example.controlcomprasapp.data.local.dto.ProductoDTO
import com.example.controlcomprasapp.data.repository.HomeRepository

class HomeViewModel(private val repository: HomeRepository): ViewModel() {
    var items by mutableStateOf<List<DescuentosDTO>>(emptyList())
        private set
    var items_gastos by mutableStateOf<List<ItemTicketDTO>>(emptyList())
        private set
    var items_prductos by mutableStateOf<List<ProductoDTO>>(emptyList())
        private set
    var items_mensual by mutableStateOf<List<GastoMensualDTO>>(emptyList())
        private set

    init {
        loadAll()
    }

    private fun loadAll() {
        loadDescuentosMax()
        loadGastoXRubro()
        loadGastosMensuales()
        loadProdMasComprados()
    }
    fun loadDescuentosMax(){
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
    }
}