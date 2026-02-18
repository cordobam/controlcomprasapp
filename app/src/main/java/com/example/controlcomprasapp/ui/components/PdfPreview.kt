package com.example.controlcomprasapp.ui.components

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import com.example.controlcomprasapp.util.PdfUtils

@Composable
fun PdfPreview(uri: Uri) {
    val context = LocalContext.current

    val bitmap by remember(uri) {
        mutableStateOf(
            PdfUtils.pdfPrimeraPaginaBitmap(context, uri)
        )
    }

    bitmap?.let {
        Image(
            bitmap = it.asImageBitmap(),
            contentDescription = "PDF preview"
        )
    }
}