package com.example.controlcomprasapp.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.navigation.NavController
import com.example.controlcomprasapp.ui.navegation.Screen
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(navController: NavController) {

    var expanded by remember { mutableStateOf(false) }

    TopAppBar(
        title = { Text("WaterDrop App") },
        actions = {

            IconButton(onClick = { expanded = true }) {
                Icon(Icons.Default.MoreVert, contentDescription = "Menu")
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {

                DropdownMenuItem(
                    text = { Text("Página Principal") },
                    onClick = {
                        expanded = false
                        navController.navigate(Screen.Home.route)
                    }
                )

                DropdownMenuItem(
                    text = { Text("Carga de Facturas") },
                    onClick = {
                        expanded = false
                        navController.navigate(Screen.Facturas.route)
                    }
                )

                DropdownMenuItem(
                    text = { Text("Productos Cargados") },
                    onClick = {
                        expanded = false
                        navController.navigate(Screen.Productos.route)
                    }
                )
            }
        }
    )
}