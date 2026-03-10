package com.example.controlcomprasapp.ui.navegation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
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
import com.example.controlcomprasapp.ui.screens.productos.ProductosScreen
import com.example.controlcomprasapp.viewmodel.ProductViewModel
import com.example.controlcomprasapp.viewmodel.ProductViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation(factory: FacturaViewModelFactory,
                  productFactory: ProductViewModelFactory
) {

    val navController = rememberNavController()

    Scaffold(
        topBar = {
            TopBar(navController)
        }
    ) { paddingValues ->

        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(paddingValues)
        ) {

            composable(Screen.Home.route) {
                HomeScreen()
            }

            composable(Screen.Facturas.route) {
                FacturaScreen(
                    factory = factory,
                    navController = navController
                )
            }

            composable(Screen.Productos.route) {
                val viewModel: ProductViewModel = viewModel(
                    factory = productFactory
                )

                ProductosScreen(viewModel)
            }
        }
    }
}