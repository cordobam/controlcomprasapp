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
import kotlinx.coroutines.Dispatchers
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

    var tipo by mutableStateOf("COMPRA")
    var banco by mutableStateOf<String?>(null)
    var marca by mutableStateOf<String?>(null)
    var fechaVencimiento by mutableStateOf<String?>(null)

    var descuentos by mutableStateOf<List<Descuentos>>(emptyList())

    var isEscaneando by mutableStateOf(false)
        private set
    var mensaje by mutableStateOf<String?>(null)
        private set

    // agregar nuevo parser en caso de agregar factura de otro lugar
    fun guardar() : Boolean{
        val fechaNormalizada = normalizarFecha(fecha)
return repo.guardarTicketCompleto(
            fecha = fechaNormalizada ?: "",
            nombreLocal = local,
            nombreArchivo = archivo,
            items = items,
            descuentos = descuentos,
            esManual = archivo.isBlank(),
            tipo = tipo,
            banco = banco,
            marca = marca,
fechaVencimiento = fechaVencimiento
        )
    }

    fun procesarUri(context: Context, uri: Uri) {

        val nombreArchivo = obtenerNombreArchivo(context, uri)

        limpiar()
        mensaje = null
        isEscaneando = true

        viewModelScope.launch(Dispatchers.IO) {
            OcrProcessor.leerTextoUniversalOCR(context, uri) { lineas ->
                viewModelScope.launch(Dispatchers.Main) {
                    when {
                        lineas == listOf("PDF_VACIO_O_ESCANEO") ->
                            mensaje = "El PDF parece escaneado y no se pudo leer."
                        lineas == listOf("ERROR_OCR") ->
                            mensaje = "Error de OCR al leer la imagen."
                        lineas == listOf("ERROR_LECTURA_PDF") ->
                            mensaje = "Error al leer el PDF."
                        lineas == listOf("ERROR_CARGA_IMAGEN") ->
                            mensaje = "No se pudo cargar la imagen."
                        else -> {
                            procesarTextoOCR(lineas)
                            if (items.isEmpty()) {
                                mensaje = "No se detectaron ítems."
                            }
                        }
                    }
                    archivo = nombreArchivo
                    isEscaneando = false
                }
            }
        }
    }

    fun procesarTextoOCR(lineas: List<String>) {
        val textoCompleto = lineas.joinToString("\n")

        val parser = parserManager.obtenerParse(textoCompleto)

        if (parser != null) {

            val ticket = parser.parser(lineas)

            fecha = ticket.fecha
            local = ticket.local
            items = ticket.items
            descuentos = ticket.descuentos
            tipo = ticket.tipo
            banco = ticket.banco
            marca = ticket.marca
            fechaVencimiento = ticket.fechaVencimiento

        } else {
            items = emptyList()
            descuentos = emptyList()
            resetearCamposTarjeta()
        }
    }

    private fun resetearCamposTarjeta() {
        tipo = "COMPRA"
        banco = null
        marca = null
        fechaVencimiento = null
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
        resetearCamposTarjeta()
    }

    fun normalizarFecha(fecha:String?): String?{
        if (fecha== null) return null
        if (fecha.matches(Regex("""\d{4}-\d{2}-\d{2}"""))) return fecha

        val normalizada = fecha.replace(Regex("""[-.]"""),"/")
        val partes = fecha.split("/")
        if (partes.size == 3){
            val dia = partes[0].padStart(2,'0')
            val mes = partes[1].padStart(2,'0')
            val anio = if (partes[2].length == 2) "20${partes[2]}" else partes[2]
            return "$anio-$mes-$dia"
        }
        return fecha
    }

}