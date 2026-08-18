package com.example.controlcomprasapp.domain.model

data class Tickets(
    val id: Long = 0,
    val fecha: String,
    val id_local: Long,
    val id_archivo: Long,
    val tipo: String = "COMPRA",
    val banco: String? = null,
    val marca: String? = null,
    val fechaVencimiento: String? = null
)
