package com.example.controlcomprasapp

import com.example.controlcomprasapp.data.parser.ParserGenerico
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ParserGenericoTest {

    private val parser = ParserGenerico()

    private val ticketCoto = listOf(
        "COTO C.I.C.S.A.",
        "JOSE C. PAZ",
        "R.N.O.S.: 001401",
        "AV. GAUCHOS 2515",
        "CONSUMIDOR FINAL",
        "DOCUMENTO NO FISCAL",
        "FECHA 13/08/2021 18:24",
        "--------------------------------",
        "ALMACEN",
        "ARROZ GALLO 1KG     1 x 120,00",
        "ACEITE COCINERO     2 x 180,50",
        "BEBIDAS",
        "COCA-COLA 1,5L      2 x 250,00",
        "FRUTAS",
        "MANZANA ROJA        1 x 320,00",
        "--------------------------------",
        "SUBTOTAL             1201,00",
        "DESCUENTOS",
        "DESC. PROMO BANCO   -10,00",
        "AHORRO TARJETA      -45,20",
        "TOTAL               1155,80"
    )

    private val ticketJumbo = listOf(
        "JUMBO SAN MARTIN",
        "R.N.O.S: 003941",
        "FECHA 05/03/22",
        "CONSUMIDOR FINAL",
        "--------------------------------",
        "VERDURAS",
        "PAPA NEGRA 1 X 89,99",
        "TOMATE REDONDO 2 X 199,90",
        "--------------------------------",
        "SUBTOTAL 489,79",
        "DESCUENTOS",
        "PROMO JUMBO -50,00",
        "TOTAL 439,79"
    )

    private val ticketGenerico = listOf(
        "MERCADO LOS TILOS",
        "FECHA 12/07/2023",
        "CONSUMIDOR FINAL",
        "--------------------------------",
        "ALMACEN",
        "YERBA PLAYADITO 2 X 600,00",
        "GALLETITAS DIVERSIÓN 1 X 350,50",
        "--------------------------------",
        "SUBTOTAL 1550,50",
        "TOTAL 1550,50"
    )

    @Test
    fun puedeParsear_acepta_tickets_reales() {
        assertTrue(parser.puedeParsear(ticketCoto.joinToString("\n")))
        assertTrue(parser.puedeParsear(ticketJumbo.joinToString("\n")))
        assertTrue(parser.puedeParsear(ticketGenerico.joinToString("\n")))
    }

    @Test
    fun puedeParsear_rechaza_texto_basura() {
        val basura = "Este es un texto cualquiera\nsin precios\nsin datos utiles\nfin"
        assertFalse(parser.puedeParsear(basura))
    }

    @Test
    fun puedeParsear_rechaza_lineas_con_precios_pero_sin_fecha() {
        val sinFecha = "ACEITE 1 X 100,00\nARROZ 1 X 50,00\nYERBA 1 X 200,00"
        assertFalse(parser.puedeParsear(sinFecha))
    }

    @Test
    fun parsea_items_de_coto() {
        val ticket = parser.parser(ticketCoto)
        assertEquals(4, ticket.items.size)

        val arroz = ticket.items[0]
        assertEquals("ARROZ GALLO 1KG", arroz.nombre)
        assertEquals(1, arroz.cantidad)
        assertEquals(120.0, arroz.precioUnitario, 0.001)
        assertEquals(120.0, arroz.total, 0.001)
        assertEquals("ALMACEN", arroz.seccion)

        val aceite = ticket.items[1]
        assertEquals("ACEITE COCINERO", aceite.nombre)
        assertEquals(2, aceite.cantidad)
        assertEquals(180.50, aceite.precioUnitario, 0.001)
        assertEquals(361.0, aceite.total, 0.001)

        val manzana = ticket.items[3]
        assertEquals("MANZANA ROJA", manzana.nombre)
        assertEquals("FRUTAS", manzana.seccion)
    }

    @Test
    fun parsea_fecha_y_local_de_coto() {
        val ticket = parser.parser(ticketCoto)
        assertEquals("2021-08-13", ticket.fecha)
        assertEquals("Coto", ticket.local)
    }

    @Test
    fun parsea_descuentos_de_coto() {
        val ticket = parser.parser(ticketCoto)
        assertEquals(1, ticket.descuentos.size)
        assertEquals("DESC. PROMO BANCO", ticket.descuentos[0].nombre)
        assertEquals(-10.0, ticket.descuentos[0].total, 0.001)
    }

    @Test
    fun parsea_fecha_corta_de_jumbo() {
        val ticket = parser.parser(ticketJumbo)
        assertEquals("2022-03-05", ticket.fecha)
        assertEquals("Jumbo", ticket.local)
        assertEquals(2, ticket.items.size)
        assertEquals(1, ticket.descuentos.size)
    }

    @Test
    fun local_desconocido_cuando_no_se_reconoce() {
        val ticket = parser.parser(ticketGenerico)
        assertEquals("Desconocido", ticket.local)
        assertEquals("2023-07-12", ticket.fecha)
        assertEquals(2, ticket.items.size)
    }
}