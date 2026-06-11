package com.example.controlcomprasapp.domain.model

data class GastoFijo(
    val id: Long = 0,
    val nombre: String,
    val monto: Double
)

data class GastoMensual(
    val id: Long = 0,
    val idGastoFijo: Long? = null,
    val nombre: String,
    val monto: Double,
    val mes: Int,
    val anio: Int,
    val esFijo: Boolean = false,
    val pagado: Boolean = false
)

data class Ingreso(
    val id: Long = 0,
    val nombre: String,
    val monto: Double,
    val mes: Int,
    val anio: Int
)
