package com.example.controlcomprasapp.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.controlcomprasapp.data.local.datasource.ProductFilter
import com.example.controlcomprasapp.data.local.datasource.ProductoUI
import com.example.controlcomprasapp.data.repository.ProductRepository

class ProductViewModel(
    private val repository: ProductRepository
) : ViewModel() {


    var items by mutableStateOf<List<ProductoUI>>(emptyList())
        private set

    fun loadItems(filter: ProductFilter) {
        items = repository.obtenerItems(filter)
    }

    fun loadDescuentos(filter: ProductFilter) {
        items = repository.obtenerDescuentos(filter)
    }
}