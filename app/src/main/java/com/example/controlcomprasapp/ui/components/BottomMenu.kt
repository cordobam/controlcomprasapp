package com.example.controlcomprasapp.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.SegmentedButtonDefaults.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.controlcomprasapp.ui.navegation.Screen
import com.example.controlcomprasapp.ui.screens.home.AccentBlue
import com.example.controlcomprasapp.ui.screens.home.SurfaceDark
import com.example.controlcomprasapp.ui.screens.home.TextGray
import com.example.controlcomprasapp.ui.screens.home.TextLight

@Composable
fun BottomMenu(navController: NavController) {
    // Obtenemos la ruta actual para resaltar el icono seleccionado
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar(
        containerColor = SurfaceDark, // El blanco puro que definimos
        tonalElevation = 8.dp
    ) {
        // Opción: Página Principal
        NavigationBarItem(
            selected = currentRoute == Screen.Home.route,
            onClick = { navController.navigate(Screen.Home.route) },
            label = { Text("Inicio", color = TextLight) },
            icon = {
                Icon(
                    Icons.Default.Home,
                    contentDescription = null,
                    tint = if (currentRoute == Screen.Home.route) AccentBlue else TextGray
                )
            }
        )

        // Opción: Carga de Facturas
        NavigationBarItem(
            selected = currentRoute == Screen.Facturas.route,
            onClick = { navController.navigate(Screen.Facturas.route) },
            label = { Text("Facturas", color = TextLight) },
            icon = {
                Icon(
                    Icons.Default.AddCircle, // Icono de carga
                    contentDescription = null,
                    tint = if (currentRoute == Screen.Facturas.route) AccentBlue else TextGray
                )
            }
        )

        // Opción: Productos Cargados
        NavigationBarItem(
            selected = currentRoute == Screen.Productos.route,
            onClick = { navController.navigate(Screen.Productos.route) },
            label = { Text("Productos", color = TextLight) },
            icon = {
                Icon(
                    Icons.Default.List,
                    contentDescription = null,
                    tint = if (currentRoute == Screen.Productos.route) AccentBlue else TextGray
                )
            }
        )
    }
}