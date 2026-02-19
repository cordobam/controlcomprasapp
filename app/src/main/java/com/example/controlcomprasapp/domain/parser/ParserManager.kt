package com.example.controlcomprasapp.domain.parser

class ParserManager(private val parsers: List<TicketParser>) {
    fun obtenerParse(textoCompleto: String): TicketParser? {
        return parsers.firstOrNull(){
            it.puedeParsear(textoCompleto)
        }
    }
}