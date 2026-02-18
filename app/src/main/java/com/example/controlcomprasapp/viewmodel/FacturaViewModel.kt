package com.example.controlcomprasapp.viewmodel

import android.content.Context
import android.net.Uri
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

class FacturaViewModel(
    private val repo: TicketRepository
) : ViewModel() {

    var items by mutableStateOf<List<ItemTicket>>(emptyList())
        private set

    fun guardar() {
        repo.guardarTicket(items)
    }
    fun procesarUri(context: Context, uri: Uri) {
        OcrProcessor.leerTextoUniversalOCR(context, uri) { lineas ->
            items = TicketParser.parsearTicketDesordenado(lineas)
        }
    }
}