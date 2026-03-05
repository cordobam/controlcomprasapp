package com.example.controlcomprasapp.domain.model

data class ItemTicket(
    val nombre: String,
    val ticket_id: Int,
    val cantidad: Int,
    val precioUnitario: Double,
    val total: Double
)
