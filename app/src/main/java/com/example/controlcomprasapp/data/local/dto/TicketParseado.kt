package com.example.controlcomprasapp.data.local.dto

import com.example.controlcomprasapp.domain.model.Descuentos
import com.example.controlcomprasapp.domain.model.ItemTicket

data class TicketParseado(
    val fecha: String?,
    val local: String,
    val archivo: String,
    val items: List<ItemTicket>,
    val descuentos: List<Descuentos>
)
