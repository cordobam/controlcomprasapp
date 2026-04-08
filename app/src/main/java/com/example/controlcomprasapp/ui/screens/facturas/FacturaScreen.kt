package com.example.controlcomprasapp.ui.screens.facturas

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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
    val scrollState = rememberScrollState()

    var imagenUriString by rememberSaveable { mutableStateOf<String?>(null) }
    val imagenUri = imagenUriString?.let { Uri.parse(it) }
    val items = viewModel.items
    val descuentos = viewModel.descuentos

    val backgroundDark = Color(0xFF111318)
    val surfaceDark = Color(0xFF1A1D24)
    val borderDark = Color(0xFF2A2D35)
    val accentBlue = Color(0xFF4A9EFF)

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

    val hayDatos = viewModel.local.isNotBlank() || viewModel.fecha != null

    // ── CONTENEDOR PRINCIPAL ──
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundDark)
    ) {
        // ── PARTE SUPERIOR (55% de la pantalla) ──
        // Usamos weight(0.55f) para garantizar que no use TODA la pantalla
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.55f)
                .verticalScroll(scrollState)
                .padding(horizontal = 16.dp)
                .padding(top = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            // PREVIEW
            Box(
                modifier = Modifier
                    .height(180.dp)
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
                            contentScale = ContentScale.Fit // Fit es mejor para tickets largos
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

            // BOTONES
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    ActionButton("Cámara", "Foto nueva", Icons.Outlined.CameraAlt, Modifier.weight(1f), true) {
                        permissionLauncher.launch(Manifest.permission.CAMERA)
                    }
                    ActionButton("Galería", "Archivo", Icons.Outlined.Photo, Modifier.weight(1f)) {
                        galeriaLauncher.launch(arrayOf("image/*", "application/pdf"))
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    ActionButton("Escanear", "Leer OCR", Icons.Outlined.Search, Modifier.weight(1f)) {
                        imagenUri?.let { viewModel.procesarUri(context, it) }
                    }
                    ActionButton("Guardar", "Listo", Icons.Outlined.Save, Modifier.weight(1f), false, hayDatos) {
                        viewModel.guardar()
                    }
                }
            }

            // META INFO
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(surfaceDark)
                    .border(0.5.dp, borderDark, RoundedCornerShape(12.dp))
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                MetaRow("Local", viewModel.local.ifBlank { "—" })
                HorizontalDivider(color = Color(0xFF222222), thickness = 0.5.dp)
                MetaRow("Fecha", viewModel.fecha ?: "—")
                HorizontalDivider(color = Color(0xFF222222), thickness = 0.5.dp)
                MetaRow("Archivo", viewModel.archivo.ifBlank { "—" })
            }
        }

        // ── PARTE INFERIOR (45% de la pantalla) ──
        // Al darle un weight(0.45f), obligamos a que esta sección SIEMPRE se vea
        TabsItemsDescuentos(
            listaItems = items,
            descuentos = descuentos,
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.45f)
                .padding(horizontal = 16.dp)
                .padding(top = 12.dp, bottom = 8.dp)
        )
    }
}

@Composable
private fun ActionButton(
    label: String,
    sub: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    isPrimary: Boolean = false,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    val bgColor = if (isPrimary) Color(0xFF1C2A3A) else Color(0xFF1A1D24)
    val borderColor = if (isPrimary) Color(0xFF1F4068) else Color(0xFF2A2D35)
    val iconTint = if (isPrimary) Color(0xFF4A9EFF) else Color(0xFF888888)

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor.copy(alpha = if (enabled) 1f else 0.4f))
            .border(0.5.dp, borderColor, RoundedCornerShape(12.dp))
            .clickable(enabled = enabled) { onClick() }
            .padding(10.dp)
    ) {
        Column {
            Icon(icon, null, tint = iconTint, modifier = Modifier.size(18.dp))
            Text(label, color = if (isPrimary) Color(0xFF4A9EFF) else Color.White, fontSize = 13.sp, fontWeight = FontWeight.Medium)
            Text(sub, color = Color.Gray, fontSize = 10.sp)
        }
    }
}

@Composable
private fun MetaRow(key: String, value: String) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(key, color = Color.Gray, fontSize = 12.sp)
        Text(value, color = Color.LightGray, fontSize = 12.sp, fontStyle = FontStyle.Italic)
    }
}