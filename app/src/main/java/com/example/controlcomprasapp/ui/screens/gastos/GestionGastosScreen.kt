package com.example.controlcomprasapp.ui.screens.gastos

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.controlcomprasapp.domain.model.GastoMensual
import com.example.controlcomprasapp.domain.model.Ingreso
import com.example.controlcomprasapp.viewmodel.GastoViewModel
import java.time.LocalDate
import java.util.Locale
import java.time.format.TextStyle as MonthTextStyle

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun GestionGastosScreen(viewModel: GastoViewModel) {
    val backgroundDark = Color(0xFF111318)
    val surfaceDark = Color(0xFF1A1D24)
    val borderDark = Color(0xFF2A2D35)
    val accentBlue = Color(0xFF4A9EFF)
    val accentGreen = Color(0xFF3DBA6E)
    val accentRed = Color(0xFFEF4444)
    val textGray = Color(0xFF888888)
    val textLight = Color(0xFFF0F0F0)

    LaunchedEffect(Unit) {
        viewModel.cargarMes(
            LocalDate.now().monthValue,
            LocalDate.now().year
        )
    }

    var expandedMes by remember { mutableStateOf(false) }
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf(
        "Fijos (${viewModel.gastosDelMes.count { it.esFijo }})",
        "Excepcionales",
        "Ingresos"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundDark)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Gesti\u00f3n de gastos",
                color = textLight,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Box {
                val mesLabel = LocalDate.of(
                    viewModel.anioSeleccionado,
                    viewModel.mesSeleccionado, 1
                ).month.getDisplayName(MonthTextStyle.FULL, Locale("es", "ES"))
                    .replaceFirstChar { it.uppercase() }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(surfaceDark)
                        .border(0.5.dp, borderDark, RoundedCornerShape(8.dp))
                        .clickable { expandedMes = true }
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Text(
                        "$mesLabel ${viewModel.anioSeleccionado}",
                        color = textGray,
                        fontSize = 13.sp
                    )
                }

                DropdownMenu(
                    expanded = expandedMes,
                    onDismissRequest = { expandedMes = false }
                ) {
                    val current = LocalDate.now()
                    for (i in 0 until 12) {
                        val date = current.minusMonths(i.toLong())
                        val label = date.month.getDisplayName(MonthTextStyle.FULL, Locale("es", "ES"))
                            .replaceFirstChar { it.uppercase() } + " ${date.year}"
                        DropdownMenuItem(
                            text = { Text(label) },
                            onClick = {
                                viewModel.cargarMes(date.monthValue, date.year)
                                expandedMes = false
                            }
                        )
                    }
                }
            }
        }

        val totalGastos = viewModel.gastosDelMes.sumOf { it.monto }
        val totalIngresos = viewModel.ingresosDelMes.sumOf { it.monto }
        val saldo = viewModel.saldo

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(surfaceDark)
                .border(1.dp, borderDark, RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Ingresos", color = textGray, fontSize = 12.sp)
                    Text("$${String.format("%.2f", totalIngresos)}", color = accentGreen, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Gastos", color = textGray, fontSize = 12.sp)
                    Text("$${String.format("%.2f", totalGastos)}", color = accentRed, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                }
                HorizontalDivider(color = borderDark)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Te va quedando", color = textLight, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Text(
                        "$${String.format("%.2f", saldo)}",
                        color = if (saldo >= 0) accentGreen else accentRed,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(surfaceDark)
                .border(0.5.dp, borderDark, RoundedCornerShape(12.dp))
                .padding(4.dp)
        ) {
            tabs.forEachIndexed { index, label ->
                val isActive = selectedTab == index
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isActive) Color(0xFF1C2A3A) else Color.Transparent)
                        .clickable { selectedTab = index }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        label,
                        color = if (isActive) accentBlue else Color(0xFF666666),
                        fontSize = 11.sp,
                        fontWeight = if (isActive) FontWeight.Medium else FontWeight.Normal
                    )
                }
            }
        }

        Spacer(Modifier.height(8.dp))

        Box(modifier = Modifier.weight(1f)) {
            when (selectedTab) {
                0 -> TabGastosFijos(
                    gastos = viewModel.gastosDelMes.filter { it.esFijo },
                    onTogglePagado = { viewModel.togglePagado(it) },
                    onActualizarMonto = { id, monto -> viewModel.actualizarMontoGastoMensual(id, monto) },
                    onEliminar = { viewModel.eliminarGasto(it) },
                    onAgregar = { nombre, monto -> viewModel.agregarGastoFijo(nombre, monto) },
                    surfaceDark = surfaceDark,
                    borderDark = borderDark,
                    accentBlue = accentBlue,
                    accentGreen = accentGreen
                )
                1 -> TabGastosExcepcionales(
                    gastos = viewModel.gastosDelMes.filter { !it.esFijo },
                    onTogglePagado = { viewModel.togglePagado(it) },
                    onActualizarMonto = { id, monto -> viewModel.actualizarMontoGastoMensual(id, monto) },
                    onEliminar = { viewModel.eliminarGasto(it) },
                    onAgregar = { nombre, monto -> viewModel.agregarGastoExcepcional(nombre, monto) },
                    surfaceDark = surfaceDark,
                    borderDark = borderDark,
                    accentBlue = accentBlue,
                    accentGreen = accentGreen
                )
                2 -> TabIngresos(
                    ingresos = viewModel.ingresosDelMes,
                    onEliminar = { viewModel.eliminarIngreso(it) },
                    onAgregar = { nombre, monto -> viewModel.agregarIngreso(nombre, monto) },
                    surfaceDark = surfaceDark,
                    borderDark = borderDark,
                    accentBlue = accentBlue,
                    accentGreen = accentGreen
                )
            }
        }
    }
}

@Composable
private fun TabGastosFijos(
    gastos: List<GastoMensual>,
    onTogglePagado: (GastoMensual) -> Unit,
    onActualizarMonto: (Long, Double) -> Unit,
    onEliminar: (GastoMensual) -> Unit,
    onAgregar: (String, Double) -> Unit,
    surfaceDark: Color,
    borderDark: Color,
    accentBlue: Color,
    accentGreen: Color
) {
    var showAddDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        items(gastos) { gasto ->
            GastoRow(
                gasto = gasto,
                onTogglePagado = onTogglePagado,
                onActualizarMonto = onActualizarMonto,
                onEliminar = onEliminar,
                surfaceDark = surfaceDark,
                borderDark = borderDark,
                accentBlue = accentBlue,
                accentGreen = accentGreen
            )
        }

        if (gastos.isEmpty()) {
            item {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Sin gastos fijos cargados", color = Color(0xFF333333), fontSize = 13.sp)
                }
            }
        }

        item {
            Spacer(Modifier.height(4.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(surfaceDark)
                    .border(0.5.dp, borderDark, RoundedCornerShape(10.dp))
                    .clickable { showAddDialog = true }
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(Icons.Outlined.Add, null, tint = accentBlue, modifier = Modifier.size(18.dp))
                Text(" Agregar gasto fijo", color = accentBlue, fontSize = 13.sp)
            }
            Spacer(Modifier.height(16.dp))
        }
    }

    if (showAddDialog) {
        AgregarDialog(
            titulo = "Nuevo gasto fijo",
            onConfirm = { nombre, monto ->
                onAgregar(nombre, monto)
                showAddDialog = false
            },
            onDismiss = { showAddDialog = false }
        )
    }
}

@Composable
private fun TabGastosExcepcionales(
    gastos: List<GastoMensual>,
    onTogglePagado: (GastoMensual) -> Unit,
    onActualizarMonto: (Long, Double) -> Unit,
    onEliminar: (GastoMensual) -> Unit,
    onAgregar: (String, Double) -> Unit,
    surfaceDark: Color,
    borderDark: Color,
    accentBlue: Color,
    accentGreen: Color
) {
    var showAddDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        items(gastos) { gasto ->
            GastoRow(
                gasto = gasto,
                onTogglePagado = onTogglePagado,
                onActualizarMonto = onActualizarMonto,
                onEliminar = onEliminar,
                surfaceDark = surfaceDark,
                borderDark = borderDark,
                accentBlue = accentBlue,
                accentGreen = accentGreen
            )
        }

        if (gastos.isEmpty()) {
            item {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Sin gastos excepcionales", color = Color(0xFF333333), fontSize = 13.sp)
                }
            }
        }

        item {
            Spacer(Modifier.height(4.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(surfaceDark)
                    .border(0.5.dp, borderDark, RoundedCornerShape(10.dp))
                    .clickable { showAddDialog = true }
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(Icons.Outlined.Add, null, tint = accentBlue, modifier = Modifier.size(18.dp))
                Text(" Agregar gasto excepcional", color = accentBlue, fontSize = 13.sp)
            }
            Spacer(Modifier.height(16.dp))
        }
    }

    if (showAddDialog) {
        AgregarDialog(
            titulo = "Nuevo gasto excepcional",
            onConfirm = { nombre, monto ->
                onAgregar(nombre, monto)
                showAddDialog = false
            },
            onDismiss = { showAddDialog = false }
        )
    }
}

@Composable
private fun TabIngresos(
    ingresos: List<Ingreso>,
    onEliminar: (Ingreso) -> Unit,
    onAgregar: (String, Double) -> Unit,
    surfaceDark: Color,
    borderDark: Color,
    accentBlue: Color,
    accentGreen: Color
) {
    var showAddDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        items(ingresos) { ingreso ->
            IngresoRow(
                ingreso = ingreso,
                onEliminar = onEliminar,
                surfaceDark = surfaceDark,
                borderDark = borderDark,
                accentGreen = accentGreen
            )
        }

        if (ingresos.isEmpty()) {
            item {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Sin ingresos cargados", color = Color(0xFF333333), fontSize = 13.sp)
                }
            }
        }

        item {
            Spacer(Modifier.height(4.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(surfaceDark)
                    .border(0.5.dp, borderDark, RoundedCornerShape(10.dp))
                    .clickable { showAddDialog = true }
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(Icons.Outlined.Add, null, tint = accentBlue, modifier = Modifier.size(18.dp))
                Text(" Agregar ingreso", color = accentBlue, fontSize = 13.sp)
            }
            Spacer(Modifier.height(16.dp))
        }
    }

    if (showAddDialog) {
        AgregarDialog(
            titulo = "Nuevo ingreso",
            onConfirm = { nombre, monto ->
                onAgregar(nombre, monto)
                showAddDialog = false
            },
            onDismiss = { showAddDialog = false }
        )
    }
}

@Composable
private fun GastoRow(
    gasto: GastoMensual,
    onTogglePagado: (GastoMensual) -> Unit,
    onActualizarMonto: (Long, Double) -> Unit,
    onEliminar: (GastoMensual) -> Unit,
    surfaceDark: Color,
    borderDark: Color,
    accentBlue: Color,
    accentGreen: Color
) {
    var editandoMonto by remember { mutableStateOf(false) }
    var montoText by remember(gasto.monto) { mutableStateOf(String.format("%.0f", gasto.monto)) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(surfaceDark)
            .border(0.5.dp, borderDark, RoundedCornerShape(10.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                gasto.nombre,
                color = Color(0xFFCCCCCC),
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )
            if (editandoMonto) {
                BasicTextField(
                    value = montoText,
                    onValueChange = { montoText = it },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    textStyle = TextStyle(color = Color(0xFFCCCCCC), fontSize = 13.sp),
                    modifier = Modifier
                        .width(100.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFF2A2D35))
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    singleLine = true
                )
            } else {
                Text(
                    "$${String.format("%.2f", gasto.monto)}",
                    color = if (gasto.pagado) accentGreen else Color(0xFFCCCCCC),
                    fontSize = 13.sp
                )
            }
        }

        if (editandoMonto) {
            TextButton(onClick = {
                montoText.toDoubleOrNull()?.let { onActualizarMonto(gasto.id, it) }
                editandoMonto = false
            }) {
                Text("OK", color = accentBlue, fontSize = 12.sp)
            }
        } else {
            Text(
                "\u270e",
                color = Color(0xFF555555),
                fontSize = 14.sp,
                modifier = Modifier
                    .clickable { editandoMonto = true }
                    .padding(horizontal = 4.dp)
            )
        }

        Spacer(Modifier.width(4.dp))

        Switch(
            checked = gasto.pagado,
            onCheckedChange = { onTogglePagado(gasto) },
            colors = SwitchDefaults.colors(
                checkedThumbColor = accentGreen,
                uncheckedThumbColor = Color(0xFF555555),
                checkedTrackColor = accentGreen.copy(alpha = 0.3f),
                uncheckedTrackColor = Color(0xFF2A2D35)
            ),
            modifier = Modifier.padding(horizontal = 4.dp)
        )

        Text(
            "\u2715",
            color = Color(0xFF444444),
            fontSize = 16.sp,
            modifier = Modifier
                .clickable { onEliminar(gasto) }
                .padding(start = 8.dp)
        )
    }
}

@Composable
private fun IngresoRow(
    ingreso: Ingreso,
    onEliminar: (Ingreso) -> Unit,
    surfaceDark: Color,
    borderDark: Color,
    accentGreen: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(surfaceDark)
            .border(0.5.dp, borderDark, RoundedCornerShape(10.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            ingreso.nombre,
            color = Color(0xFFCCCCCC),
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f)
        )
        Text(
            "$${String.format("%.2f", ingreso.monto)}",
            color = accentGreen,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
        Spacer(Modifier.width(12.dp))
        Text(
            "\u2715",
            color = Color(0xFF444444),
            fontSize = 16.sp,
            modifier = Modifier.clickable { onEliminar(ingreso) }
        )
    }
}

@Composable
private fun AgregarDialog(
    titulo: String,
    onConfirm: (String, Double) -> Unit,
    onDismiss: () -> Unit
) {
    var nombre by remember { mutableStateOf("") }
    var monto by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF1A1D24),
        shape = RoundedCornerShape(14.dp),
        title = { Text(titulo, color = Color.White, fontSize = 15.sp) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Nombre", color = Color(0xFF666666), fontSize = 11.sp)
                    BasicTextField(
                        value = nombre,
                        onValueChange = { nombre = it },
                        textStyle = TextStyle(color = Color(0xFFCCCCCC), fontSize = 13.sp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF2A2D35))
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        decorationBox = { inner ->
                            if (nombre.isEmpty()) {
                                Text("Ej: Alquiler", color = Color(0xFF444444), fontSize = 13.sp)
                            }
                            inner()
                        }
                    )
                }
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Monto", color = Color(0xFF666666), fontSize = 11.sp)
                    BasicTextField(
                        value = monto,
                        onValueChange = { monto = it },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        textStyle = TextStyle(color = Color(0xFFCCCCCC), fontSize = 13.sp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF2A2D35))
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        decorationBox = { inner ->
                            if (monto.isEmpty()) {
                                Text("Ej: 50000", color = Color(0xFF444444), fontSize = 13.sp)
                            }
                            inner()
                        }
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val montoVal = monto.toDoubleOrNull() ?: 0.0
                    if (nombre.isNotBlank() && montoVal > 0) {
                        onConfirm(nombre, montoVal)
                    }
                }
            ) { Text("Agregar", color = Color(0xFF4A9EFF)) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", color = Color(0xFF555555))
            }
        }
    )
}