package com.example.controlcomprasapp.ui.navegation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.controlcomprasapp.viewmodel.FacturaViewModelFactory
import com.example.controlcomprasapp.ui.components.TopBar
import com.example.controlcomprasapp.ui.screens.facturas.FacturaScreen
import com.example.controlcomprasapp.ui.screens.home.HomeScreen
import com.example.controlcomprasapp.ui.screens.home.SurfaceDark
import com.example.controlcomprasapp.ui.screens.productos.ProductosScreen
import com.example.controlcomprasapp.viewmodel.HomeViewModelFactory
import com.example.controlcomprasapp.viewmodel.ProductViewModel
import com.example.controlcomprasapp.viewmodel.ProductViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation(
    factory: FacturaViewModelFactory,
    productFactory: ProductViewModelFactory,
    homeFactory: HomeViewModelFactory
) {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            NavigationBar(containerColor = SurfaceDark) {
                NavigationBarItem(
                    selected = false,
                    onClick = { navController.navigate(Screen.Home.route) },
                    icon = { Icon(Icons.Default.Home, null) },
                    label = { Text("Inicio") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { navController.navigate(Screen.Facturas.route) },
                    icon = { Icon(Icons.Default.AddCircle, null) },
                    label = { Text("Facturas") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { navController.navigate(Screen.Productos.route) },
                    icon = { Icon(Icons.Default.List, null) },
                    label = { Text("Productos") }
                )
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(Screen.Home.route) { HomeScreen(homeFactory) }
            composable(Screen.Facturas.route) { FacturaScreen(factory, navController) }
            composable(Screen.Productos.route) {
                val viewModel: ProductViewModel = viewModel(factory = productFactory)
                ProductosScreen(viewModel)
            }
        }
    }
}