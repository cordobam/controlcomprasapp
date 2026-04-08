package com.example.controlcomprasapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.controlcomprasapp.data.local.datasource.ArchivoDataSource
import com.example.controlcomprasapp.data.local.datasource.LocalDataSource
import com.example.controlcomprasapp.ui.theme.ControlcomprasappTheme
import com.example.controlcomprasapp.data.local.datasource.TicketLocalDataSource
import com.example.controlcomprasapp.data.parser.CarrefourParser
import com.example.controlcomprasapp.data.repository.TicketRepository
import com.example.controlcomprasapp.domain.parser.ParserManager
import com.example.controlcomprasapp.ui.navegation.AppNavigation
import com.example.controlcomprasapp.ui.screens.facturas.FacturaScreen
import com.example.controlcomprasapp.viewmodel.FacturaViewModelFactory
import com.example.controlcomprasapp.data.local.datasource.DescuentoDataSource
import com.example.controlcomprasapp.data.local.datasource.HomeDataSource
import com.example.controlcomprasapp.data.repository.HomeRepository
import com.example.controlcomprasapp.data.repository.ProductRepository
import com.example.controlcomprasapp.viewmodel.HomeViewModelFactory
import com.example.controlcomprasapp.viewmodel.ProductViewModelFactory


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        setContent {

            val dataSource = TicketLocalDataSource(this)
            val localDataSource = LocalDataSource(this)
            val archivoDataSource = ArchivoDataSource(this)
            val descuentosDataSource = DescuentoDataSource(this)
            val homeDataSource = HomeDataSource(this)

            val repo = TicketRepository(dataSource, localDataSource, archivoDataSource, descuentosDataSource )

            val parseManager = ParserManager(listOf(CarrefourParser()))

            val factory = FacturaViewModelFactory(repo, parseManager)

            val productRepository = ProductRepository(dataSource , descuentosDataSource)
            val productFactory = ProductViewModelFactory(productRepository)

            val homeRepository = HomeRepository(HomeDataSoucrce = homeDataSource)
            val homeFactory = HomeViewModelFactory(homeRepository)

            ControlcomprasappTheme {
                AppNavigation(factory = factory,productFactory = productFactory, homeFactory = homeFactory)
            }
        }
    }
}

