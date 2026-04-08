package com.example.controlcomprasapp.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalMall
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.controlcomprasapp.viewmodel.HomeViewModel
import com.example.controlcomprasapp.viewmodel.HomeViewModelFactory

val BackgroundDark = Color(0xFF111318)
val SurfaceDark = Color(0xFF1A1D24)
val BorderDark = Color(0xFF2A2D35)
val TextGray = Color(0xFF888888)
val TextLight = Color(0xFFF0F0F0)
val AccentGreen = Color(0xFF3DBA6E)
val AccentBlue = Color(0xFF4A9EFF)
val AccentOrange = Color(0xFFF0A030)

@Composable
fun HomeScreen(factory: HomeViewModelFactory) {
    val viewModel: HomeViewModel = viewModel(factory = factory)
    val items = viewModel.items
    val items_gastos = viewModel.items_gastos
    val items_prductos = viewModel.items_prductos

    // Valores de ejemplo para las métricas (puedes vincularlos a tu ViewModel)
    val totalGastado = items_gastos.sumOf { it.total.toDouble() }.toInt()
    val totalAhorrado = items.sumOf { it.total.toDouble() }.toInt()

    val scrollState = rememberScrollState()

    val BackgroundDark = Color(0xFF111318)
    val SurfaceDark = Color(0xFF1A1D24)
    val BorderDark = Color(0xFF2A2D35)
    val TextGray = Color(0xFF888888)
    val TextLight = Color(0xFFF0F0F0)
    val AccentGreen = Color(0xFF3DBA6E)
    val AccentBlue = Color(0xFF4A9EFF)
    val AccentOrange = Color(0xFFF0A030)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // ── HEADER ──
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Resumen del mes",
                    color = TextLight,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "Mirá en qué se va tu plata",
                    color = Color(0xFF555555),
                    fontSize = 12.sp
                )
            }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF1E2028))
                    .border(0.5.dp, BorderDark, RoundedCornerShape(8.dp))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text("Abril 2026", color = TextGray, fontSize = 12.sp)
            }
        }

        // ── MÉTRICAS TOTALES ──
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            MetricBox(Modifier.weight(1f), "Total gastado", "$$totalGastado", TextLight, SurfaceDark)
            MetricBox(Modifier.weight(1f), "Total ahorrado", "$$totalAhorrado", AccentGreen, SurfaceDark)
        }

        // ── SECCIÓN: TOP DESCUENTOS ──
        SectionCard(titulo = "Top descuentos", icono = Icons.Default.TrendingDown, colorIcono = Color(0xFFE05555), surfaceColor = SurfaceDark) {
            val maxDescuento = items.maxOfOrNull { it.total.toFloat() } ?: 1f
            items.take(3).forEachIndexed { index, it ->
                StatRow(
                    rank = index + 1,
                    nombre = it.nombre,
                    valor = "-$${it.total}",
                    porcentajeBarra = if(maxDescuento > 0) it.total.toFloat() / maxDescuento else 0f,
                    colorBarra = Color(0xFF1E4A2A),
                    esVerde = true,
                    textGray = TextGray
                )
            }
        }

        // ── SECCIÓN: GASTOS POR RUBRO ──
        SectionCard(titulo = "Gastos por rubro", icono = Icons.Default.Receipt, colorIcono = AccentBlue, surfaceColor = SurfaceDark) {
            val maxGasto = items_gastos.maxOfOrNull { it.total.toFloat() } ?: 1f
            items_gastos.take(3).forEachIndexed { index, it ->
                StatRow(
                    rank = index + 1,
                    nombre = it.seccion,
                    valor = "$${it.total}",
                    porcentajeBarra = if(maxGasto > 0) it.total.toFloat() / maxGasto else 0f,
                    colorBarra = Color(0xFF1A2A3A),
                    textGray = TextGray
                )
            }
        }

        // ── SECCIÓN: TOP PRODUCTOS ──
        SectionCard(titulo = "Top productos", icono = Icons.Default.LocalMall, colorIcono = AccentOrange, surfaceColor = SurfaceDark) {
            items_prductos.take(3).forEachIndexed { index, it ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RankBadge(index + 1)
                        Text(it.nombre, color = TextGray, fontSize = 12.sp)
                    }
                    Text("x${it.cant_veces}", color = Color(0xFF666666), fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
fun MetricBox(modifier: Modifier, label: String, value: String, colorValue: Color, bgColor: Color) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(bgColor)
            .padding(12.dp)
    ) {
        Text(label, color = Color(0xFF555555), fontSize = 11.sp)
        Text(value, color = colorValue, fontSize = 16.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun SectionCard(
    titulo: String,
    icono: ImageVector,
    colorIcono: Color,
    surfaceColor: Color,
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(surfaceColor)
            .border(0.5.dp, Color(0xFF222222), RoundedCornerShape(14.dp))
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(icono, null, modifier = Modifier.size(16.dp), tint = colorIcono)
            Text(titulo, color = Color(0xFFCCCCCC), fontSize = 13.sp, fontWeight = FontWeight.Medium)
        }
        HorizontalDivider(color = Color(0xFF222222), thickness = 0.5.dp) // <-- CORREGIDO
        Column(modifier = Modifier.padding(14.dp)) {
            content()
        }
    }
}

@Composable
fun StatRow(rank: Int, nombre: String, valor: String, porcentajeBarra: Float, colorBarra: Color, esVerde: Boolean = false, textGray: Color) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                RankBadge(rank)
                Text(nombre, color = textGray, fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
            Text(valor, color = if (esVerde) Color(0xFF3DBA6E) else Color(0xFFCCCCCC), fontSize = 12.sp, fontWeight = FontWeight.Medium)
        }
        Spacer(Modifier.height(8.dp))
        Box(modifier = Modifier.fillMaxWidth().height(3.dp).clip(CircleShape).background(Color(0xFF232630))) {
            Box(modifier = Modifier.fillMaxWidth(porcentajeBarra).fillMaxHeight().background(colorBarra))
        }
        Spacer(Modifier.height(8.dp))
    }
}

@Composable
fun RankBadge(rank: Int) {
    Box(
        modifier = Modifier.padding(end = 8.dp).size(18.dp).clip(RoundedCornerShape(6.dp)).background(Color(0xFF232630)),
        contentAlignment = Alignment.Center
    ) {
        Text(rank.toString(), color = Color(0xFF555555), fontSize = 10.sp)
    }
}

