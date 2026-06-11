package com.example.controlcomprasapp.viewmodel

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.controlcomprasapp.data.repository.GastoRepository


class GastoViewModelFactory(
    private val repository: GastoRepository
) : ViewModelProvider.Factory  {
    @RequiresApi(Build.VERSION_CODES.O)
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GastoViewModel::class.java)) {
            return GastoViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}