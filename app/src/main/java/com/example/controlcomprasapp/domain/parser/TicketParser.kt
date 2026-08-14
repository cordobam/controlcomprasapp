package com.example.controlcomprasapp.domain.parser

import com.example.controlcomprasapp.data.local.dto.TicketParseado

interface TicketParser {

    fun puedeParsear(texto: String): Boolean

    fun parser(lineas: List<String>): TicketParseado
}
