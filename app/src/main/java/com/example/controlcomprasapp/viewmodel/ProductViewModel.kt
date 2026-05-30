package com.example.controlcomprasapp.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.controlcomprasapp.data.local.datasource.ProductFilter
import com.example.controlcomprasapp.data.local.datasource.ProductoUI
import com.example.controlcomprasapp.data.repository.ProductRepository
import com.example.controlcomprasapp.domain.model.Locales
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProductViewModel(
    private val repository: ProductRepository
) : ViewModel() {


    var items by mutableStateOf<List<ProductoUI>>(emptyList())
        private set
    private val _locales = MutableStateFlow<List<Locales>>(emptyList())
    val locales: StateFlow<List<Locales>> = _locales

    init {
        loadLocales()
    }

    fun loadItems(filter: ProductFilter) {
        items = repository.obtenerItems(filter)
    }

    fun loadDescuentos(filter: ProductFilter) {
        items = repository.obtenerDescuentos(filter)
    }

    fun loadLocales() {
        viewModelScope.launch(Dispatchers.IO) {
            // Trae directamente lo que haya en la base de datos actual
            val listaDb = repository.obtenerLocales()

            // Actualiza el flujo en el hilo principal
            _locales.value = listaDb
        }
    }
}