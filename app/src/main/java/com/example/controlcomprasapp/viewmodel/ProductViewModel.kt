package com.example.controlcomprasapp.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.controlcomprasapp.data.local.datasource.ProductFilter
import com.example.controlcomprasapp.data.repository.ProductRepository
import com.example.controlcomprasapp.domain.model.ItemTicket
import kotlinx.coroutines.launch

class ProductViewModel(
    private val repository: ProductRepository
) : ViewModel() {

    var items by mutableStateOf<List<ItemTicket>>(emptyList())
        private set

    fun loadItems(filter: ProductFilter) {
        items = repository.obtenerItems(filter)
    }
}