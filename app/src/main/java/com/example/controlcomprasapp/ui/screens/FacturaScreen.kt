package com.example.controlcomprasapp.ui.screens

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import coil.compose.AsyncImage
import com.example.controlcomprasapp.util.PdfUtils
import com.example.controlcomprasapp.util.FileUtils

import com.example.controlcomprasapp.viewmodel.FacturaViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.controlcomprasapp.ui.components.PdfPreview
import com.example.controlcomprasapp.viewmodel.FacturaViewModelFactory

@Composable
fun FacturaScreen(
    factory: FacturaViewModelFactory
) {
    val viewModel: FacturaViewModel = viewModel(factory = factory)

    val context = LocalContext.current

    var imagenUriString by rememberSaveable { mutableStateOf<String?>(null) }
    val imagenUri = imagenUriString?.let { Uri.parse(it) }

    val items = viewModel.items   // ← viene del ViewModel

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
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        Button(onClick = {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }) {
            Text("Sacar Foto")
        }

        Button(onClick = {
            galeriaLauncher.launch(arrayOf("image/*", "application/pdf"))
        }) {
            Text("Elegir de Galería")
        }

        Button(onClick = {
            imagenUri?.let { viewModel.procesarUri(context, it) }
        }) {
            Text("Leer OCR")
        }

        Button(onClick = {
            viewModel.guardar()
        }) {
            Text("Guardar en DB")
        }

        Box(
            modifier = Modifier
                .height(200.dp)
                .fillMaxWidth()
        ) {
            imagenUri?.let { uri ->
                val mime = context.contentResolver.getType(uri)

                if (mime?.contains("pdf") == true) {
                    PdfPreview(uri)
                } else {
                    AsyncImage(model = uri, contentDescription = null)
                }
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            items(items) { item ->
                Column(Modifier.padding(8.dp)) {
                    Text(item.nombre, fontWeight = FontWeight.Bold)
                    Text(
                        "Cant: ${item.cantidad} | Unit: ${item.precioUnitario} | Total: ${item.total}"
                    )
                    HorizontalDivider()
                }
            }
        }
    }
}