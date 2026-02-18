package com.example.controlcomprasapp.ocr

import android.content.Context
import android.net.Uri
import com.example.controlcomprasapp.util.PdfUtils.pdfPrimeraPaginaBitmap
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions

object OcrProcessor {
    fun leerTextoUniversalOCR(
        context: Context,
        uri: Uri,
        onResult: (List<String>) -> Unit
    ) {
        val mime = context.contentResolver.getType(uri)
        val image = if (mime?.contains("pdf") == true) {
            val bitmap =
                pdfPrimeraPaginaBitmap(context, uri) ?: return onResult(listOf("ERROR PDF"))
            InputImage.fromBitmap(bitmap, 0)
        } else {
            InputImage.fromFilePath(context, uri)
        }

        val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

        recognizer.process(image)
            .addOnSuccessListener { visionText ->

                // --- AQUÍ VA LA VERSIÓN AVANZADA ---

                val todasLasLineas = visionText.textBlocks.flatMap { it.lines }
                    .sortedBy { it.boundingBox?.top ?: 0 }

                val lineasProcesadas = mutableListOf<String>()

                if (todasLasLineas.isNotEmpty()) {
                    var renglonActual = StringBuilder(todasLasLineas[0].text)
                    var yReferencia = todasLasLineas[0].boundingBox?.top ?: 0

                    for (i in 1 until todasLasLineas.size) {
                        val linea = todasLasLineas[i]
                        val yActual = linea.boundingBox?.top ?: 0

                        // Comparamos la altura actual con la anterior
                        // Si la diferencia es menor a 20 píxeles, asumimos que es el mismo renglón
                        if (Math.abs(yActual - yReferencia) < 20) {
                            renglonActual.append(" ").append(linea.text)
                        } else {
                            // Es un renglón nuevo
                            lineasProcesadas.add(renglonActual.toString())
                            renglonActual = StringBuilder(linea.text)
                            yReferencia = yActual
                        }
                    }
                    // Agregar el último renglón procesado
                    lineasProcesadas.add(renglonActual.toString())
                }

                // Enviamos la lista final de renglones coherentes al resultado
                onResult(lineasProcesadas)

                // ----------------------------------
            }
            .addOnFailureListener {
                onResult(listOf("ERROR OCR"))
            }
    }
}