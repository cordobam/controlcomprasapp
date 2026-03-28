package com.example.controlcomprasapp.data.repository

import com.example.controlcomprasapp.data.local.datasource.HomeDataSource
import com.example.controlcomprasapp.data.local.datasource.ProductFilter
import com.example.controlcomprasapp.data.local.datasource.ProductoUI
import com.example.controlcomprasapp.data.local.datasource.TipoProducto
import com.example.controlcomprasapp.data.local.dto.DescuentosDTO
import com.example.controlcomprasapp.data.local.dto.GastoMensualDTO
import com.example.controlcomprasapp.data.local.dto.ItemTicketDTO
import com.example.controlcomprasapp.data.local.dto.ProductoDTO

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

    fun obtenerGastoXRubro(): List<ItemTicketDTO> {
        return HomeDataSoucrce.obtenerGastoXRubro().map {
            ItemTicketDTO(
                seccion = it.seccion,
                total = it.total
            )
        }
    }

    fun obtenerProdcutosMasComprados(): List<ProductoDTO> {
        return HomeDataSoucrce.obtenerProdcutosMasComprados().map {
            ProductoDTO(
                nombre = it.nombre,
                cant_veces = it.cant_veces
            )
        }
    }

    fun obtenerGastoXMes(): List<GastoMensualDTO> {
        return HomeDataSoucrce.obtenerGastoXMes().map {
            GastoMensualDTO(
                fecha = it.fecha,
                monto = it.monto
            )
        }
    }
}