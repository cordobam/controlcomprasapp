package com.example.controlcomprasapp.ui.navegation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Facturas : Screen("facturas")
    object Productos : Screen("productos")

    object NuevaFactura : Screen("nueva_factura")

    object FacturaManual : Screen("factura_manual")
}