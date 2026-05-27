package com.example.controlcomprasapp.viewmodel

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import android.widget.Toast
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
import com.example.controlcomprasapp.domain.model.Descuentos
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

    var descuentos by mutableStateOf<List<Descuentos>>(emptyList())

    // agregar nuevo parser en caso de agregar factura de otro lugar
    fun guardar() : Boolean{

        return repo.guardarTicketCompleto(
            fecha = fecha ?: "",
            nombreLocal = local,
            nombreArchivo = archivo,
            items = items,
            descuentos=descuentos,
            esManual = archivo.isBlank())
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
                descuentos = ticket.descuentos

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

    fun agregarItem(nombre: String, cantidad: Int, precioUnitario: Double, seccion: String = "") {
        val nuevoItem = ItemTicket(
            nombre = nombre,
            ticket_id = 0,           // temporal, el repo asigna el real al guardar
            cantidad = cantidad,
            precioUnitario = precioUnitario,
            total = precioUnitario * cantidad,
            seccion = seccion
        )
        items = items + nuevoItem
    }

    fun eliminarItem(item: ItemTicket) {
        items = items - item
    }

    fun agregarDescuento(nombre: String, total: Double) {
        val nuevoDescuento = Descuentos(
            id = 0,                  // temporal
            ticket_id = 0,           // temporal
            nombre = nombre,
            total = total,
            id_archivo = 0           // temporal
        )
        descuentos = descuentos + nuevoDescuento
    }

    fun eliminarDescuento(descuento: Descuentos) {
        descuentos = descuentos - descuento
    }

    fun limpiar() {
        items = emptyList()
        descuentos = emptyList()
        fecha = null
        local = ""
        archivo = ""
    }

}