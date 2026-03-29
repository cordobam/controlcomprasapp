package com.example.controlcomprasapp.ui.screens.home

import android.util.Log
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.LaunchedEffect
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
    val items_gastos = viewModel.items_gastos
    val items_mensual = viewModel.items_mensual
    val items_prductos = viewModel.items_prductos


    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .clip(RoundedCornerShape(10.dp))
            .background(Color.LightGray.copy(alpha = 0.1f))
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        Text(
            text = "📊 Resumen del mes",
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp
        )

        Text(
            text = "Mirá en qué se va tu plata",
            color = Color.Gray
        )

        InfoCard(titulo = "🔥 Top descuentos") {
            items.take(5).forEach {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(it.nombre, fontWeight = FontWeight.Bold)
                    Text("$${it.total}")
                }
            }
        }

        InfoCard(titulo = "🔥 Top Gastos x Rubro") {
            items_gastos.take(5).forEach {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(it.seccion, fontWeight = FontWeight.Bold)
                    Text("$${it.total}")
                }
            }
        }

        InfoCard(titulo = "🔥 Top Productos") {
            items_prductos.take(5).forEach {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(it.nombre, fontWeight = FontWeight.Bold)
                    Text("${it.cant_veces}")
                }
            }
        }

        InfoCard(titulo = "🔥 Gasto x Mes") {
            items_mensual.take(5).forEach {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(it.fecha, fontWeight = FontWeight.Bold)
                    Text("$${it.monto}")
                }
            }
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
            .background(Color.LightGray.copy(alpha = 0.1f))
            .padding(16.dp)
    ) {
        Text(titulo, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(8.dp))

        content() // 👈 clave
    }
}

