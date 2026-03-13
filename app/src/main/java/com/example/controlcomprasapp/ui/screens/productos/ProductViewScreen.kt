package com.example.controlcomprasapp.ui.screens.productos


import android.app.DatePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

@Composable
fun ProductosScreen(viewModel: ProductViewModel) {
    var tipoVista by remember { mutableStateOf(TipoVista.ITEMS) }

    val productos = viewModel.items

    var fechaDesde by remember { mutableStateOf("") }
    var fechaHasta by remember { mutableStateOf("") }
    var localSeleccionado by remember { mutableStateOf<Locales?>(null) }

    val locales = listOf(
        Locales(1, "Carrefour"),
        Locales(2, "Disco"),
        Locales(3, "Vea")
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        DatePickerField(
            label = "Fecha desde",
            value = fechaDesde
        ) {
            fechaDesde = it
        }

        Spacer(Modifier.height(8.dp))

        DatePickerField(
            label = "Fecha hasta",
            value = fechaHasta
        ) {
            fechaHasta = it
        }

        Spacer(Modifier.height(8.dp))

        LocalDropdown(
            locales = locales,
            selected = localSeleccionado
        ) {
            localSeleccionado = it
        }

        Spacer(Modifier.height(8.dp))

        Row {

            Button(onClick = { tipoVista = TipoVista.ITEMS }) {
                Text("Items")
            }

            Spacer(Modifier.width(8.dp))

            Button(onClick = { tipoVista = TipoVista.DESCUENTOS }) {
                Text("Descuentos")
            }
        }

        Spacer(Modifier.height(8.dp))

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

            }
        ) {
            Text("Aplicar filtros")
        }

        Spacer(Modifier.height(16.dp))

        LazyColumn {

            items(productos) { product ->

                Text("${product.nombre} - ${product.total}")

            }
        }
    }
}

@Composable
fun DatePickerField(
    label: String,
    value: String,
    onDateSelected: (String) -> Unit
) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            // Formato ISO para Base de Datos: YYYY-MM-DD
            val fechaDB = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth)
            onDateSelected(fechaDB)
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    Box(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = value,
            onValueChange = {},
            label = { Text(label) },
            readOnly = true,
            modifier = Modifier.fillMaxWidth()
        )
        Box(
            modifier = Modifier
                .matchParentSize()
                .clickable { datePickerDialog.show() }
        )
    }
}

@Composable
fun LocalDropdown(
    locales: List<Locales>,
    selected: Locales?,
    onSelected: (Locales) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = selected?.nombre ?: "",
            onValueChange = {},
            label = { Text("Local") },
            readOnly = true,
            modifier = Modifier.fillMaxWidth()
        )
        // Capa para abrir el menú
        Box(
            modifier = Modifier
                .matchParentSize()
                .clickable { expanded = true }
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth(0.9f) // Ajusta el ancho si es necesario
        ) {
            locales.forEach { local ->
                DropdownMenuItem(
                    text = { Text(local.nombre) },
                    onClick = {
                        onSelected(local)
                        expanded = false
                    }
                )
            }
        }
    }
}