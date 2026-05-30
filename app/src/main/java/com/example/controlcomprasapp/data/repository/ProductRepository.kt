package com.example.controlcomprasapp.data.repository

import com.example.controlcomprasapp.data.local.datasource.DescuentoDataSource
import com.example.controlcomprasapp.data.local.datasource.LocalDataSource
import com.example.controlcomprasapp.data.local.datasource.ProductFilter
import com.example.controlcomprasapp.data.local.datasource.ProductoUI
import com.example.controlcomprasapp.data.local.datasource.TicketLocalDataSource
import com.example.controlcomprasapp.data.local.datasource.TipoProducto
import com.example.controlcomprasapp.data.local.db.DbHelper
import com.example.controlcomprasapp.domain.model.ItemTicket
import com.example.controlcomprasapp.domain.model.Locales

class ProductRepository(
    private val ticketLocalDataSource: TicketLocalDataSource,
    private val descuentoDataSource: DescuentoDataSource,
    private val localesDataSource: LocalDataSource
) {

    fun obtenerItems(filter: ProductFilter): List<ProductoUI> {
        return ticketLocalDataSource.obtenerItems(filter).map {
            ProductoUI(
                nombre = it.nombre,
                total = it.total,
                tipo = TipoProducto.ITEM
            )
        }
    }

    fun obtenerDescuentos(filter: ProductFilter): List<ProductoUI> {
        return descuentoDataSource.obtenerDescuentos(filter).map {
            ProductoUI(
                nombre = it.nombre,
                total = it.total,
                tipo = TipoProducto.DESCUENTO
            )
        }
    }

    fun obtenerLocales(): List<Locales> {
        return localesDataSource.getAllLocales().map {
            Locales(
                id = it.id,
                nombre = it.nombre
            )
        }
    }

}