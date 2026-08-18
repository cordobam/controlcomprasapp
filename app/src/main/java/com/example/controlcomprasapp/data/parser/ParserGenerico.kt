package com.example.controlcomprasapp.data.parser

import com.example.controlcomprasapp.data.local.dto.TicketParseado
import com.example.controlcomprasapp.domain.model.Descuentos
import com.example.controlcomprasapp.domain.model.ItemTicket
import com.example.controlcomprasapp.domain.parser.TicketParser

class ParserGenerico : TicketParser {

    override fun puedeParsear(texto: String): Boolean {
        if (texto.isBlank()) return false
        val lineas = texto.split("\n")
            .map { ParserUtils.normalizarTexto(it) }
            .filter { it.isNotBlank() }
        if (lineas.isEmpty()) return false

        val tieneFecha = ParserUtils.obtenerFecha(lineas) != null || lineas.any { it.contains("FECHA") }
        val lineasPrecio = lineas.count { ParserUtils.esLineaPrecio(it) }
        return tieneFecha && lineasPrecio >= 3
    }

    override fun parser(lineas: List<String>): TicketParseado {
        val items = mutableListOf<ItemTicket>()
        val descuentos = mutableListOf<Descuentos>()

        val fecha = ParserUtils.obtenerFecha(lineas)
        val local = ParserUtils.detectarLocal(lineas)

        val regexDescuento = Regex("""(.+?)\s+(-\d+[.,]\d+)""")
        val regexCantidadPrecio = Regex("""(\d+)\s*[xX]\s*([\d,.]+)""")
        val regexPrecioFinal = Regex("""\d[\d.,]*,\d{2}$""")

        var dentroSeccionDescuentos = false
        var seccionActual = "General"

        for (i in lineas.indices) {
            val limpia = ParserUtils.normalizarTexto(lineas[i])

            if (limpia == "DESCUENTOS") {
                dentroSeccionDescuentos = true
                continue
            }

            if (dentroSeccionDescuentos) {
                if (limpia.contains("AHORRO") || limpia.contains("TOTAL")) {
                    dentroSeccionDescuentos = false
                    continue
                }

                val matchDesc = regexDescuento.find(limpia)
                if (matchDesc != null) {
                    val nombre = matchDesc.groupValues[1].trim()
                    val total = matchDesc.groupValues[2]
                        .replace(",", ".")
                        .toDoubleOrNull() ?: 0.0
                    descuentos.add(
                        Descuentos(
                            id = 0,
                            ticket_id = 0,
                            nombre = nombre,
                            total = total,
                            id_archivo = 0
                        )
                    )
                }
                continue
            }

            if (ParserUtils.esSeccion(limpia)) {
                seccionActual = limpia
                continue
            }

            var cantidad = 1
            var precio: Double? = null
            var nombreEncontrado: String? = null

            val matchCantidad = regexCantidadPrecio.find(limpia)
            if (matchCantidad != null) {
                cantidad = matchCantidad.groupValues[1].toIntOrNull() ?: 1
                precio = ParserUtils.parsearMonto(matchCantidad.groupValues[2])
                val parteAntes = limpia.substring(0, matchCantidad.range.first).trim()
                if (ParserUtils.esNombrePotencial(parteAntes)) {
                    nombreEncontrado = parteAntes
                }
            } else {
                val matchFinal = regexPrecioFinal.find(limpia)
                if (matchFinal != null) {
                    precio = ParserUtils.parsearMonto(matchFinal.value)
                    val parteAntes = limpia.substring(0, matchFinal.range.first).replace("$", "").trim()
                    if (ParserUtils.esNombrePotencial(parteAntes)) {
                        nombreEncontrado = parteAntes
                    }
                }
            }

            if (nombreEncontrado == null) {
                val lineaAnterior = lineas.getOrNull(i - 1)?.trim()
                if (lineaAnterior != null && ParserUtils.esNombrePotencial(lineaAnterior)) {
                    nombreEncontrado = lineaAnterior
                }
            }

            if (nombreEncontrado != null && precio != null && precio > 0.0) {
                items.add(
                    ItemTicket(
                        nombre = nombreEncontrado,
                        ticket_id = 0,
                        cantidad = cantidad,
                        precioUnitario = precio,
                        total = cantidad * precio,
                        seccion = seccionActual
                    )
                )
            }
        }

        return TicketParseado(
            fecha = fecha,
            local = local,
            archivo = "",
            items = items,
            descuentos = descuentos
        )
    }
}