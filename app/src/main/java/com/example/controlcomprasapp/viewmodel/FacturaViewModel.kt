package com.example.controlcomprasapp.viewmodel

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.controlcomprasapp.data.repository.TicketRepository
import com.example.controlcomprasapp.domain.model.ItemTicket
import com.example.controlcomprasapp.domain.parser.TicketParser
import com.example.controlcomprasapp.ocr.OcrProcessor
import kotlinx.coroutines.launch
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.example.controlcomprasapp.data.parser.CarrefourParser
import com.example.controlcomprasapp.domain.parser.ParserManager

class FacturaViewModel(
    private val repo: TicketRepository,
    private val parserManager: ParserManager
) : ViewModel() {

    var items by mutableStateOf<List<ItemTicket>>(emptyList())
        private set
    var fecha: String? by mutableStateOf("")
    var local by mutableStateOf("")
    var archivo by mutableStateOf("")
    // agregar nuevo parser en caso de agregar factura de otro lugar

    fun guardar() {
        repo.guardarTicketCompleto(
            fecha = fecha ?: "",
            nombreLocal = local,
            nombreArchivo = archivo,
            items = items    )
    }
    fun procesarUri(context: Context, uri: Uri) {

        val nombreArchivo = obtenerNombreArchivo(context, uri)

        OcrProcessor.leerTextoUniversalOCR(context, uri) { lineas ->

            val textoCompleto = lineas.joinToString("\n")

            val parser = parserManager.obtenerParse(textoCompleto)

            if (parser != null) {

                val ticket = parser.parser(lineas)

                fecha = ticket.fecha
                local = ticket.local
                items = ticket.items
                archivo = nombreArchivo

            } else {
                items = emptyList()
            }
        }
    }

    fun obtenerNombreArchivo(context: Context, uri: Uri): String {
        var nombre = "archivo"

        if (uri.scheme == "content") {
            val cursor = context.contentResolver.query(uri, null, null, null, null)
            cursor?.use {
                val index = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (it.moveToFirst() && index != -1) {
                    nombre = it.getString(index)
                }
            }
        }

        if (nombre == "archivo") {
            nombre = uri.lastPathSegment ?: "archivo"
        }
        if (!nombre.lowercase().endsWith(".pdf")) {
            nombre = "$nombre.pdf"
        }

        return nombre
    }
}