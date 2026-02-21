package com.example.controlcomprasapp.ocr

import android.content.Context
import android.net.Uri
import android.util.Log
import com.example.controlcomprasapp.util.PdfUtils.pdfPrimeraPaginaBitmap
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
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
                    // Si el PDF no tiene texto (es una imagen), podrías llamar
                    // aquí a tu vieja función de bitmap + OCR como plan B.
                    onResult(listOf("PDF_VACIO_O_ESCANEO"))
                } else {
                    onResult(lineas)
                }
            }
        } catch (e: Exception) {
            Log.e("OcrProcessor", "Error PDFBox: ${e.message}")
            onResult(listOf("ERROR_LECTURA_PDF"))
        }
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