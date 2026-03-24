package com.example.controlcomprasapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.controlcomprasapp.domain.model.ItemTicket
import com.example.controlcomprasapp.domain.model.Descuentos

@Composable
fun TabsItemsDescuentos(
    items: List<ItemTicket>,
    descuentos: List<Descuentos>,
    modifier: Modifier = Modifier
) {
    var tabSeleccionado by rememberSaveable { mutableStateOf(0) }

    Column(modifier = modifier) {

        TabRow(selectedTabIndex = tabSeleccionado) {
            Tab(
                selected = tabSeleccionado == 0,
                onClick = { tabSeleccionado = 0 }
            ) {
                Text("🛒 Items")
            }

            Tab(
                selected = tabSeleccionado == 1,
                onClick = { tabSeleccionado = 1 }
            ) {
                Text("💸 Descuentos")
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (tabSeleccionado == 0) {
                items(items) { item ->
                    Column(
                        Modifier
                            .padding(horizontal = 8.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color.LightGray.copy(alpha = 0.1f))
                            .padding(10.dp)
                    ) {
                        Text(item.nombre, fontWeight = FontWeight.Bold)
                        Text("Cant: ${item.cantidad}")
                        Text("Unit: ${item.precioUnitario}")
                        Text("Total: ${item.total}")
                    }
                }
            } else {
                items(descuentos) { item ->
                    Column(
                        Modifier
                            .padding(horizontal = 8.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color.LightGray.copy(alpha = 0.1f))
                            .padding(10.dp)
                    ) {
                        Text(item.nombre, fontWeight = FontWeight.Bold)
                        Text("Total: ${item.total}")
                    }
                }
            }
        }
    }
}