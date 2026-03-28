package com.example.controlcomprasapp.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.controlcomprasapp.viewmodel.HomeViewModel
import com.example.controlcomprasapp.viewmodel.HomeViewModelFactory

@Composable
fun HomeScreen( factory: HomeViewModelFactory) {

    val viewModel: HomeViewModel = viewModel(factory = factory)
    val items = viewModel.items

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        // 🔹 TEXTOS ARRIBA
        Text(
            text = "📊 Resumen del mes",
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp
        )

        Text(
            text = "Mirá en qué se va tu plata",
            color = Color.Gray
        )

        // 🔹 CARDS (2x2)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            InfoCard(
                titulo = "🔥 Top descuentos",
                modifier = Modifier.weight(1f)
            ) {
                items.forEach {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(it.nombre)
                        Text("$${it.total}")
                    }
                }
            }

            //InfoCard("Ahorro", "$45.000", Modifier.weight(1f))
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            //InfoCard("Ticket prom.", "$12.500", Modifier.weight(1f))
            //InfoCard("Top producto", "Coca Cola", Modifier.weight(1f))
        }
    }


}

@Composable
fun InfoCard(
    titulo: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
            .padding(16.dp)
    ) {
        Text(titulo, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(8.dp))

        content() // 👈 clave
    }
}

