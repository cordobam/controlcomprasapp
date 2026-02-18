package com.example.controlcomprasapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.controlcomprasapp.data.repository.TicketRepository

class FacturaViewModelFactory(
    private val repo: TicketRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FacturaViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FacturaViewModel(repo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}