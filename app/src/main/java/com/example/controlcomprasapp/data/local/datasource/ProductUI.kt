package com.example.controlcomprasapp.data.local.datasource

data class ProductoUI(
    val nombre: String,
    val total: Double,
    val tipo: TipoProducto
)

enum class TipoProducto {
    ITEM,
    DESCUENTO
}
