package com.example.controlcomprasapp.viewmodel

import android.content.Context
import android.net.Uri
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

    // agregar nuevo parser en caso de agregar factura de otro lugar

    fun guardar() {
        repo.guardarTicket(items)
    }
    fun procesarUri(context: Context, uri: Uri) {
        OcrProcessor.leerTextoUniversalOCR(context, uri) { lineas ->

            val textoCompleto = lineas.joinToString ("\n")

            val parser = parserManager.obtenerParse(textoCompleto)

            if (parser != null){
                items = parser.parser(lineas)
            } else {
                items = emptyList<ItemTicket>()
            }

        }
    }
}