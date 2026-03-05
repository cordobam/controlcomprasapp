package com.example.controlcomprasapp.domain.model

data class Descuentos(
    val id: Int,
    val ticket_id: Int,
    val nombre: String,
    val total: Double,
    val id_archivo: Int
)
