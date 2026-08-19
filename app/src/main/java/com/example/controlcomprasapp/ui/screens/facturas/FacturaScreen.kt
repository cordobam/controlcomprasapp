package com.example.controlcomprasapp.ui.screens.facturas

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.LocalOffer
import androidx.compose.material.icons.outlined.Photo
import androidx.compose.material.icons.outlined.Save
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.controlcomprasapp.util.FileUtils

import com.example.controlcomprasapp.viewmodel.FacturaViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.controlcomprasapp.domain.model.Descuentos
import com.example.controlcomprasapp.domain.model.ItemTicket
import com.example.controlcomprasapp.ui.components.PdfPreview
import com.example.controlcomprasapp.ui.components.TabsItemsDescuentos
import com.example.controlcomprasapp.viewmodel.FacturaViewModelFactory

@Composable
fun FacturaScreen(
    factory: FacturaViewModelFactory,
    navController: NavController
) {
    val viewModel: FacturaViewModel = viewModel(factory = factory)
    val context = LocalContext.current

    var imagenUriString by rememberSaveable { mutableStateOf<String?>(null) }
    val imagenUri = imagenUriString?.let { Uri.parse(it) }
    val items = viewModel.items
    val descuentos = viewModel.descuentos
    val isEscaneando = viewModel.isEscaneando

    var editandoLocal by rememberSaveable { mutableStateOf(false) }
    var localTemp by rememberSaveable { mutableStateOf("") }

    val backgroundDark = Color(0xFF111318)
    val surfaceDark = Color(0xFF1A1D24)
    val borderDark = Color(0xFF2A2D35)
    val accentBlue = Color(0xFF4A9EFF)

    val hayDatos = viewModel.local.isNotBlank() || viewModel.fecha != null

    // Muestra mensajes de resultado/error del escaneo
    LaunchedEffect(viewModel.mensaje) {
        viewModel.mensaje?.let { Toast.makeText(context, it, Toast.LENGTH_LONG).show() }
    }

    // Launchers
    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) {}
    val galeriaLauncher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        uri?.let {
            context.contentResolver.takePersistableUriPermission(it, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            imagenUriString = it.toString()
        }
    }
    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) {
            val uri = FileUtils.crearArchivoImagen(context)
            imagenUriString = uri.toString()
            cameraLauncher.launch(uri)
        }
    }

    // ── CONTENEDOR PRINCIPAL ──────────────────────────────────────────────────
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundDark)
    ) {

        // ── PARTE SUPERIOR — preview + íconos + meta ──────────────────────────
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.52f)
                .padding(horizontal = 16.dp)
                .padding(top = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {

            // PREVIEW
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(surfaceDark)
                    .border(1.5.dp, borderDark, RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                imagenUri?.let { uri ->
                    val mime = context.contentResolver.getType(uri)
                    if (mime?.contains("pdf") == true) {
                        PdfPreview(uri)
                    } else {
                        AsyncImage(
                            model = uri,
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Fit
                        )
                    }
                } ?: Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Image,
                        contentDescription = null,
                        tint = Color(0xFF555555),
                        modifier = Modifier.size(32.dp)
                    )
                    Text("Sin imagen", color = Color(0xFF555555), fontSize = 13.sp)
                }
            }

            // ── BOTONES — una sola fila de íconos ────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconActionButton(
                    icon = Icons.Outlined.CameraAlt,
                    label = "Cámara",
                    isPrimary = true,
                    modifier = Modifier.weight(1f)
                ) { permissionLauncher.launch(Manifest.permission.CAMERA) }

                IconActionButton(
                    icon = Icons.Outlined.Photo,
                    label = "Galería",
                    modifier = Modifier.weight(1f)
                ) { galeriaLauncher.launch(arrayOf("image/*", "application/pdf")) }

                IconActionButton(
                    icon = Icons.Outlined.Search,
                    label = "Escanear",
                    modifier = Modifier.weight(1f),
                    enabled = imagenUri != null && !isEscaneando,
                    loading = isEscaneando
                ) { imagenUri?.let { viewModel.procesarUri(context, it) } }
            }

            // ── META INFO ─────────────────────────────────────────────────────
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(surfaceDark)
                    .border(0.5.dp, borderDark, RoundedCornerShape(12.dp))
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            localTemp = viewModel.local
                            editandoLocal = true
                        }
                        .padding(vertical = 2.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Local", color = Color.Gray, fontSize = 12.sp)
                    Text(
                        viewModel.local.ifBlank { "—" },
                        color = accentBlue,
                        fontSize = 12.sp,
                        fontStyle = FontStyle.Italic
                    )
                }
                HorizontalDivider(color = Color(0xFF222222), thickness = 0.5.dp)
                MetaRow("Fecha", viewModel.fecha ?: "—")
                HorizontalDivider(color = Color(0xFF222222), thickness = 0.5.dp)
                MetaRow("Archivo", viewModel.archivo.ifBlank { "—" })
                if (viewModel.tipo == "TARJETA") {
                    HorizontalDivider(color = Color(0xFF222222), thickness = 0.5.dp)
                    MetaRow("Banco", viewModel.banco ?: "—")
                    HorizontalDivider(color = Color(0xFF222222), thickness = 0.5.dp)
                    MetaRow("Marca", viewModel.marca ?: "—")
                    HorizontalDivider(color = Color(0xFF222222), thickness = 0.5.dp)
                    MetaRow("Vencimiento", viewModel.fechaVencimiento ?: "—")
                }
            }
        }

        // ── PARTE INFERIOR — lista siempre visible ────────────────────────────
        TabsItemsDescuentos(
            listaItems = items,
            descuentos = descuentos,
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.48f)
                .padding(horizontal = 16.dp)
                .padding(top = 8.dp)
        )

        // ── BOTTOM BAR — Guardar siempre visible, sin scroll ─────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(surfaceDark)
                .border(BorderStroke(0.5.dp, borderDark))
                .padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Total a la izquierda
            Column(modifier = Modifier.weight(1f)) {
                Text("Total", color = Color(0xFF555555), fontSize = 11.sp)
                Text(
                    "$${items.sumOf { it.total }}",
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            // Botón guardar a la derecha
            Button(
                onClick = {
                    val ok = viewModel.guardar()
                    if (ok) imagenUriString = null
                },
                enabled = hayDatos,
                colors = ButtonDefaults.buttonColors(
                    containerColor = accentBlue,
                    disabledContainerColor = Color(0xFF1C2A3A)
                ),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.height(42.dp)
            ) {
                Icon(
                    Icons.Outlined.Save,
                    contentDescription = null,
                    tint = if (hayDatos) Color.Black else Color(0xFF2A4A6A),
                    modifier = Modifier.size(16.dp)
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    "Guardar",
                    color = if (hayDatos) Color.Black else Color(0xFF2A4A6A),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }

    if (editandoLocal) {
        AlertDialog(
            onDismissRequest = { editandoLocal = false },
            containerColor = surfaceDark,
            title = { Text("Editar local", color = Color.White) },
            text = {
                OutlinedTextField(
                    value = localTemp,
                    onValueChange = { localTemp = it },
                    label = { Text("Nombre del local", color = Color.Gray) },
                    singleLine = true
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.local = localTemp.trim()
                        editandoLocal = false
                    }
                ) { Text("Guardar", color = accentBlue) }
            },
            dismissButton = {
                TextButton(onClick = { editandoLocal = false }) { Text("Cancelar", color = Color.Gray) }
            }
        )
    }
}

// ── Botón ícono compacto ──────────────────────────────────────────────────────

@Composable
private fun IconActionButton(
    icon: ImageVector,
    label: String,
    modifier: Modifier = Modifier,
    isPrimary: Boolean = false,
    enabled: Boolean = true,
    loading: Boolean = false,
    onClick: () -> Unit
) {
    val bgColor = if (isPrimary) Color(0xFF1C2A3A) else Color(0xFF1A1D24)
    val borderColor = if (isPrimary) Color(0xFF1F4068) else Color(0xFF2A2D35)
    val iconTint = when {
        !enabled -> Color(0xFF333333)
        isPrimary -> Color(0xFF4A9EFF)
        else -> Color(0xFF888888)
    }
    val labelColor = when {
        !enabled -> Color(0xFF333333)
        isPrimary -> Color(0xFF4A9EFF)
        else -> Color(0xFFCCCCCC)
    }

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(bgColor.copy(alpha = if (enabled) 1f else 0.5f))
            .border(0.5.dp, borderColor, RoundedCornerShape(10.dp))
            .clickable(enabled = enabled) { onClick() }
            .padding(vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        if (loading) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                color = Color(0xFF4A9EFF),
                strokeWidth = 2.dp
            )
        } else {
            Icon(icon, contentDescription = label, tint = iconTint, modifier = Modifier.size(20.dp))
        }
        Text(label, fontSize = 10.sp, color = labelColor)
    }
}

// ── MetaRow sin cambios ───────────────────────────────────────────────────────

@Composable
private fun MetaRow(key: String, value: String) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(key, color = Color.Gray, fontSize = 12.sp)
        Text(value, color = Color.LightGray, fontSize = 12.sp, fontStyle = FontStyle.Italic)
    }
}