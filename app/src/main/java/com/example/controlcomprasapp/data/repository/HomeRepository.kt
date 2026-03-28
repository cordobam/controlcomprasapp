package com.example.controlcomprasapp.data.repository

import com.example.controlcomprasapp.data.local.datasource.HomeDataSource
import com.example.controlcomprasapp.data.local.datasource.ProductFilter
import com.example.controlcomprasapp.data.local.datasource.ProductoUI
import com.example.controlcomprasapp.data.local.datasource.TipoProducto
import com.example.controlcomprasapp.data.local.dto.DescuentosDTO

class HomeRepository(private val HomeDataSoucrce : HomeDataSource) {
    fun obtenerDescuentos(): List<DescuentosDTO> {
        return HomeDataSoucrce.obtenerDescuentosMax().map {
            DescuentosDTO(
                nombre = it.nombre,
                fecha = it.fecha,
                total = it.total
            )
        }
    }
}