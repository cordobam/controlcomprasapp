package com.example.controlcomprasapp.util

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import java.io.File

object FileUtils{
    fun crearArchivoImagen(context: Context): Uri{
        val file = File.createTempFile(
            "factura_", ".jpg", context.cacheDir
        )
        return FileProvider.getUriForFile(
            context,"${context.packageName}.provider", file
        )
    }
}