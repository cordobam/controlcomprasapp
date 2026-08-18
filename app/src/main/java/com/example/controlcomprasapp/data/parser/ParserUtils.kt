package com.example.controlcomprasapp.data.parser

object ParserUtils {

    private val regexFecha = Regex("""\b\d{1,2}[/-]\d{1,2}[/-]\d{2,4}\b""")
    private val regexFechaMon = Regex("""\b(\d{1,2})\s+([A-Z]{3,4})\.?\s+(\d{2,4})\b""")
    private val regexLineaPrecio = Regex("""\d+\s*[xX]\s*[\d,.]+""")
    private val regexPrecioDolar = Regex("""\$\s*[\d,.]+""")
    private val regexPrecioFinal = Regex("""\d[\d.,]*,\d{2}$""")

    private val meses = mapOf(
        "ENE" to 1, "FEB" to 2, "MAR" to 3, "ABR" to 4, "MAY" to 5, "JUN" to 6,
        "JUL" to 7, "AGO" to 8, "SEP" to 9, "SET" to 9, "SEPT" to 9,
        "OCT" to 10, "NOV" to 11, "DIC" to 12
    )

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

    private val bancos = listOf(
        "BANCO DE GALICIA" to "Galicia",
        "GALICIA" to "Galicia",
        "BANCO DE LA NACION" to "Nacion",
        "BANCO NACION" to "Nacion",
        "NACION" to "Nacion",
        "BANCO MACRO" to "Macro",
        "BANCO CIUDAD" to "Ciudad",
        "BANCO PROVINCIA" to "Provincia",
        "BANCO HIPOTECARIO" to "Hipotecario",
        "BANCO FRANCES" to "Frances",
        "BANCO SUPERVIELLE" to "Supervielle",
        "BANCO COMAFI" to "Comafi",
        "COMAFI" to "Comafi",
        "BBVA" to "BBVA",
        "SANTANDER" to "Santander",
        "ICBC" to "ICBC"
    )

    private val marcasTarjeta = listOf(
        "AMERICAN EXPRESS",
        "AMEX",
        "MASTERCARD",
        "VISA",
        "CABAL",
        "NARANJA",
        "DINERS"
    )

    private val regexConsumo = Regex("""(\d{1,2}[./-]\d{1,2}(?:[./-]\d{2,4})?)\s+(.+?)\s+([\d.]+,\d{2})""")
    private val regexCuota = Regex("""CUOTA\s+(\d{1,3})\s*(?:DE\s*|/)?\s*(\d{1,3})""")
    private val regexCuotaFinal = Regex("""\(?(\d{1,3})\s*/\s*(\d{1,3})\)?\s*$""")

    data class Consumo(
        val fecha: String?,
        val nombre: String,
        val monto: Double
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
        for (i in lineas.indices) {
            val l = lineas[i].uppercase()
            if (l.contains("FECHA")) {
                extraerFechaDeLinea(l)?.let { return it }
                lineas.getOrNull(i + 1)?.let { extraerFechaDeLinea(it.uppercase()) }?.let { return it }
            }
        }

        for (l in lineas) {
            extraerFechaDeLinea(l.uppercase())?.let { return it }
        }
        return null
    }

    fun obtenerFechaVencimiento(lineas: List<String>): String? {
        return buscarFechaConSenal(lineas, listOf("VENCIMIENTO", "VENCE"))
    }

    fun obtenerFechaCierre(lineas: List<String>): String? {
        return buscarFechaConSenal(lineas, listOf("CIERRE", "PERIODO"))
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

    fun detectarBanco(lineas: List<String>): String? {
        for (linea in lineas) {
            val u = linea.uppercase()
            if (u.contains("EMISOR")) {
                buscarBancoEn(u)?.let { return it }
            }
        }

        val limite = minOf(lineas.size, 25)
        for (i in 0 until limite) {
            buscarBancoEn(lineas[i].uppercase())?.let { return it }
        }
        return null
    }

    private fun buscarBancoEn(linea: String): String? {
        for ((razon, nombre) in bancos) {
            if (linea.contains(razon)) return nombre
        }
        return null
    }

    fun detectarMarcaTarjeta(texto: String): String? {
        val u = normalizarTexto(texto)
        for (marca in marcasTarjeta) {
            if (u.contains(marca)) return marca
        }
        return null
    }

    fun esLineaConsumo(linea: String): Boolean {
        return parsearConsumo(linea) != null
    }

    fun parsearConsumo(linea: String): Consumo? {
        val match = regexConsumo.find(linea.uppercase()) ?: return null
        val fechaRaw = match.groupValues[1]
        val nombre = match.groupValues[2]
            .replace(Regex("""^\d{3,}\*?\s+"""), "")
            .trim()
        val monto = parsearMonto(match.groupValues[3]) ?: return null
        val fecha = convertirFecha(fechaRaw) ?: fechaRaw
        return Consumo(fecha = fecha, nombre = nombre, monto = monto)
    }

    fun parsearCuotas(linea: String): Pair<Int?, Int?>? {
        val u = linea.uppercase()
        val m1 = regexCuota.find(u)
        if (m1 != null) {
            return m1.groupValues[1].toIntOrNull() to m1.groupValues[2].toIntOrNull()
        }
        val m2 = regexCuotaFinal.find(u)
        if (m2 != null && m2.range.first > 0) {
            return m2.groupValues[1].toIntOrNull() to m2.groupValues[2].toIntOrNull()
        }
        return null
    }

    fun parsearMonto(texto: String): Double? {
        val limpio = texto.trim().replace(".", "").replace(",", ".")
        return limpio.toDoubleOrNull()
    }

    private fun buscarFechaConSenal(lineas: List<String>, senales: List<String>): String? {
        val ventana = 5
        for (i in lineas.indices) {
            val l = lineas[i].uppercase()
            if (senales.any { l.contains(it) }) {
                for (j in i until minOf(i + ventana, lineas.size)) {
                    extraerFechaDeLinea(lineas[j].uppercase())?.let { return it }
                }
            }
        }
        return null
    }

    private fun extraerFechaDeLinea(linea: String): String? {
        val num = regexFecha.find(linea)?.value?.let { convertirFecha(it) }
        if (num != null) return num

        val m = regexFechaMon.find(linea) ?: return null
        val dia = m.groupValues[1].toIntOrNull() ?: return null
        val mes = meses[m.groupValues[2]] ?: return null
        val anioRaw = m.groupValues[3]
        val anio = if (anioRaw.length <= 2) "20$anioRaw" else anioRaw
        val anioInt = anio.toIntOrNull() ?: return null
        if (anioInt < 1900 || dia !in 1..31) return null
        return String.format("%04d-%02d-%02d", anioInt, mes, dia)
    }

    private fun convertirFecha(fecha: String): String? {
        val normalizada = fecha.replace('.', '/').replace('-', '/')
        val partes = normalizada.split("/")
        if (partes.size != 3) return null
        val dia = partes[0].toIntOrNull() ?: return null
        val mes = partes[1].toIntOrNull() ?: return null
        val anioRaw = partes[2]
        val anio = if (anioRaw.length <= 2) "20$anioRaw" else anioRaw
        val anioInt = anio.toIntOrNull() ?: return null
        if (anioInt < 1900 || mes !in 1..12 || dia !in 1..31) return null
        return String.format("%04d-%02d-%02d", anioInt, mes, dia)
    }
}