package com.example.controlcomprasapp.ocr

import android.content.Context
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.util.Log
import com.example.controlcomprasapp.util.PdfUtils
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions

import com.tom_roush.pdfbox.android.PDFBoxResourceLoader
import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.text.PDFTextStripper

object OcrProcessor {
    fun leerTextoUniversalOCR(
        context: Context,
        uri: Uri,
        onResult: (List<String>) -> Unit
    ) {
        val mime = context.contentResolver.getType(uri)

        if (mime?.contains("pdf") == true) {
            // --- FLUJO A: LECTURA DIRECTA DE PDF (Apache PDFBox) ---
            leerPdfDirecto(context, uri, onResult)
        } else {
            // --- FLUJO B: OCR PARA IMÁGENES (ML Kit) ---
            val image = try {
                InputImage.fromFilePath(context, uri)
            } catch (e: Exception) {
                return onResult(listOf("ERROR_CARGA_IMAGEN"))
            }
            procesarConMLKit(image, onResult)
        }
    }

    private fun leerPdfDirecto(context: Context, uri: Uri, onResult: (List<String>) -> Unit) {
        try {
            // Inicializar PDFBox (Obligatorio)
            PDFBoxResourceLoader.init(context)

            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                val document = PDDocument.load(inputStream)
                val stripper = PDFTextStripper()
                val textoCompleto = stripper.getText(document)
                document.close()

                // Convertimos el string largo en una lista de líneas para tu Parser
                val lineas = textoCompleto.split("\n")
                    .map { it.trim() }
                    .filter { it.isNotBlank() }

                if (lineas.isEmpty()) {
                    // El PDF no tiene capa de texto (escaneado): plan B con OCR,
                    // procesando TODAS las páginas una por una.
                    procesarPdfEscaneado(context, uri, onResult)
                } else {
                    onResult(lineas)
                }
            }
        } catch (e: Exception) {
            Log.e("OcrProcessor", "Error PDFBox: ${e.message}")
            onResult(listOf("ERROR_LECTURA_PDF"))
        }
    }

    private fun procesarPdfEscaneado(context: Context, uri: Uri, onResult: (List<String>) -> Unit) {
        val pfd = try {
            context.contentResolver.openFileDescriptor(uri, "r")
        } catch (e: Exception) {
            null
        }
        if (pfd == null) {
            onResult(listOf("PDF_VACIO_O_ESCANEO"))
            return
        }

        val renderer = try {
            PdfRenderer(pfd)
        } catch (e: Exception) {
            pfd.close()
            onResult(listOf("PDF_VACIO_O_ESCANEO"))
            return
        }

        val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
        val acumuladas = mutableListOf<String>()
        var indice = 0
        val totalPaginas = renderer.pageCount

        fun siguiente() {
            if (indice >= totalPaginas) {
                recognizer.close()
                renderer.close()
                pfd.close()
                onResult(if (acumuladas.isEmpty()) listOf("PDF_VACIO_O_ESCANEO") else acumuladas.toList())
                return
            }

            val page = try {
                renderer.openPage(indice)
            } catch (e: Exception) {
                indice++
                siguiente()
                return
            }

            val bitmap = PdfUtils.renderPaginaBitmap(page, 4f)
            page.close()

            if (bitmap == null) {
                indice++
                siguiente()
                return
            }

            val image = InputImage.fromBitmap(bitmap, 0)
            recognizer.process(image)
                .addOnSuccessListener { visionText ->
                    val lineasPagina = visionText.textBlocks.flatMap { bloque ->
                        bloque.lines.map { it.text }
                    }
                    acumuladas.addAll(lineasPagina)
                    bitmap.recycle()
                    indice++
                    siguiente()
                }
                .addOnFailureListener {
                    bitmap.recycle()
                    indice++
                    siguiente()
                }
        }

        siguiente()
    }

    private fun procesarConMLKit(image: InputImage, onResult: (List<String>) -> Unit) {
        val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
        recognizer.process(image)
            .addOnSuccessListener { visionText ->
                val lineas = visionText.textBlocks.flatMap { bloque ->
                    bloque.lines.map { it.text }
                }
                onResult(lineas)
            }
            .addOnFailureListener {
                onResult(listOf("ERROR_OCR"))
            }
    }
}