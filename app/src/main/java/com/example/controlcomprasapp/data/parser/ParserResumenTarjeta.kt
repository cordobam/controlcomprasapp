package com.example.controlcomprasapp.data.parser

import com.example.controlcomprasapp.data.local.dto.TicketParseado
import com.example.controlcomprasapp.domain.model.ItemTicket
import com.example.controlcomprasapp.domain.parser.TicketParser

class ParserResumenTarjeta : TicketParser {

    private val senalesResumen = listOf(
        "RESUMEN DE CUENTA",
        "FECHA DE VENCIMIENTO",
        "FECHA DE CIERRE",
        "CODIGO DE PAGO"
    )

    override fun puedeParsear(texto: String): Boolean {
        if (texto.isBlank()) return false
        val normalizado = ParserUtils.normalizarTexto(texto)

        val marca = ParserUtils.detectarMarcaTarjeta(normalizado) ?: return false

        val tieneSenal = senalesResumen.any { normalizado.contains(it) }
        val resumenConTarjeta = normalizado.contains("RESUMEN") && normalizado.contains("TARJETA")

        return tieneSenal || resumenConTarjeta
    }

    override fun parser(lineas: List<String>): TicketParseado {
        val textoCompleto = lineas.joinToString("\n")

        val banco = ParserUtils.detectarBanco(lineas)
        val marca = ParserUtils.detectarMarcaTarjeta(textoCompleto)
        val fecha = ParserUtils.obtenerFechaCierre(lineas)
            ?: ParserUtils.obtenerFechaVencimiento(lineas)
        val fechaVencimiento = ParserUtils.obtenerFechaVencimiento(lineas)

        val items = mutableListOf<ItemTicket>()

        for (linea in lineas) {
            val consumo = ParserUtils.parsearConsumo(linea) ?: continue
            val cuotas = ParserUtils.parsearCuotas(linea)

            items.add(
                ItemTicket(
                    nombre = consumo.nombre,
                    ticket_id = 0,
                    cantidad = 1,
                    precioUnitario = consumo.monto,
                    total = consumo.monto,
                    seccion = "CONSUMOS",
                    fechaConsumo = consumo.fecha,
                    cuotasTotal = cuotas?.second,
                    cuotaActual = cuotas?.first
                )
            )
        }

        val partes = listOfNotNull(marca, banco)
        val local = if (partes.isEmpty()) "Resumen de tarjeta" else "Resumen ${partes.joinToString(" - ")}"

        return TicketParseado(
            fecha = fecha,
            local = local,
            archivo = "",
            items = items,
            descuentos = emptyList(),
            tipo = "TARJETA",
            banco = banco,
            marca = marca,
            fechaVencimiento = fechaVencimiento
        )
    }
}