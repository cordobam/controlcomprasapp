package com.example.controlcomprasapp.ui.screens.facturas

import android.inputmethodservice.Keyboard
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.controlcomprasapp.viewmodel.FacturaViewModel
import com.example.controlcomprasapp.viewmodel.FacturaViewModelFactory

@Composable
fun FacturaManualScreen(
    factory: FacturaViewModelFactory,
    navController: NavController
) {
    val viewModel: FacturaViewModel = viewModel(factory = factory)
    val backgroundDark = Color(0xFF111318)
    val surfaceDark = Color(0xFF1A1D24)
    val borderDark = Color(0xFF2A2D35)
    val accentBlue = Color(0xFF4A9EFF)

    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Datos", "Items (${viewModel.items.size})", "Descuentos")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundDark)
    ) {
        // ── Header ───────────────────────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(surfaceDark)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "← Volver",
                color = Color(0xFF555555),
                fontSize = 13.sp,
                modifier = Modifier.clickable { navController.popBackStack() }
            )
            Text(
                "Carga manual",
                color = Color.White,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center
            )
            Text(
                "borrador",
                color = Color(0xFF333333),
                fontSize = 11.sp
            )
        }

        // ── Tabs ─────────────────────────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(surfaceDark)
                .border(BorderStroke(0.5.dp, borderDark))
        ) {
            tabs.forEachIndexed { index, label ->
                val isActive = selectedTab == index
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { selectedTab = index }
                        .padding(vertical = 10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        label,
                        color = if (isActive) accentBlue else Color(0xFF555555),
                        fontSize = 12.sp,
                        fontWeight = if (isActive) FontWeight.Medium else FontWeight.Normal
                    )
                    if (isActive) {
                        Spacer(Modifier.height(4.dp))
                        Box(
                            Modifier
                                .width(24.dp)
                                .height(1.5.dp)
                                .background(accentBlue, RoundedCornerShape(1.dp))
                        )
                    }
                }
            }
        }

        // ── Contenido del tab ─────────────────────────────────────────────────
        Box(modifier = Modifier.weight(1f)) {
            when (selectedTab) {
                0 -> TabDatos(viewModel, surfaceDark, borderDark)
                1 -> TabItems(viewModel, surfaceDark, borderDark, accentBlue)
                2 -> TabDescuentos(viewModel, surfaceDark, borderDark, accentBlue)
            }
        }

        // ── Bottom bar ────────────────────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(surfaceDark)
                .border(BorderStroke(0.5.dp, borderDark))
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            OutlinedButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier.weight(1f),
                border = BorderStroke(0.5.dp, borderDark),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF666666))
            ) { Text("Descartar", fontSize = 13.sp) }

            Button(
                onClick = {
                    viewModel.guardar()
                    navController.popBackStack()
                },
                modifier = Modifier.weight(2f),
                colors = ButtonDefaults.buttonColors(containerColor = accentBlue),
                enabled = viewModel.local.isNotBlank()
            ) { Text("Guardar factura", color = Color.Black, fontSize = 13.sp) }
        }
    }
}


// ── Tab Datos ─────────────────────────────────────────────────────────────────

@Composable
private fun TabDatos(
    viewModel: FacturaViewModel,
    surfaceDark: Color,
    borderDark: Color
) {
    val borderColor = Color(0xFF2A2D35)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        SectionLabel("Información del local")

        ManualField(
            label = "Local / comercio",
            value = viewModel.local,
            onValueChange = { viewModel.local = it },
            placeholder = "Ej: Café Central"
        )
        ManualField(
            label = "Fecha",
            value = viewModel.fecha ?: "",
            onValueChange = { viewModel.fecha = it },
            placeholder = "dd/mm/aaaa"
        )
    }
}

// ── Tab Items ─────────────────────────────────────────────────────────────────

@Composable
private fun TabItems(
    viewModel: FacturaViewModel,
    surfaceDark: Color,
    borderDark: Color,
    accentBlue: Color
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var nombreItem by remember { mutableStateOf("") }
    var precioItem by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize()) {
        // Lista de items
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(viewModel.items) { item ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(item.nombre, color = Color(0xFFCCCCCC), fontSize = 13.sp, modifier = Modifier.weight(1f))
                    Text("$${item.precioUnitario}", color = accentBlue, fontSize = 13.sp)
                    Spacer(Modifier.width(12.dp))
                    Text(
                        "×",
                        color = Color(0xFF444444),
                        fontSize = 16.sp,
                        modifier = Modifier.clickable { viewModel.eliminarItem(item) }
                    )
                }
                HorizontalDivider(color = Color(0xFF1A1A1A))
            }

            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showAddDialog = true }
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(22.dp)
                            .clip(CircleShape)
                            .border(0.5.dp, Color(0xFF2A2D35), CircleShape),
                        contentAlignment = Alignment.Center
                    ) { Text("+", color = Color(0xFF555555), fontSize = 14.sp) }
                    Text("Agregar item", color = Color(0xFF555555), fontSize = 13.sp)
                }
            }
        }

        // Total
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF1A1D24))
                .border(BorderStroke(0.5.dp, Color(0xFF2A2D35)))
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Total", color = Color(0xFF666666), fontSize = 13.sp)
            Text(
                "$${viewModel.items.sumOf { it.precioUnitario }}",
                color = accentBlue,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }

    // Dialog agregar item
    if (showAddDialog) {
        var nombreItem by remember { mutableStateOf("") }
        var precioUnitario by remember { mutableStateOf("") }
        var cantidad by remember { mutableStateOf("") }
        var seccion by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            containerColor = Color(0xFF1A1D24),
            title = { Text("Agregar item", color = Color.White, fontSize = 15.sp) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    ManualField("Descripción", nombreItem, { nombreItem = it }, "Ej: Pollo")
                    ManualField("Precio Unitario", precioUnitario, { precioUnitario = it }, "Ej: 200", isNumber = true)
                    ManualField("Cantidad", cantidad, { cantidad = it }, "Ej: 1", isNumber = true)
                    ManualField("Seccion", seccion, { seccion = it }, "Ej: Carniceria", isNumber = true)
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    val precioUnitario = precioUnitario.toDoubleOrNull() ?: 0.0
                    if (nombreItem.isNotBlank() && precioUnitario > 0) {
                        viewModel.agregarItem(
                            nombre = nombreItem,
                            cantidad=cantidad.toIntOrNull() ?: 1,
                            precioUnitario  = precioUnitario ,
                            seccion=seccion
                        )
                        showAddDialog = false
                    }
                }) { Text("Agregar", color = Color(0xFF4A9EFF)) }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("Cancelar", color = Color(0xFF555555))
                }
            }
        )
    }
}

// ── Tab Descuentos ─────────────────────────────────────────────────────────────

@Composable
private fun TabDescuentos(
    viewModel: FacturaViewModel,
    surfaceDark: Color,
    borderDark: Color,
    accentBlue: Color
) {
    var showAddDialog by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize()) {

        // ── Lista de descuentos ───────────────────────────────────────────────
        LazyColumn(modifier = Modifier.weight(1f)) {

            if (viewModel.descuentos.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "Sin descuentos cargados",
                            color = Color(0xFF333333),
                            fontSize = 13.sp
                        )
                    }
                }
            }

            items(viewModel.descuentos) { descuento ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        descuento.nombre,
                        color = Color(0xFFCCCCCC),
                        fontSize = 13.sp,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        "-$${descuento.total}",
                        color = Color(0xFF4ADE80), // verde para descuentos
                        fontSize = 13.sp
                    )
                    Spacer(Modifier.width(12.dp))
                    Text(
                        "×",
                        color = Color(0xFF444444),
                        fontSize = 16.sp,
                        modifier = Modifier.clickable {
                            viewModel.eliminarDescuento(descuento)
                        }
                    )
                }
                HorizontalDivider(color = Color(0xFF1A1A1A))
            }

            // ── Botón agregar ─────────────────────────────────────────────────
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showAddDialog = true }
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(22.dp)
                            .clip(CircleShape)
                            .border(0.5.dp, Color(0xFF2A2D35), CircleShape),
                        contentAlignment = Alignment.Center
                    ) { Text("+", color = Color(0xFF555555), fontSize = 14.sp) }
                    Text("Agregar descuento", color = Color(0xFF555555), fontSize = 13.sp)
                }
            }
        }

        // ── Total ahorrado ────────────────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF1A1D24))
                .border(BorderStroke(0.5.dp, Color(0xFF2A2D35)))
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Total descuentos", color = Color(0xFF666666), fontSize = 13.sp)
            Text(
                "-$${viewModel.descuentos.sumOf { it.total }}",
                color = Color(0xFF4ADE80),
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }

    // ── Dialog agregar descuento ──────────────────────────────────────────────
    if (showAddDialog) {
        var nombreDesc by remember { mutableStateOf("") }
        var totalDesc by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            containerColor = Color(0xFF1A1D24),
            shape = RoundedCornerShape(14.dp),
            title = { Text("Agregar descuento", color = Color.White, fontSize = 15.sp) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    ManualField(
                        label = "Descripción",
                        value = nombreDesc,
                        onValueChange = { nombreDesc = it },
                        placeholder = "Ej: Descuento socio"
                    )
                    ManualField(
                        label = "Monto",
                        value = totalDesc,
                        onValueChange = { totalDesc = it },
                        placeholder = "Ej: 200",
                        isNumber = true
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val total = totalDesc.toDoubleOrNull() ?: 0.0
                        if (nombreDesc.isNotBlank() && total > 0) {
                            viewModel.agregarDescuento(
                                nombre = nombreDesc,
                                total = total
                            )
                            nombreDesc = ""
                            totalDesc = ""
                            showAddDialog = false
                        }
                    }
                ) { Text("Agregar", color = Color(0xFF4A9EFF)) }
            },
            dismissButton = {
                TextButton(onClick = {
                    nombreDesc = ""
                    totalDesc = ""
                    showAddDialog = false
                }) { Text("Cancelar", color = Color(0xFF555555)) }
            }
        )
    }
}

// ── Componentes compartidos ───────────────────────────────────────────────────

@Composable
private fun SectionLabel(text: String) {
    Text(
        text.uppercase(),
        color = Color(0xFF444444),
        fontSize = 10.sp,
        letterSpacing = 0.6.sp
    )
}

@Composable
private fun ManualField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    isNumber: Boolean = false
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(label, color = Color(0xFF666666), fontSize = 11.sp)
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            keyboardOptions = if (isNumber)
                KeyboardOptions(keyboardType = KeyboardType.Decimal)
            else
                KeyboardOptions.Default,
            textStyle = TextStyle(color = Color(0xFFCCCCCC), fontSize = 13.sp),
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFF1A1D24))
                .border(0.5.dp, Color(0xFF2A2D35), RoundedCornerShape(8.dp))
                .padding(horizontal = 12.dp, vertical = 10.dp),
            decorationBox = { inner ->
                if (value.isEmpty()) {
                    Text(placeholder, color = Color(0xFF333333), fontSize = 13.sp)
                }
                inner()
            }
        )
    }
}