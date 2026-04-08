package com.example.controlcomprasapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.LocalOffer
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.controlcomprasapp.domain.model.ItemTicket
import com.example.controlcomprasapp.domain.model.Descuentos

@Composable
fun TabsItemsDescuentos(
    listaItems: List<ItemTicket>,
    descuentos: List<Descuentos>,
    modifier: Modifier = Modifier
) {
    val surfaceDark = Color(0xFF1A1D24)
    val borderDark = Color(0xFF2A2D35)
    val accentBlue = Color(0xFF4A9EFF)

    var tabSeleccionado by rememberSaveable { mutableStateOf(0) }

    Column(modifier = modifier) {

        // TABS SELECTOR
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(surfaceDark)
                .border(0.5.dp, borderDark, RoundedCornerShape(12.dp))
                .padding(4.dp)
        ) {
            listOf("Ítems" to listaItems.size, "Descuentos" to descuentos.size).forEachIndexed { index, (label, count) ->
                val selected = tabSeleccionado == index
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (selected) Color(0xFF1C2A3A) else Color.Transparent)
                        .clickable { tabSeleccionado = index }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            label,
                            color = if (selected) accentBlue else Color(0xFF888888),
                            fontSize = 13.sp,
                            fontWeight = if (selected) FontWeight.Medium else FontWeight.Normal
                        )
                        if (count > 0) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (selected) Color(0xFF1F4068) else Color(0xFF232630))
                                    .padding(horizontal = 6.dp, vertical = 1.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    "$count",
                                    color = if (selected) accentBlue else Color(0xFF555555),
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // LISTA O ESTADO VACÍO
        val listaVacia = (tabSeleccionado == 0 && listaItems.isEmpty()) || (tabSeleccionado == 1 && descuentos.isEmpty())

        if (listaVacia) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = if (tabSeleccionado == 0) Icons.Outlined.ShoppingCart else Icons.Outlined.LocalOffer,
                        contentDescription = null,
                        tint = Color(0xFF333333),
                        modifier = Modifier.size(32.dp)
                    )
                    Text(
                        if (tabSeleccionado == 0) "Sin ítems escaneados" else "Sin descuentos detectados",
                        color = Color(0xFF444444),
                        fontSize = 13.sp
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (tabSeleccionado == 0) {
                    items(listaItems) { item ->
                        ItemFila(item, surfaceDark, borderDark)
                    }
                } else {
                    items(descuentos) { descuento ->
                        DescuentoFila(descuento)
                    }
                }
            }
        }
    }
}

// Separé las filas en componentes pequeños para que el código sea más legible
@Composable
fun ItemFila(item: ItemTicket, surfaceDark: Color, borderDark: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(surfaceDark)
            .border(0.5.dp, borderDark, RoundedCornerShape(10.dp))
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(item.nombre, color = Color(0xFFDDDDDD), fontSize = 13.sp, fontWeight = FontWeight.Medium)
            Text("x${item.cantidad}  ·  unit: $${item.precioUnitario}", color = Color(0xFF666666), fontSize = 11.sp)
        }
        Text("$${item.total}", color = Color(0xFFCCCCCC), fontSize = 14.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun DescuentoFila(descuento: Descuentos) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0xFF1A1F1A))
            .border(0.5.dp, Color(0xFF1E3A1E), RoundedCornerShape(10.dp))
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(descuento.nombre, color = Color(0xFFAAAAAA), fontSize = 13.sp, modifier = Modifier.weight(1f))
        Text("-$${descuento.total}", color = Color(0xFF3DBA6E), fontSize = 14.sp, fontWeight = FontWeight.Medium)
    }
}