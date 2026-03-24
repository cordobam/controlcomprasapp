package com.example.controlcomprasapp.ui.screens.facturas

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.controlcomprasapp.util.FileUtils

import com.example.controlcomprasapp.viewmodel.FacturaViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
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

    val items = viewModel.items   // ← viene del ViewModel

    val descuentos = viewModel.descuentos

    val cameraLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            // nada extra
        }

    val galeriaLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.OpenDocument()
        ) { uri ->
            uri?.let {
                context.contentResolver.takePersistableUriPermission(
                    it,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
                imagenUriString = it.toString()
            }
        }

    val permissionLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { granted ->
            if (granted) {
                val uri = FileUtils.crearArchivoImagen(context)
                imagenUriString = uri.toString()
                cameraLauncher.launch(uri)
            }
        }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        // 🔹 PREVIEW
        Box(
            modifier = Modifier
                .height(200.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Color.LightGray.copy(alpha = 0.2f)),
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
                        contentScale = ContentScale.Crop
                    )
                }
            } ?: Text("Sin archivo seleccionado")
        }

        // 🔹 BOTONES
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = {
                        permissionLauncher.launch(Manifest.permission.CAMERA)
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp)
                ) {
                    Text("📸 Foto")
                }

                Button(
                    onClick = {
                        galeriaLauncher.launch(arrayOf("image/*", "application/pdf"))
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp)
                ) {
                    Text("📁 Galería")
                }
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = {
                        imagenUri?.let { viewModel.procesarUri(context, it) }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp)
                ) {
                    Text("🔍 OCR")
                }

                Button(
                    onClick = {
                        val guardado = viewModel.guardar()
                        if (!guardado) {
                            Toast.makeText(context, "Este ticket ya fue cargado", Toast.LENGTH_LONG).show()
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp)
                ) {
                    Text("💾 Guardar")
                }
            }
        }

        // 🔹 INFO
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Color.LightGray.copy(alpha = 0.1f))
                .padding(12.dp)
        ) {
            Text("Local: ${viewModel.local}", fontWeight = FontWeight.Bold)
            Text("Fecha: ${viewModel.fecha ?: "No detectada"}")
            Text("Archivo: ${viewModel.archivo}")
        }

        TabsItemsDescuentos(
            items = items,
            descuentos = descuentos,
            modifier = Modifier.weight(1f)
        )
    }
}