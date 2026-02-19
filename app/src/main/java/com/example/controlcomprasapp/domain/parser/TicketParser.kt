package com.example.controlcomprasapp.domain.parser

import android.util.Log
import com.example.controlcomprasapp.domain.model.ItemTicket

interface TicketParser {

    fun puedeParsear(texto: String): Boolean

    fun parser(lineas: List<String>): List<ItemTicket>
}
