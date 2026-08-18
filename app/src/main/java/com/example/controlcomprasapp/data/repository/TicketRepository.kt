package com.example.controlcomprasapp.data.repository

import android.util.Log
import com.example.controlcomprasapp.data.local.datasource.ArchivoDataSource
import com.example.controlcomprasapp.data.local.datasource.DescuentoDataSource
import com.example.controlcomprasapp.data.local.datasource.LocalDataSource
import com.example.controlcomprasapp.data.local.datasource.ProductFilter
import com.example.controlcomprasapp.data.local.datasource.TicketLocalDataSource
import com.example.controlcomprasapp.domain.model.Descuentos
import com.example.controlcomprasapp.domain.model.ItemTicket
import com.example.controlcomprasapp.domain.model.Tickets

class TicketRepository(
    private val local: TicketLocalDataSource,
    private val localDataSource: LocalDataSource,
    private val archivoDataSource: ArchivoDataSource,
    private val descuentosDataSource: DescuentoDataSource
) {

    fun guardarTicketCompleto(
        fecha: String,
        nombreLocal: String,
        nombreArchivo: String,
        items: List<ItemTicket>,
        descuentos: List<Descuentos>,
        esManual: Boolean = false,
        tipo: String = "COMPRA",
        banco: String? = null,
        marca: String? = null,
        fechaVencimiento: String? = null
    ): Boolean{

        val archivoId: Long

        if (esManual) {
            val archivo = archivoDataSource.insertOrGet("manual_${System.currentTimeMillis()}")
            archivoId = archivo.id
        } else {

            val archivoResult  = archivoDataSource.insertOrGet(nombreArchivo)

            if (!archivoResult .inserted) {
                Log.d("TICKET_DUPLICADO", "El ticket ya fue cargado")
                return false
            }

            archivoId = archivoResult.id
        }



        val localId = localDataSource.insertOrGet(nombreLocal)

        val ticketId = local.guardarTicket(
            Tickets(
                fecha = fecha,
                id_local = localId,
                id_archivo = archivoId,
                tipo = tipo,
                banco = banco,
                marca = marca,
                fechaVencimiento = fechaVencimiento
            )
        )



        local.guardarItems(items, ticketId)
        descuentosDataSource.guardarDescuentos(descuentos, ticketId , archivoId)

        return true
    }


    //fun guardarTicket(items: List<ItemTicket>) {
    //    local.guardarItems(items)
    //}

    fun listarTickets(filter: ProductFilter): List<ItemTicket> {
        return local.obtenerItems(filter)
   }
}
