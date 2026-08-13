package com.example.controlcomprasapp.ui.screens.home

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.LocalMall
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.controlcomprasapp.data.local.dto.DescuentosDTO
import com.example.controlcomprasapp.data.local.dto.ItemTicketDTO
import com.example.controlcomprasapp.data.local.dto.ProductoDTO
import com.example.controlcomprasapp.viewmodel.HomeViewModel
import com.example.controlcomprasapp.viewmodel.HomeViewModelFactory
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottom
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStart
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberColumnCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.common.component.rememberLineComponent
import com.patrykandpatrick.vico.compose.common.fill
import com.patrykandpatrick.vico.core.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.CartesianValueFormatter
import com.patrykandpatrick.vico.core.cartesian.data.columnSeries
import com.patrykandpatrick.vico.core.cartesian.layer.ColumnCartesianLayer
import kotlin.math.roundToInt

val BackgroundDark = Color(0xFF111318)

val SurfaceDark = Color(0xFF1A1D24)

val BorderDark = Color(0xFF2A2D35)

val TextGray = Color(0xFF888888)

val TextLight = Color(0xFFF0F0F0)

val AccentGreen = Color(0xFF3DBA6E)

val AccentBlue = Color(0xFF4A9EFF)

val AccentOrange = Color(0xFFF0A030)

// Clase auxiliar para definir las secciones del carrusel
data class CarouselSection(
    val titulo: String,
    val icono: ImageVector,
    val colorIcono: Color,
    val content: @Composable () -> Unit
)

@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("RememberReturnType")
@Composable
fun HomeScreen(factory: HomeViewModelFactory) {
    val viewModel: HomeViewModel = viewModel(factory = factory)
    val items = viewModel.items
    val items_gastos = viewModel.items_gastos
    val items_prductos = viewModel.items_prductos
    var expanded by remember { mutableStateOf(false) }
    var mostrarGraficos by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.loadMeses()
    }
    val meses = viewModel.items_mes

    var mesSeleccionado by remember(meses) {
        mutableStateOf(meses.firstOrNull())
    }

    LaunchedEffect(mesSeleccionado) {
        mesSeleccionado?.let {
            viewModel.cargarDatosPorMes(it.mes, it.anio)
        }
    }

    val totalGastado = items_gastos.sumOf { it.total.toDouble() }.toInt()
    val totalAhorrado = items.sumOf { it.total.toDouble() }.toInt()

    val scrollState = rememberScrollState()

    // --- DEFINICIÓN DE LAS SECCIONES PARA EL CARRUSEL ---
    val carouselSections = listOf(
        // Sección 1: Top Descuentos
        CarouselSection(
            titulo = "Top descuentos",
            icono = Icons.Default.TrendingDown,
            colorIcono = AccentGreen
        ) {
            val maxDescuento = items.maxOfOrNull { kotlin.math.abs(it.total.toFloat()) } ?: 1f
            items.take(5).forEachIndexed { index, it ->
                StatRow(
                    rank = index + 1,
                    nombre = it.nombre,
                    valor = "$${it.total}",
                    porcentajeBarra = if(maxDescuento > 0) kotlin.math.abs(it.total.toFloat()) / maxDescuento else 0f,
                    colorBarra = AccentGreen.copy(alpha = 0.2f),
                    colorBarraActiva = AccentGreen,
                    esVerde = true
                )
            }
        },
        // Sección 2: Gastos por Rubro
        CarouselSection(
            titulo = "Gastos por rubro",
            icono = Icons.Default.Receipt,
            colorIcono = AccentBlue
        ) {
            val maxGasto = items_gastos.maxOfOrNull { it.total.toFloat() } ?: 1f
            items_gastos.take(5).forEachIndexed { index, it ->
                StatRow(
                    rank = index + 1,
                    nombre = it.seccion,
                    valor = "$${it.total}",
                    porcentajeBarra = if(maxGasto > 0) it.total.toFloat() / maxGasto else 0f,
                    colorBarra = AccentBlue.copy(alpha = 0.1f),
                    colorBarraActiva = AccentBlue
                )
            }
        },
        // Sección 3: Top Productos
        CarouselSection(
            titulo = "Top productos",
            icono = Icons.Default.LocalMall,
            colorIcono = AccentOrange
        ) {
            items_prductos.take(5).forEachIndexed { index, it ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RankBadge(index + 1)
                        Text(it.nombre, color = TextLight, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                    }
                    Text("x${it.cant_veces}", color = TextGray, fontSize = 13.sp)
                }
            }
        }
    )

    // Estado del Carrusel de Secciones
    val pagerState = rememberPagerState(pageCount = { carouselSections.size })

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
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
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Estado actual de tus finanzas",
                    color = TextGray,
                    fontSize = 14.sp
                )
            }

            // 👇 ESTE BOX ES LA CLAVE
            Box {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF1E2028))
                        .border(0.5.dp, BorderDark, RoundedCornerShape(8.dp))
                        .clickable { expanded = true }
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = mesSeleccionado?.label ?: "",
                        color = TextGray,
                        fontSize = 12.sp
                    )
                }

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    meses.forEach { mes ->
                        DropdownMenuItem(
                            text = { Text(mes.label) },
                            onClick = {
                                mesSeleccionado = mes
                                expanded = false
                            }
                        )
                    }
                }
            }
        }

        // ── MÉTRICAS TOTALES (Vuelven a ser estáticas) ──
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            MetricBox(Modifier.weight(1f), "Total gastado", "$$totalGastado", Color(0xFFEF4444), SurfaceDark)
            MetricBox(Modifier.weight(1f), "Total ahorrado", "$$totalAhorrado", AccentGreen, SurfaceDark)
        }

        // ── CARRUSEL DE SECCIONES (SectionCard) ──
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            HorizontalPager(
                state = pagerState,
                contentPadding = PaddingValues(horizontal = 0.dp),
                pageSpacing = 16.dp
            ) { page ->
                val section = carouselSections[page]
                // Reutilizamos SectionCard pero dentro del carrusel
                SectionCard(
                    titulo = section.titulo,
                    icono = section.icono,
                    colorIcono = section.colorIcono,
                    surfaceColor = SurfaceDark,
                    onVerGrafico = { mostrarGraficos = true }
                ) {
                    section.content()
                }
            }

            // Indicador de puntos (Dots) para el carrusel
            Row(
                Modifier.fillMaxWidth().padding(top = 8.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                repeat(carouselSections.size) { iteration ->
                    val color = if (pagerState.currentPage == iteration) AccentBlue else BorderDark
                    Box(
                        modifier = Modifier
                            .padding(3.dp)
                            .clip(CircleShape)
                            .background(color)
                            .size(8.dp)
                    )
                }
            }
        }

        // ── CARD DE GRÁFICOS (visible al tocar "Ver gráfico") ──
        if (mostrarGraficos) {
            GraficosCard(
                descuentos = items,
                gastos = items_gastos,
                productos = items_prductos
            )
        }

        Spacer(modifier = Modifier.height(50.dp)) // Espacio para el menú inferior
    }
}

// --- COMPONENTES AUXILIARES (Ligeramente retocados para el look pro clara) ---

@Composable
fun MetricBox(modifier: Modifier, label: String, value: String, colorValue: Color, bgColor: Color) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(bgColor)
            .border(1.dp, BorderDark, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Text(label, color = TextGray, fontSize = 12.sp, fontWeight = FontWeight.Medium)
        Text(value, color = colorValue, fontSize = 20.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun SectionCard(titulo: String, icono: ImageVector, colorIcono: Color, surfaceColor: Color, onVerGrafico: (() -> Unit)? = null, content: @Composable () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp) // Altura fija para que todas las cards del carrusel midan lo mismo
            .clip(RoundedCornerShape(20.dp))
            .background(surfaceColor)
            .border(1.dp, BorderDark, RoundedCornerShape(20.dp))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(icono, null, modifier = Modifier.size(20.dp), tint = colorIcono)
            Text(titulo, color = TextLight, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.weight(1f))
            if (onVerGrafico != null) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(colorIcono.copy(alpha = 0.15f))
                        .clickable { onVerGrafico() }
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text("Ver gráfico", color = colorIcono, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }
        HorizontalDivider(color = BorderDark, thickness = 1.dp)
        Column(modifier = Modifier.padding(16.dp).verticalScroll(rememberScrollState())) { // Scroll interno si hay muchos items
            content()
        }
    }
}

@Composable
fun StatRow(rank: Int, nombre: String, valor: String, porcentajeBarra: Float, colorBarra: Color, colorBarraActiva: Color, esVerde: Boolean = false) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                RankBadge(rank)
                Text(nombre, color = TextLight, fontSize = 14.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
            Text(valor, color = if (esVerde) AccentGreen else TextLight, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(10.dp))
        Box(modifier = Modifier.fillMaxWidth().height(6.dp).clip(CircleShape).background(colorBarra)) {
            Box(modifier = Modifier.fillMaxWidth(porcentajeBarra).fillMaxHeight().clip(CircleShape).background(colorBarraActiva))
        }
    }
}

@Composable
fun RankBadge(rank: Int) {
    Box(
        modifier = Modifier.padding(end = 10.dp).size(22.dp).clip(RoundedCornerShape(6.dp)).background(BackgroundDark),
        contentAlignment = Alignment.Center
    ) {
        Text(rank.toString(), color = TextGray, fontSize = 11.sp, fontWeight = FontWeight.Bold)
    }
}

// --- GRÁFICOS DEL MES ---

val DonutColors = listOf(
    AccentBlue,
    AccentGreen,
    AccentOrange,
    Color(0xFFA78BFA),
    Color(0xFFF472B6),
    Color(0xFF34D399),
    Color(0xFFFBBF24),
    Color(0xFF64748B)
)

@Composable
fun GraficosCard(
    descuentos: List<DescuentosDTO>,
    gastos: List<ItemTicketDTO>,
    productos: List<ProductoDTO>
) {
    val totalTitulos = listOf("Top descuentos", "Gastos por rubro", "Top productos")
    val graficosPagerState = rememberPagerState(pageCount = { totalTitulos.size })

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(SurfaceDark)
            .border(1.dp, BorderDark, RoundedCornerShape(20.dp))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(Icons.Default.BarChart, null, modifier = Modifier.size(20.dp), tint = AccentBlue)
            Text("Gráficos del mes", color = TextLight, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
        HorizontalDivider(color = BorderDark, thickness = 1.dp)
        Spacer(Modifier.height(12.dp))

        HorizontalPager(
            state = graficosPagerState,
            pageSpacing = 16.dp,
            contentPadding = PaddingValues(horizontal = 0.dp),
            modifier = Modifier.fillMaxWidth().height(480.dp)
        ) { page ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                when (page) {
                    0 -> GraficoTopDescuentos(descuentos)
                    1 -> GraficoGastosRubro(gastos)
                    else -> GraficoTopProductos(productos)
                }
            }
        }

        Spacer(Modifier.height(8.dp))
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(totalTitulos.size) { iteration ->
                val color = if (graficosPagerState.currentPage == iteration) AccentBlue else BorderDark
                Box(
                    modifier = Modifier
                        .padding(3.dp)
                        .clip(CircleShape)
                        .background(color)
                        .size(8.dp)
                )
            }
        }
    }
}

@Composable
fun GraficoTopDescuentos(items: List<DescuentosDTO>) {
    val top = items.take(5)
    Column(modifier = Modifier.fillMaxWidth()) {
        Text("Top descuentos", color = TextGray, fontSize = 12.sp, fontWeight = FontWeight.Medium)
        Spacer(Modifier.height(8.dp))
        if (top.isEmpty()) {
            Box(Modifier.fillMaxWidth().height(180.dp), contentAlignment = Alignment.Center) {
                Text("Sin datos", color = TextGray, fontSize = 13.sp)
            }
        } else {
            VicoColumnChart(
                valores = top.map { it.total },
                nombres = top.map { it.nombre },
                color = AccentGreen
            )
            Spacer(Modifier.height(12.dp))
            top.forEach { item ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        Box(Modifier.size(10.dp).clip(RoundedCornerShape(3.dp)).background(AccentGreen))
                        Spacer(Modifier.width(8.dp))
                        Text(item.nombre, color = TextLight, fontSize = 13.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                    Text("$${item.total}", color = AccentGreen, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun GraficoGastosRubro(items: List<ItemTicketDTO>) {
    val top = items.take(7).filter { it.total > 0 }
    val total = top.sumOf { it.total.toDouble() }.toFloat()
    Column(modifier = Modifier.fillMaxWidth()) {
        Text("Gastos por rubro", color = TextGray, fontSize = 12.sp, fontWeight = FontWeight.Medium)
        Spacer(Modifier.height(8.dp))
        if (top.isEmpty() || total <= 0f) {
            Box(Modifier.fillMaxWidth().height(180.dp), contentAlignment = Alignment.Center) {
                Text("Sin datos", color = TextGray, fontSize = 13.sp)
            }
        } else {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Canvas(
                    modifier = Modifier
                        .padding(vertical = 12.dp)
                        .size(180.dp)
                ) {
                    val strokeWidth = 30.dp.toPx()
                    val inset = strokeWidth / 2
                    var startAngle = -90f
                    top.forEachIndexed { index, item ->
                        val sweepAngle = (item.total.toFloat() / total * 360f)
                        drawArc(
                            color = DonutColors[index % DonutColors.size],
                            startAngle = startAngle,
                            sweepAngle = sweepAngle - 1f,
                            useCenter = false,
                            topLeft = Offset(inset, inset),
                            size = Size(size.width - strokeWidth, size.height - strokeWidth),
                            style = Stroke(width = strokeWidth)
                        )
                        startAngle += sweepAngle
                    }
                }
            }
            Spacer(Modifier.height(8.dp))
            top.forEachIndexed { index, item ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        Box(Modifier.size(10.dp).clip(RoundedCornerShape(3.dp)).background(DonutColors[index % DonutColors.size]))
                        Spacer(Modifier.width(8.dp))
                        Text(item.seccion, color = TextLight, fontSize = 13.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                    Text("$${item.total}", color = TextLight, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun GraficoTopProductos(items: List<ProductoDTO>) {
    val top = items.take(5)
    Column(modifier = Modifier.fillMaxWidth()) {
        Text("Top productos", color = TextGray, fontSize = 12.sp, fontWeight = FontWeight.Medium)
        Spacer(Modifier.height(8.dp))
        if (top.isEmpty()) {
            Box(Modifier.fillMaxWidth().height(180.dp), contentAlignment = Alignment.Center) {
                Text("Sin datos", color = TextGray, fontSize = 13.sp)
            }
        } else {
            VicoColumnChart(
                valores = top.map { it.cant_veces },
                nombres = top.map { it.nombre },
                color = AccentOrange
            )
            Spacer(Modifier.height(12.dp))
            top.forEach { item ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        Box(Modifier.size(10.dp).clip(RoundedCornerShape(3.dp)).background(AccentOrange))
                        Spacer(Modifier.width(8.dp))
                        Text(item.nombre, color = TextLight, fontSize = 13.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                    Text("x${item.cant_veces}", color = AccentOrange, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun VicoColumnChart(
    valores: List<Number>,
    nombres: List<String>,
    color: Color,
    modifier: Modifier = Modifier
) {
    if (valores.isEmpty()) {
        Box(modifier.fillMaxWidth().height(180.dp), contentAlignment = Alignment.Center) {
            Text("Sin datos", color = TextGray, fontSize = 13.sp)
        }
        return
    }
    val modelProducer = remember { CartesianChartModelProducer() }
    LaunchedEffect(valores) {
        modelProducer.runTransaction {
            columnSeries {
                series(valores)
            }
        }
    }
    CartesianChartHost(
        chart = rememberCartesianChart(
            rememberColumnCartesianLayer(
                columnProvider = ColumnCartesianLayer.ColumnProvider.series(
                    rememberLineComponent(fill = fill(color), thickness = 18.dp)
                )
            ),
            startAxis = VerticalAxis.rememberStart(),
            bottomAxis = HorizontalAxis.rememberBottom(
                valueFormatter = CartesianValueFormatter { _, value, _ ->
                    nombres.getOrNull(value.roundToInt())?.let { if (it.isNotEmpty()) it.take(10) else "·" } ?: "·"
                }
            )
        ),
        modelProducer = modelProducer,
        modifier = modifier.fillMaxWidth()
    )
}

