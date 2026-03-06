package com.example.controlcomprasapp.data.parser

import android.util.Log
import com.example.controlcomprasapp.data.local.dto.TicketParseado
import com.example.controlcomprasapp.domain.model.Descuentos
import com.example.controlcomprasapp.domain.model.ItemTicket
import com.example.controlcomprasapp.domain.parser.TicketParser

class CarrefourParser : TicketParser {
    override fun puedeParsear(texto: String): Boolean {

        val resultado = texto.contains("Carrefour", ignoreCase = true)
        //return texto.contains("Carrefour", ignoreCase = true)

        return resultado
    }

    override fun parser(lineas: List<String>): TicketParseado  {
        val items = mutableListOf<ItemTicket>()
        val descuentos = mutableListOf<Descuentos>()
        val establecimiento = "Carrefour"
        val fecha = obtenerFecha(lineas)

        // Regex para detectar cantidad x precio (ej: 1 x 4889,00)
        val regexPrecio = Regex("""(\d+)\s*[xX]\s*([\d,.]+)""")
        val regexDescuento = Regex("""MC\s+(.+?)\s+(-?\d+[.,]\d+)""")
        Log.d("LINEAS_OK", "MATCH: $lineas")

        for (i in lineas.indices) {
            val limpia = lineas[i]
                .replace('\u00A0', ' ')
                .replace('­', '-')
                .trim()
            val matchDesc = regexDescuento.find(limpia)


            if (matchDesc != null) {

                val nombreDesc = matchDesc.groupValues[1].trim()
                val totalDesc = matchDesc.groupValues[2].replace(",", ".").toDoubleOrNull() ?: 0.0

                descuentos.add(
                    Descuentos(
                        id = 0,
                        ticket_id = 0,
                        nombre = nombreDesc,
                        total = totalDesc,
                        id_archivo = 0
                    )
                )

                continue
            }

            val matchPrecio = regexPrecio.find(limpia)

            if (matchPrecio != null) {
                val cantidad = matchPrecio.groupValues[1].toIntOrNull() ?: 1
                val precioUni = matchPrecio.groupValues[2].replace(",", ".").toDoubleOrNull() ?: 0.0

                // --- ESTRATEGIA PARA EL NOMBRE ---
                var nombreEncontrado: String? = null

                // 1. ¿El nombre está en la misma línea antes del precio?
                // Ejemplo: "MILA CON PROVENZAL 1 x 4889,00" -> tomamos "MILA CON PROVENZAL"
                val parteAntesDelPrecio = limpia.substring(0, matchPrecio.range.first).trim()
                if (esNombrePotencial(parteAntesDelPrecio)) {
                    nombreEncontrado = parteAntesDelPrecio
                }


                // 2. Si no estaba en la misma línea, buscar SOLO en la línea anterior
                if (nombreEncontrado == null) {
                    val lineaAnterior = lineas.getOrNull(i - 1)?.trim()
                    if (lineaAnterior != null && esNombrePotencial(lineaAnterior)) {
                        nombreEncontrado = lineaAnterior
                    }
                }

                // 3. Si encontramos todo, guardamos el item
                if (nombreEncontrado != null && precioUni > 0.0) {
                    items.add(
                        ItemTicket(
                            nombre = nombreEncontrado,
                            ticket_id = 0,
                            cantidad = cantidad,
                            precioUnitario = precioUni,
                            total = cantidad * precioUni
                        )
                    )
                }
            }
        }

        return TicketParseado(
            fecha = fecha,
            local = establecimiento,
            archivo= "",
            items = items,
            descuentos = descuentos
        )
    }


    fun esNombrePotencial(l: String): Boolean {
        val u = l
            .replace(Regex("\\s+"), " ")
            .uppercase()
            .trim()

        Log.d("PARSER", u.take(5).map { it.code }.toString())

        // 1. Filtros de palabras prohibidas (Rubros y Datos Fiscales)
        val listaNegra = listOf(
            "ALMACEN", "CARNICERIA", "BEBIDAS", "FRUTAS", "VERDURAS", "PERFUMERIA", "LIMPIEZA",
            "FACTURA", "CONSUMIDOR FINAL", "COD.006", "SUBTOTAL", "TOTAL", "CAE", "CUIT",
            "PAGO", "TARJETA", "CAJERO", "FECHA", "HORA", "P.V. NRO", "INICIO ACTIVIDAD",
            "ORIENTACION AL CONSUMIDOR", "RESPONSABLE INSCRIPTO", "OTROS"
        )

        if (listaNegra.any { u == it }) return false

        // 2. Filtros de formato
        if (u.startsWith("MC ")) return false // Descuentos "Mi Carrefour"
        if (u.contains("---")) return false // Líneas separadoras
        if (u.contains("BOLSAS NEGRAS")) return false
        if (u.matches(Regex(""".*\d{10,}.*"""))) return false // Códigos de barras (EAN13)
        if (u == "BEBIDAS") return false
        if (u == "CARNICERIA") return false
        if (u == "ALMACEN") return false
        if (u == "OTROS") return false

        // 3. Validaciones de contenido
        val letras = l.count { it.isLetter() }
        val numeros = l.count { it.isDigit() }

        // Un nombre real suele tener al menos 5 letras y pocas cifras numéricas
        // (A diferencia de "18/08/2021" o "86041647...")
        return letras > 4 && letras > numeros && u.length > 4
    }


    fun obtenerFecha(lineas: List<String>): String? {
        val regexFecha = Regex("""\b\d{1,2}[/-]\d{1,2}[/-]\d{2,4}\b""")

        for (i in lineas.indices) {
            val l = lineas[i].uppercase()

            if (l.contains("FECHA")) {
                val match = regexFecha.find(l)
                if (match != null) return match.value

                val siguiente = lineas.getOrNull(i + 1)
                val match2 = siguiente?.let { regexFecha.find(it) }
                if (match2 != null) return match2.value
            }
        }
        return null
    }
}