package com.example.controlcomprasapp.data.repository

import com.example.controlcomprasapp.data.local.datasource.ArchivoDataSource
import com.example.controlcomprasapp.data.local.datasource.LocalDataSource
import com.example.controlcomprasapp.data.local.datasource.TicketLocalDataSource
import com.example.controlcomprasapp.domain.model.ItemTicket
import com.example.controlcomprasapp.domain.model.Tickets

class TicketRepository(
    private val local: TicketLocalDataSource,
    private val localDataSource: LocalDataSource,
    private val archivoDataSource: ArchivoDataSource
) {

    fun guardarTicketCompleto(
        fecha: String,
        nombreLocal: String,
        nombreArchivo: String,
        items: List<ItemTicket>
    ) {

        val archivoId = archivoDataSource.insertOrGet(nombreArchivo)

        val localId = localDataSource.insertOrGet(nombreLocal)

        val ticketId = local.guardarTicket(
            Tickets(
                fecha = fecha,
                id_local = localId,
                id_archivo = archivoId
            )
        )

        local.guardarItems(items, ticketId)
    }


    //fun guardarTicket(items: List<ItemTicket>) {
    //    local.guardarItems(items)
    //}

    fun listarTickets(): List<ItemTicket> {
        return local.obtenerItems()
   }
}
