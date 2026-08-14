package com.example.controlcomprasapp.data.parser

import java.text.SimpleDateFormat
import java.util.Locale

object ParserUtils {

    private val regexFecha = Regex("""\b\d{1,2}[/-]\d{1,2}[/-]\d{2,4}\b""")
    private val regexLineaPrecio = Regex("""\d+\s*[xX]\s*[\d,.]+""")
    private val regexPrecioDolar = Regex("""\$\s*[\d,.]+""")
    private val regexPrecioFinal = Regex("""\d+[.,]\d{2}$""")

    private val secciones = listOf(
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

    private val listaNegra = listOf(
        "ALMACEN", "CARNICERIA", "BEBIDAS", "FRUTAS", "VERDURAS", "PERFUMERIA", "LIMPIEZA",
        "FACTURA", "CONSUMIDOR FINAL", "COD.006", "SUBTOTAL", "TOTAL", "CAE", "CUIT",
        "PAGO", "TARJETA", "CAJERO", "FECHA", "HORA", "P.V. NRO", "INICIO ACTIVIDAD",
        "ORIENTACION AL CONSUMIDOR", "RESPONSABLE INSCRIPTO", "OTROS"
    )

    private val prefijosProhibidos = listOf(
        "TOTAL", "SUBTOTAL", "PAGO", "VUELTO", "CAMBIO", "EFECTIVO",
        "TARJETA", "CAJERO", "FECHA", "DESCUENTO", "AHORRO"
    )

    private val locales = listOf(
        "COTO C.I.C.S.A." to "Coto",
        "COTO" to "Coto",
        "JUMBO" to "Jumbo",
        "WALMART" to "Walmart",
        "VEA" to "Vea",
        "CHANGOMAS" to "Changomas",
        "MAKRO" to "Makro",
        "LIBERTAD" to "Libertad",
        "DIARCO" to "Diarco",
        "LA ANONIMA" to "La Anonima",
        "CARREFOUR" to "Carrefour"
    )

    fun normalizarTexto(texto: String): String {
        return texto
            .replace('\u00A0', ' ')
            .replace('\u00AD', '-')
            .replace(Regex("\\s+"), " ")
            .uppercase()
            .trim()
    }

    fun esSeccion(linea: String): Boolean {
        return secciones.contains(linea.uppercase().trim())
    }

    fun esLineaPrecio(linea: String): Boolean {
        val l = linea.trim()
        if (l.isEmpty()) return false
        if (regexLineaPrecio.containsMatchIn(l)) return true
        if (regexPrecioDolar.containsMatchIn(l)) return true
        return regexPrecioFinal.containsMatchIn(l)
    }

    fun esNombrePotencial(l: String): Boolean {
        val limpio = l.replace("$", "").replace(Regex("\\s+"), " ").trim()
        val u = limpio.uppercase()
        if (u.isEmpty()) return false

        if (limpio.matches(Regex(""".*-\d+[.,]\d+.*"""))) return false

        if (listaNegra.any { u == it }) return false
        if (prefijosProhibidos.any { u.startsWith(it) }) return false

        if (u.startsWith("MC ")) return false
        if (u.contains("---")) return false
        if (u.contains("BOLSAS NEGRAS")) return false
        if (u.contains("BOLSAS VERDES")) return false
        if (u.matches(Regex(""".*\d{10,}.*"""))) return false

        val letras = limpio.count { it.isLetter() }
        val numeros = limpio.count { it.isDigit() }
        return letras > 4 && letras > numeros && u.length > 4
    }

    fun obtenerFecha(lineas: List<String>): String? {
        fun convertirFecha(fecha: String): String? {
            val formateadores = listOf(
                SimpleDateFormat("d/M/yyyy", Locale.US),
                SimpleDateFormat("d/M/yy", Locale.US)
            )
            for (formateador in formateadores) {
                formateador.isLenient = false
                try {
                    val date = formateador.parse(fecha) ?: continue
                    return SimpleDateFormat("yyyy-MM-dd", Locale.US).format(date)
                } catch (e: Exception) {
                    // probar el siguiente formato
                }
            }
            return null
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

        for (l in lineas) {
            val match = regexFecha.find(l)
            if (match != null) {
                val convertida = convertirFecha(match.value)
                if (convertida != null) return convertida
            }
        }
        return null
    }

    fun detectarLocal(lineas: List<String>): String {
        for (linea in lineas) {
            val u = linea.uppercase()
            for ((razon, nombre) in locales) {
                if (u.contains(razon)) return nombre
            }
        }
        return "Desconocido"
    }
}