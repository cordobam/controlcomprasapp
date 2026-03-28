package com.example.controlcomprasapp.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.controlcomprasapp.data.local.dto.DescuentosDTO
import com.example.controlcomprasapp.data.repository.HomeRepository

class HomeViewModel(private val repository: HomeRepository): ViewModel() {
    var items by mutableStateOf<List<DescuentosDTO>>(emptyList())
        private set

    fun loadDescuentosMax(){
        items = repository.obtenerDescuentos()
    }
}