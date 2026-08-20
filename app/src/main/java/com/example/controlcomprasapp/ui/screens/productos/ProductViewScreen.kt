package com.example.controlcomprasapp.ui.screens.productos


import android.app.DatePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.LocalOffer
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.controlcomprasapp.data.local.datasource.ProductFilter
import com.example.controlcomprasapp.domain.model.Locales
import com.example.controlcomprasapp.viewmodel.ProductViewModel
import java.util.Calendar
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun ProductosScreen(viewModel: ProductViewModel) {
    var tipoVista by remember { mutableStateOf(TipoVista.ITEMS) }
    val productos = viewModel.items

    var fechaDesde by remember { mutableStateOf("") }
    var fechaHasta by remember { mutableStateOf("") }
    var localSeleccionado by remember { mutableStateOf<Locales?>(null) }

    val locales by viewModel.locales.collectAsStateWithLifecycle()

    val backgroundDark = Color(0xFF111318)
    val surfaceDark = Color(0xFF1A1D24)
    val borderDark = Color(0xFF2A2D35)
    val accentBlue = Color(0xFF4A9EFF)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundDark)
    ) {

        // ── FILTROS ───────────────────────────────────────────
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {

            // ── FECHAS ───────────────────────────────────────
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                DatePickerFieldDark(
                    label = "Desde",
                    value = fechaDesde,
                    modifier = Modifier.weight(1f)
                ) { fechaDesde = it }

                DatePickerFieldDark(
                    label = "Hasta",
                    value = fechaHasta,
                    modifier = Modifier.weight(1f)
                ) { fechaHasta = it }
            }

            // ── LOCAL ─────────────────────────────────────────
            LocalDropdownDark(
                locales = locales,
                selected = localSeleccionado,
                onSelected = { localSeleccionado = it }
            )

            // ── TIPO VISTA ────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(surfaceDark)
                    .border(0.5.dp, borderDark, RoundedCornerShape(12.dp))
                    .padding(4.dp)
            ) {
                listOf(TipoVista.ITEMS to "Ítems", TipoVista.DESCUENTOS to "Descuentos").forEach { (tipo, label) ->
                    val selected = tipoVista == tipo
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (selected) Color(0xFF1C2A3A) else Color.Transparent)
                            .clickable { tipoVista = tipo }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            label,
                            color = if (selected) accentBlue else Color(0xFF888888),
                            fontSize = 13.sp,
                            fontWeight = if (selected) FontWeight.Medium else FontWeight.Normal
                        )
                    }
                }
            }

            // ── BOTÓN APLICAR ─────────────────────────────────
            Button(
                onClick = {
                    val filter = ProductFilter(
                        fechaDesde = fechaDesde,
                        fechaHasta = fechaHasta,
                        localId = localSeleccionado?.id
                    )
                    if (tipoVista == TipoVista.ITEMS) {
                        viewModel.loadItems(filter)
                    } else {
                        viewModel.loadDescuentos(filter)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF1A3A5C),
                    contentColor = accentBlue
                )
            ) {
                Icon(
                    Icons.Outlined.Search,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text("Aplicar filtros", fontWeight = FontWeight.Medium, fontSize = 14.sp)
            }
        }

        HorizontalDivider(color = Color(0xFF1E2028), thickness = 0.5.dp)

        // ── LISTA ─────────────────────────────────────────────
        if (productos.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = if (tipoVista == TipoVista.ITEMS)
                            Icons.Outlined.ShoppingCart else Icons.Outlined.LocalOffer,
                        contentDescription = null,
                        tint = Color(0xFF333333),
                        modifier = Modifier.size(36.dp)
                    )
                    Text(
                        "Aplicá los filtros para ver resultados",
                        color = Color.White,
                        fontSize = 13.sp
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
                contentPadding = PaddingValues(vertical = 12.dp)
            ) {
                items(productos) { product ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFF1A1D24))
                            .border(0.5.dp, Color(0xFF2A2D35), RoundedCornerShape(10.dp))
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            product.nombre,
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.weight(1f),
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(Modifier.width(12.dp))
                        Text(
                            "$${product.total}",
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

// ── DATE PICKER DARK ──────────────────────────────────────────

@Composable
fun DatePickerFieldDark(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    onDateSelected: (String) -> Unit
) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            val fechaDB = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth)
            onDateSelected(fechaDB)
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0xFF1A1D24))
            .border(0.5.dp, Color(0xFF2A2D35), RoundedCornerShape(10.dp))
            .clickable { datePickerDialog.show() }
            .padding(horizontal = 12.dp, vertical = 10.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(label, color = Color.White, fontSize = 11.sp)
            Text(
                value.ifBlank { "—" },
                color = Color.White,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

// ── LOCAL DROPDOWN DARK ───────────────────────────────────────

@Composable
fun LocalDropdownDark(
    locales: List<Locales>,
    selected: Locales?,
    onSelected: (Locales) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(Color(0xFF1A1D24))
                .border(0.5.dp, Color(0xFF2A2D35), RoundedCornerShape(10.dp))
                .clickable { expanded = true }
                .padding(horizontal = 12.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text("Local", color = Color.White, fontSize = 11.sp)
                Text(
                    selected?.nombre ?: "Todos",
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
            }
            Icon(
                Icons.Outlined.KeyboardArrowDown,
                contentDescription = null,
                tint = Color(0xFF555555),
                modifier = Modifier.size(18.dp)
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .background(Color(0xFF1A1D24))
        ) {
            locales.forEach { local ->
                DropdownMenuItem(
                    text = {
                        Text(
                            local.nombre,
                            color = Color.White,
                            fontSize = 13.sp
                        )
                    },
                    onClick = {
                        onSelected(local)
                        expanded = false
                    }
                )
            }
        }
    }
}