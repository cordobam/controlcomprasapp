package com.example.controlcomprasapp

import com.example.controlcomprasapp.data.parser.ParserGenerico
import com.example.controlcomprasapp.data.parser.ParserResumenTarjeta
import com.example.controlcomprasapp.data.parser.ParserUtils
import com.example.controlcomprasapp.domain.parser.ParserManager
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ParserResumenTarjetaTest {

    private val parser = ParserResumenTarjeta()

    private val resumenVisaGalicia = listOf(
        "BANCO DE GALICIA Y BUENOS AIRES S.A.",
        "RESUMEN DE CUENTA",
        "TITULAR: JUAN PEREZ",
        "FECHA DE VENCIMIENTO: 05/08/2026",
        "FECHA DE CIERRE: 20/07/2026",
        "CODIGO DE PAGO: 123456",
        "VISA",
        "--------------------------------",
        "CONSUMOS",
        "12/07 MERCADO LIBRE 1.234,56",
        "10/07 SUPERMERCADOS COTO 456,78",
        "05/07 TIENDA X CUOTA 1/3 150,00",
        "--------------------------------",
        "TOTAL 1.841,34",
        "PAGO MINIMO 91,00"
    )

    private val resumenMastercardNacion = listOf(
        "BANCO DE LA NACION ARGENTINA",
        "RESUMEN DE CUENTA TARJETA DE CREDITO",
        "FECHA DE CIERRE: 10/08/2026",
        "FECHA DE VENCIMIENTO: 25/08/2026",
        "MASTERCARD",
        "CODIGO DE PAGO: 999",
        "01/08 SUPERMERCADO COTO 3.500,00",
        "28/07 NETFLIX 1.234,56",
        "--------------------------------",
        "TOTAL 4.734,56"
    )

    private val resumenComafi = listOf(
        "VISA CLASSIC",
        "PAGINA: 1/4",
        "SUCURSAL: 999 - DIAGONAL",
        "GRUPO: 9989 - GENERAL SUMAPUNTOS",
        "CUIT: 30 60473101 8",
        "N DE CUENTA: 0536844130",
        "RESUMEN NRO.: 0375323 - 04 - 1 - CR0104",
        "TITULAR DE CUENTA: CORDOBA MARIANO",
        "CORDOBA MARIANO CIERRE ACTUAL: 30 Jul 26",
        "DEAN FUNES 5455 0 VENCIMIENTO SALDO $ SALDO U$S PAGO MIN.$ PAGO MIN.U$S",
        "5003 BARRIO PARQUE REPUBL 10 Ago 26 2.355.647,77 0,00 640.905,00 0,00",
        "CÓRDOBA CAR: 01",
        "VTO.ANTERIOR 13 Jul 26 SALDO ANTERIOR $ 1.804.174,17 PROXIMO CIERRE 27 Ago 26",
        "CIERRE ANTERIOR 02 Jul 26 PAGO MIN.ANT. $ 294.563,00 PROXIMO VTO. 07 Set 26",
        "03.07.26 007552* EXPRESS 22.405,00",
        "07.07.26 342991* GOOGLE *YouTubeP P1mDeHI7 6.799,00",
        "15.07.26 030302* SAN CRISTOBAL 1030730437748-007-000 31.511,00",
        "TARJETA 2396 Total Consumos de MARIANO CORDOBA 395.659,31 0,00",
        "Emisor: Banco Comafi SA Av. Pte. Roque Sáenz Peña 660",
        "el tipo de cambio vendedor del Banco Nación, correspondiente al día hábil anterior"
    )

    private val ticketSupermercado = listOf(
        "COTO C.I.C.S.A.",
        "PAGO TARJETA VISA",
        "FECHA 13/08/2026 18:24",
        "ARROZ GALLO 1KG   1 x 120,00",
        "ACEITE COCINERO   2 x 180,50",
        "TOTAL 481,00"
    )

    @Test
    fun puedeParsear_acepta_resumen_visa() {
        assertTrue(parser.puedeParsear(resumenVisaGalicia.joinToString("\n")))
    }

    @Test
    fun puedeParsear_acepta_resumen_mastercard() {
        assertTrue(parser.puedeParsear(resumenMastercardNacion.joinToString("\n")))
    }

    @Test
    fun puedeParsear_rechaza_ticket_supermercado_con_tarjeta() {
        assertFalse(parser.puedeParsear(ticketSupermercado.joinToString("\n")))
    }

    @Test
    fun puedeParsear_rechaza_texto_basura() {
        val basura = "Este es un texto cualquiera\nsin datos utiles\nfin"
        assertFalse(parser.puedeParsear(basura))
    }

    @Test
    fun parsea_banco_marca_y_fechas_de_galicia() {
        val ticket = parser.parser(resumenVisaGalicia)
        assertEquals("Galicia", ticket.banco)
        assertEquals("VISA", ticket.marca)
        assertEquals("2026-07-20", ticket.fecha)
        assertEquals("2026-08-05", ticket.fechaVencimiento)
        assertEquals("TARJETA", ticket.tipo)
        assertEquals("Resumen VISA - Galicia", ticket.local)
    }

    @Test
    fun parsea_consumos_con_miles_y_cuotas() {
        val ticket = parser.parser(resumenVisaGalicia)
        assertEquals(3, ticket.items.size)

        val mercadolibre = ticket.items[0]
        assertEquals("MERCADO LIBRE", mercadolibre.nombre)
        assertEquals(1234.56, mercadolibre.total, 0.001)
        assertEquals("12/07", mercadolibre.fechaConsumo)
        assertEquals(null, mercadolibre.cuotasTotal)

        val coto = ticket.items[1]
        assertEquals("SUPERMERCADOS COTO", coto.nombre)
        assertEquals(456.78, coto.total, 0.001)

        val tienda = ticket.items[2]
        assertEquals("TIENDA X CUOTA 1/3", tienda.nombre)
        assertEquals(150.0, tienda.total, 0.001)
        assertEquals(1, tienda.cuotaActual)
        assertEquals(3, tienda.cuotasTotal)
    }

    @Test
    fun parsea_resumen_mastercard() {
        val ticket = parser.parser(resumenMastercardNacion)
        assertEquals("Nacion", ticket.banco)
        assertEquals("MASTERCARD", ticket.marca)
        assertEquals("2026-08-10", ticket.fecha)
        assertEquals("2026-08-25", ticket.fechaVencimiento)
        assertEquals(2, ticket.items.size)
        assertEquals(3500.0, ticket.items[0].total, 0.001)
        assertEquals(1234.56, ticket.items[1].total, 0.001)
    }

    @Test
    fun local_fallback_cuando_no_hay_banco_ni_marca() {
        val ticket = parser.parser(
            listOf(
                "RESUMEN DE CUENTA",
                "FECHA DE CIERRE: 20/07/2026",
                "12/07 TIENDA 150,00"
            )
        )
        assertEquals("Resumen de tarjeta", ticket.local)
        assertEquals(1, ticket.items.size)
    }

    @Test
    fun puedeParsear_acepta_resumen_comafi() {
        assertTrue(parser.puedeParsear(resumenComafi.joinToString("\n")))
    }

    @Test
    fun parsea_resumen_comafi() {
        val ticket = parser.parser(resumenComafi)
        assertEquals("Comafi", ticket.banco)
        assertEquals("VISA", ticket.marca)
        assertEquals("2026-07-30", ticket.fecha)
        assertEquals("2026-08-10", ticket.fechaVencimiento)
        assertEquals("Resumen VISA - Comafi", ticket.local)
        assertEquals(3, ticket.items.size)
    }

    @Test
    fun parsea_consumos_comafi_con_fecha_puntos() {
        val ticket = parser.parser(resumenComafi)

        val express = ticket.items[0]
        assertEquals("EXPRESS", express.nombre)
        assertEquals(22405.0, express.total, 0.001)
        assertEquals("2026-07-03", express.fechaConsumo)

        val google = ticket.items[1]
        assertEquals("GOOGLE *YOUTUBEP P1MDEHI7", google.nombre)
        assertEquals(6799.0, google.total, 0.001)

        val sanCristobal = ticket.items[2]
        assertEquals("SAN CRISTOBAL 1030730437748-007-000", sanCristobal.nombre)
        assertEquals(31511.0, sanCristobal.total, 0.001)
    }

    @Test
    fun banco_comafi_ignora_banco_nacion_del_texto_legal() {
        val ticket = parser.parser(resumenComafi)
        assertEquals("Comafi", ticket.banco)
        assertFalse(ticket.banco == "Nacion")
    }

    @Test
    fun obtenerFechaCierre_formato_dd_mon_yy() {
        val lineas = listOf(
            "CORDOBA MARIANO CIERRE ACTUAL: 30 Jul 26",
            "5003 BARRIO PARQUE REPUBL 10 Ago 26 2.355.647,77"
        )
        assertEquals("2026-07-30", ParserUtils.obtenerFechaCierre(lineas))
    }

    @Test
    fun obtenerFechaVencimiento_formato_dd_mon_yy() {
        val lineas = listOf(
            "DEAN FUNES 5455 0 VENCIMIENTO SALDO $ SALDO U$S",
            "5003 BARRIO PARQUE REPUBL 10 Ago 26 2.355.647,77"
        )
        assertEquals("2026-08-10", ParserUtils.obtenerFechaVencimiento(lineas))
    }

    @Test
    fun obtenerFechaCierre_gana_a_vencimiento() {
        val lineas = listOf(
            "FECHA DE VENCIMIENTO: 05/08/2026",
            "FECHA DE CIERRE: 20/07/2026"
        )
        assertEquals("2026-07-20", ParserUtils.obtenerFechaCierre(lineas))
        assertEquals("2026-08-05", ParserUtils.obtenerFechaVencimiento(lineas))
    }

    @Test
    fun obtenerFechaCierre_lee_fecha_en_linea_siguiente() {
        val lineas = listOf(
            "FECHA DE CIERRE",
            "20/07/2026"
        )
        assertEquals("2026-07-20", ParserUtils.obtenerFechaCierre(lineas))
    }

    @Test
    fun parsearCuotas_trailing() {
        assertEquals(1 to 6, ParserUtils.parsearCuotas("12/07 TIENDA 456,78 1/6"))
        assertEquals(1 to 3, ParserUtils.parsearCuotas("05/07 TIENDA X CUOTA 1/3 150,00"))
        assertNull(ParserUtils.parsearCuotas("12/07 MERCADO LIBRE 1.234,56"))
    }

    @Test
    fun parsearMonto_con_miles() {
        assertEquals(1234.56, ParserUtils.parsearMonto("1.234,56"), 0.001)
        assertEquals(3500.0, ParserUtils.parsearMonto("3.500,00"), 0.001)
        assertEquals(150.0, ParserUtils.parsearMonto("150,00"), 0.001)
    }

    @Test
    fun esLineaConsumo_distingue_encabezados() {
        assertTrue(ParserUtils.esLineaConsumo("12/07 MERCADO LIBRE 1.234,56"))
        assertFalse(ParserUtils.esLineaConsumo("TOTAL 1.841,34"))
        assertFalse(ParserUtils.esLineaConsumo("FECHA DE CIERRE: 20/07/2026"))
    }

    @Test
    fun orden_en_parserManager() {
        val manager = ParserManager(
            listOf(
                ParserResumenTarjeta(),
                ParserGenerico()
            )
        )
        assertTrue(manager.obtenerParse(resumenVisaGalicia.joinToString("\n")) is ParserResumenTarjeta)
        assertTrue(manager.obtenerParse(ticketSupermercado.joinToString("\n")) is ParserGenerico)
    }
}