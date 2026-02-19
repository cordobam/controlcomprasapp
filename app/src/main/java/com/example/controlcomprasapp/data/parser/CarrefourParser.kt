package com.example.controlcomprasapp.data.parser

import android.util.Log
import com.example.controlcomprasapp.domain.model.ItemTicket
import com.example.controlcomprasapp.domain.parser.TicketParser

class CarrefourParser : TicketParser {
    override fun puedeParsear(texto: String): Boolean {

        Log.d("PARSER_CHECK", "Texto recibido:")
        Log.d("PARSER_CHECK", texto)

        val resultado = texto.contains("Carrefour", ignoreCase = true)
        //return texto.contains("Carrefour", ignoreCase = true)

        Log.d("PARSER_CHECK", "Resultado contains: $resultado")

        return resultado
    }

    override fun parser(lineas: List<String>): List<ItemTicket> {
        val items = mutableListOf<ItemTicket>()

        // Regex para detectar cantidad x precio (ej: 1 x 4889,00)
        val regexPrecio = Regex("""(\d+)\s*[xX]\s*([\d,.]+)""")

        for (i in lineas.indices) {
            val limpia = lineas[i].trim()
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

                // 2. Si no estaba en la misma línea, buscar en las 3 líneas anteriores
                if (nombreEncontrado == null) {
                    for (j in (i - 1) downTo maxOf(0, i - 3)) {
                        val cand = lineas[j].trim()
                        if (esNombrePotencial(cand)) {
                            nombreEncontrado = cand
                            break
                        }
                    }
                }

                // 3. Si encontramos todo, guardamos el item
                if (nombreEncontrado != null && precioUni > 0.0) {
                    items.add(
                        ItemTicket(
                            nombre = nombreEncontrado,
                            cantidad = cantidad,
                            precioUnitario = precioUni,
                            total = cantidad * precioUni
                        )
                    )
                    Log.d("PARSE_OK", "Producto: $nombreEncontrado - Precio: $precioUni")
                }
            }
        }
        return items
    }


    fun esNombrePotencial(l: String): Boolean {
        val u = l.uppercase().trim()

        // 1. Filtros de palabras prohibidas (Rubros y Datos Fiscales)
        val listaNegra = listOf(
            "ALMACEN", "CARNICERIA", "BEBIDAS", "FRUTAS", "VERDURAS", "PERFUMERIA", "LIMPIEZA",
            "FACTURA", "CONSUMIDOR FINAL", "COD.006", "SUBTOTAL", "TOTAL", "CAE", "CUIT",
            "PAGO", "TARJETA", "CAJERO", "FECHA", "HORA", "P.V. NRO", "INICIO ACTIVIDAD",
            "ORIENTACION AL CONSUMIDOR", "RESPONSABLE INSCRIPTO"
        )

        if (listaNegra.any { u.contains(it) }) return false

        // 2. Filtros de formato
        if (u.startsWith("MC ")) return false // Descuentos "Mi Carrefour"
        if (u.contains("---")) return false // Líneas separadoras
        if (u.matches(Regex(""".*\d{10,}.*"""))) return false // Códigos de barras (EAN13)
        if (u == "BEBIDAS") return false
        if (u == "CARNICERIA") return false
        if (u == "ALMACEN") return false

        // 3. Validaciones de contenido
        val letras = l.count { it.isLetter() }
        val numeros = l.count { it.isDigit() }

        // Un nombre real suele tener al menos 5 letras y pocas cifras numéricas
        // (A diferencia de "18/08/2021" o "86041647...")
        return letras > 4 && letras > numeros && u.length > 4
    }
}