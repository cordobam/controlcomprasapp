package com.example.controlcomprasapp

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.controlcomprasapp.ui.screens.FacturaScreen
import com.example.controlcomprasapp.ui.theme.ControlcomprasappTheme
import androidx.compose.ui.platform.LocalContext
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import coil.compose.AsyncImage
import java.io.File
import android.Manifest
import android.widget.Toast


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ControlcomprasappTheme {
                FacturaScreenMetodo()
            }
        }
    }
}

@Composable
fun FacturaScreenMetodo() {

    val context = LocalContext.current
    var imagenurl by remember { mutableStateOf<Uri?>(null) }

    val cameraLuncher = rememberLauncherForActivityResult(contract = ActivityResultContracts.TakePicture())
    {
        succes -> if (succes) {

        }
    }

    val galeriaLuncher = rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent())
    {
        uri -> imagenurl = uri
    }

    val permissionLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { granted ->
            if (granted) {
                val uri = crearArchivoImagen(context)
                imagenurl = uri
                cameraLuncher.launch(uri)
            } else {
                Toast.makeText(
                    context,
                    "Permiso de cámara requerido",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp))
    {
        Button( onClick = {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        })
        {
            Text("Sacar Foto")
        }

        Button(onClick = {galeriaLuncher.launch("image/*")})
        {
            Text("Elejir de Galeria")
        }

        imagenurl?.let {
            AsyncImage(
                model = it,
                contentDescription = "Factura",
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

fun crearArchivoImagen(context: Context): Uri{
    val file = File.createTempFile(
        "factura_", ".jpg", context.cacheDir
    )
    return FileProvider.getUriForFile(
        context,"${context.packageName}.provider", file
    )
}

