package com.example.controlcomprasapp.data.parser

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.controlcomprasapp.data.local.dto.TicketParseado
import com.example.controlcomprasapp.domain.model.Descuentos
import com.example.controlcomprasapp.domain.model.ItemTicket
import com.example.controlcomprasapp.domain.parser.TicketParser
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.toString

class CarrefourParser : TicketParser {
    override fun puedeParsear(texto: String): Boolean {
        val limpio = normalizarTexto(texto)

        Log.d("puedeParsear", limpio)

        return limpio.contains("INC SA")

    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun parser(lineas: List<String>): TicketParseado {

        val items = mutableListOf<ItemTicket>()
        val descuentos = mutableListOf<Descuentos>()

        val establecimiento = "Carrefour"
        val fecha = obtenerFecha(lineas)

        val regexPrecio = Regex("""(\d+)\s*[xX]\s*([\d,.]+)""")
        val regexDescuento = Regex("""(.+?)\s+(-\d+[.,]\d+)""")

        var dentroSeccionDescuentos = false
        var seccionActual = "General"

        for (i in lineas.indices) {

            val limpia = lineas[i]
                .replace('\u00A0', ' ')
                .replace('­', '-')
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
            if (esSeccion(limpia)) {
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

                if (esNombrePotencial(parteAntesPrecio)) {
                    nombreEncontrado = parteAntesPrecio
                }

                if (nombreEncontrado == null) {

                    val lineaAnterior = lineas
                        .getOrNull(i - 1)
                        ?.trim()

                    if (lineaAnterior != null &&
                        esNombrePotencial(lineaAnterior)
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
        if (u.contains("BOLSAS VERDES")) return false
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


    @RequiresApi(Build.VERSION_CODES.O)
    fun obtenerFecha(lineas: List<String>): String? {

        val regexFecha = Regex("""\b\d{1,2}[/-]\d{1,2}[/-]\d{2,4}\b""")

        val formatterEntradaCorto = DateTimeFormatter.ofPattern("d/M/yy")
        val formatterEntradaLargo = DateTimeFormatter.ofPattern("d/M/yyyy")
        val formatterSalida = DateTimeFormatter.ISO_LOCAL_DATE // yyyy-MM-dd

        fun convertirFecha(fecha: String): String? {
            return try {
                val date = try {
                    LocalDate.parse(fecha, formatterEntradaCorto)
                } catch (e: Exception) {
                    LocalDate.parse(fecha, formatterEntradaLargo)
                }
                date.format(formatterSalida)
            } catch (e: Exception) {
                null
            }
        }

        for (i in lineas.indices) {
            val l = lineas[i].uppercase()

            if (l.contains("FECHA")) {
                val match = regexFecha.find(l)
                if (match != null) return convertirFecha(match.value)

                val siguiente = lineas.getOrNull(i + 1)
                val match2 = siguiente?.let { regexFecha.find(it) }
                if (match2 != null) return convertirFecha(match2.value)
            }
        }

        return null
    }

    fun esSeccion(linea: String): Boolean {

        val secciones = listOf(
            "ALMACEN",
            "BEBIDAS",
            "CARNICERIA",
            "PANADERIA",
            "VERDURAS",
            "FRUTAS",
            "LIMPIEZA",
            "PERFUMERIA",
            "OTROS"
        )

        return secciones.contains(linea.uppercase().trim())
    }

    fun normalizarTexto(texto: String): String {
        return texto
            .replace('\u00A0', ' ')   // espacios raros → espacio normal
            .replace('­', '-')        // guiones raros → guion normal
            .replace(Regex("\\s+"), " ") // múltiples espacios → uno solo
            .uppercase()
            .trim()
    }
}