package com.example.controlcomprasapp.data.repository

import com.example.controlcomprasapp.data.local.datasource.ProductFilter
import com.example.controlcomprasapp.data.local.datasource.TicketLocalDataSource
import com.example.controlcomprasapp.data.local.db.DbHelper
import com.example.controlcomprasapp.domain.model.ItemTicket

class ProductRepository(
    private val ticketLocalDataSource: TicketLocalDataSource
) {

    fun obtenerItems(filter: ProductFilter): List<ItemTicket> {
        return ticketLocalDataSource.obtenerItems(filter)
    }

}