package com.example.controlcomprasapp.data.repository

import com.example.controlcomprasapp.data.local.datasource.TicketLocalDataSource
import com.example.controlcomprasapp.domain.model.ItemTicket

class TicketRepository(
    private val local: TicketLocalDataSource
) {

    fun guardarTicket(items: List<ItemTicket>) {
        local.guardarItems(items)
    }

    fun listarTickets(): List<ItemTicket> {
        return local.obtenerItems()
    }
}
