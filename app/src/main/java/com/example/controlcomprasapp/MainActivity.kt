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
import android.content.Intent
import android.widget.Toast
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.InputChip
import androidx.compose.foundation.Image
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.graphics.asImageBitmap
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import android.util.Log

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
    var imagenUriString by rememberSaveable { mutableStateOf<String?>(null) }
    val imagenurl = imagenUriString?.let { Uri.parse(it) }
    var textoOCR by rememberSaveable { mutableStateOf("") }
    var lineasOCR by rememberSaveable { mutableStateOf(listOf<String>()) }
    val mimeType = imagenurl?.let { context.contentResolver.getType(it) }

    val cameraLuncher = rememberLauncherForActivityResult(contract = ActivityResultContracts.TakePicture())
    {
        succes -> if (succes) {

        }
    }

    val galeriaLuncher =
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
                val uri = crearArchivoImagen(context)
                imagenUriString = uri.toString()
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

        Button(onClick = { galeriaLuncher.launch(arrayOf("image/*","application/pdf")) })
        {
            Text("Elejir de Galeria")
        }

        if (imagenurl != null) {
            Button(onClick = {
                imagenurl?.let { uri ->
                    leerTextoUniversalOCR(context, uri) { texto ->

                        textoOCR = texto
                        lineasOCR = texto.lines()
                            .filter { it.isNotBlank() }
                    }
                }
            }) {
                Text("Leer con OCR")
            }
        }

        imagenurl?.let { uri ->

            if (mimeType?.contains("pdf") == true) {
                PdfPreview(uri)   // tu composable
            } else {
                AsyncImage(
                    model = uri,
                    contentDescription = "Factura",
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        LazyColumn {
            items(lineasOCR.size) { i ->
                Text(
                    text = lineasOCR[i],
                    modifier = Modifier.padding(4.dp)
                )
            }
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

fun leerTextoConOcr(context: Context,uri: Uri,onResult: (String) -> Unit)
{
    val image = InputImage.fromFilePath(context,uri)
    val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    recognizer.process(image)
        .addOnSuccessListener { visionText ->
            onResult(visionText.text)
        }
        .addOnFailureListener {
            onResult("ERROR OCR")
        }
}


fun pdfPrimeraPaginaBitmap(
    context: Context,
    uri: Uri
): Bitmap? {

    val pfd = context.contentResolver.openFileDescriptor(uri, "r") ?: return null
    val renderer = PdfRenderer(pfd)

    val page = renderer.openPage(0)

    val bitmap = Bitmap.createBitmap(
        page.width,
        page.height,
        Bitmap.Config.ARGB_8888
    )

    page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)

    page.close()
    renderer.close()
    pfd.close()

    return bitmap
}

@Composable
fun PdfPreview(uri: Uri) {
    val context = LocalContext.current

    val bitmap by remember(uri) {
        mutableStateOf(
            pdfPrimeraPaginaBitmap(context, uri)
        )
    }

    bitmap?.let {
        Image(
            bitmap = it.asImageBitmap(),
            contentDescription = "PDF preview"
        )
    }
}

fun leerTextoUniversalOCR(
    context: Context,
    uri: Uri,
    onResult: (String) -> Unit
) {
    val mime = context.contentResolver.getType(uri)

    val image = if (mime?.contains("pdf") == true) {
        val bitmap = pdfPrimeraPaginaBitmap(context, uri)

        if (bitmap == null) {
            onResult("ERROR PDF")
            return
        }

        InputImage.fromBitmap(bitmap, 0)
    } else {
        InputImage.fromFilePath(context, uri)
    }

    val recognizer =
        TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    recognizer.process(image)
        .addOnSuccessListener { onResult(it.text) }
        .addOnFailureListener { onResult("ERROR OCR") }
}