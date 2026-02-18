package com.example.controlcomprasapp.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.util.Log

object PdfUtils{
    fun pdfPrimeraPaginaBitmap(
        context: Context,
        uri: Uri
    ): Bitmap? { return try {

        val pfd = context.contentResolver.openFileDescriptor(uri, "r") ?: return null
        val renderer = PdfRenderer(pfd)

        val page = renderer.openPage(0)

        val scale = 4f

        val width = (page.width * scale).toInt()
        val height = (page.height * scale).toInt()

        val bitmap = Bitmap.createBitmap(
            width,
            height,
            Bitmap.Config.ARGB_8888
        )

        // --- PASO CRUCIAL: Pintar el fondo de blanco ---
        val canvas = android.graphics.Canvas(bitmap)
        canvas.drawColor(android.graphics.Color.WHITE)
        // -----------------------------------------------


        val matrix = Matrix()
        matrix.setScale(scale, scale)

        page.render(bitmap, null, matrix, PdfRenderer.Page.RENDER_MODE_FOR_PRINT)

        page.close()
        renderer.close()
        pfd.close()

        bitmap

    } catch (e: Exception) {
        Log.e("PDF_ERROR", "Error renderizando PDF", e)
        null
    }
    }
}