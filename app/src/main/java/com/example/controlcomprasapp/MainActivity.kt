package com.example.controlcomprasapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

            val dataSource = TicketLocalDataSource(this)
            val localDataSource = LocalDataSource(this)
            val archivoDataSource = ArchivoDataSource(this)
            val repo = TicketRepository(dataSource, localDataSource, archivoDataSource)

            val parseManager = ParserManager(listOf(CarrefourParser()))

            val factory = FacturaViewModelFactory(repo, parseManager)

            ControlcomprasappTheme {
                AppNavigation(factory = factory)
            }
        }
    }
}

