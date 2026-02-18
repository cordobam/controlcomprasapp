package com.example.controlcomprasapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.controlcomprasapp.ui.theme.ControlcomprasappTheme
import androidx.compose.runtime.*
import com.example.controlcomprasapp.data.local.datasource.TicketLocalDataSource
import com.example.controlcomprasapp.data.repository.TicketRepository
import com.example.controlcomprasapp.ui.screens.FacturaScreen
import com.example.controlcomprasapp.viewmodel.FacturaViewModelFactory


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

            val dataSource = TicketLocalDataSource(this)
            val repo = TicketRepository(dataSource)
            val factory = FacturaViewModelFactory(repo)

            ControlcomprasappTheme {
                FacturaScreen(factory = factory)
            }
        }
    }
}

