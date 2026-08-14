package com.example.controlcomprasapp.data.parser

import com.example.controlcomprasapp.data.local.dto.TicketParseado
import com.example.controlcomprasapp.domain.model.Descuentos
import com.example.controlcomprasapp.domain.model.ItemTicket
import com.example.controlcomprasapp.domain.parser.TicketParser

class CarrefourParser : TicketParser {
    override fun puedeParsear(texto: String): Boolean {
        val limpio = ParserUtils.normalizarTexto(texto)
        return limpio.contains("INC SA")
    }

    override fun parser(lineas: List<String>): TicketParseado {

        val items = mutableListOf<ItemTicket>()
        val descuentos = mutableListOf<Descuentos>()

        val establecimiento = "Carrefour"
        val fecha = ParserUtils.obtenerFecha(lineas)

        val regexPrecio = Regex("""(\d+)\s*[xX]\s*([\d,.]+)""")
        val regexDescuento = Regex("""(.+?)\s+(-\d+[.,]\d+)""")

        var dentroSeccionDescuentos = false
        var seccionActual = "General"

        for (i in lineas.indices) {

            val limpia = lineas[i]
                .replace('\u00A0', ' ')
                .replace('\u00AD', '-')
                .trim()

            val u = limpia.uppercase()

            // =========================
            // DETECTAR SECCION DESCUENTOS
            // =========================

            if (u == "DESCUENTOS") {
                dentroSeccionDescuentos = true
                continue
            }

            if (dentroSeccionDescuentos) {

                if (u.contains("AHORRO") || u.contains("TOTAL")) {
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

            // =========================
            // ITEMS
            // =========================
            if (ParserUtils.esSeccion(limpia)) {
                seccionActual = limpia
                continue
            }
            val matchPrecio = regexPrecio.find(limpia)

            if (matchPrecio != null) {

                val cantidad = matchPrecio.groupValues[1].toIntOrNull() ?: 1
                val precioUni = matchPrecio.groupValues[2]
                    .replace(",", ".")
                    .toDoubleOrNull() ?: 0.0

                var nombreEncontrado: String? = null

                val parteAntesPrecio = limpia
                    .substring(0, matchPrecio.range.first)
                    .trim()

                if (ParserUtils.esNombrePotencial(parteAntesPrecio)) {
                    nombreEncontrado = parteAntesPrecio
                }

                if (nombreEncontrado == null) {

                    val lineaAnterior = lineas
                        .getOrNull(i - 1)
                        ?.trim()

                    if (lineaAnterior != null &&
                        ParserUtils.esNombrePotencial(lineaAnterior)
                    ) {
                        nombreEncontrado = lineaAnterior
                    }
                }

                if (nombreEncontrado != null && precioUni > 0.0) {

                    items.add(
                        ItemTicket(
                            nombre = nombreEncontrado,
                            ticket_id = 0,
                            cantidad = cantidad,
                            precioUnitario = precioUni,
                            total = cantidad * precioUni,
                            seccion = seccionActual
                        )
                    )
                }
            }
        }

        return TicketParseado(
            fecha = fecha,
            local = establecimiento,
            archivo = "",
            items = items,
            descuentos = descuentos
        )
    }
}