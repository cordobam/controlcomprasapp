package com.example.controlcomprasapp.data.repository

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.controlcomprasapp.data.local.datasource.HomeDataSource
import com.example.controlcomprasapp.data.local.datasource.MesFiltro
import com.example.controlcomprasapp.data.local.datasource.ProductFilter
import com.example.controlcomprasapp.data.local.datasource.ProductoUI
import com.example.controlcomprasapp.data.local.datasource.TipoProducto
import com.example.controlcomprasapp.data.local.dto.DescuentosDTO
import com.example.controlcomprasapp.data.local.dto.GastoMensualDTO
import com.example.controlcomprasapp.data.local.dto.ItemTicketDTO
import com.example.controlcomprasapp.data.local.dto.ProductoDTO

class HomeRepository(private val HomeDataSoucrce : HomeDataSource) {
    fun obtenerGastoXMes(): List<GastoMensualDTO> {
        return HomeDataSoucrce.obtenerGastoXMes().map {
            GastoMensualDTO(
                fecha = it.fecha,
                monto = it.monto
            )
        }
    }
    /*fun obtenerDescuentos(): List<DescuentosDTO> {
        return HomeDataSoucrce.obtenerDescuentosMax().map {
            DescuentosDTO(
                nombre = it.nombre,
                fecha = it.fecha,
                total = it.total
            )
        }
    }

    fun obtenerGastoXRubro(): List<ItemTicketDTO> {
        return HomeDataSoucrce.obtenerGastoXRubro().map {
            ItemTicketDTO(
                seccion = it.seccion,
                total = it.total
            )
        }
    }

    fun obtenerProdcutosMasComprados(): List<ProductoDTO> {
        return HomeDataSoucrce.obtenerProdcutosMasComprados().map {
            ProductoDTO(
                nombre = it.nombre,
                cant_veces = it.cant_veces
            )
        }
    }*/

    @RequiresApi(Build.VERSION_CODES.O)
    fun obtenerMeses(): List<MesFiltro> {
        return HomeDataSoucrce.generarMeses(3).map {
            MesFiltro(
                mes = it.mes,
                anio = it.anio,
                label = it.label
            )
        }
    }

    // nuevos

    fun obtenerDescuentosPorMes(mes: Int, anio: Int): List<DescuentosDTO> {
        val mesStr = mes.toString().padStart(2, '0')
        val anioStr = anio.toString()
        return HomeDataSoucrce.obtenerDescuentosMax(mesStr, anioStr).map {
            DescuentosDTO(
                nombre = it.nombre,
                fecha = it.fecha,
                total = it.total
            )
        }
    }

    fun obtenerGastoXRubroPorMes(mes: Int, anio: Int): List<ItemTicketDTO> {
        val mesStr = mes.toString().padStart(2, '0')
        val anioStr = anio.toString()
        return HomeDataSoucrce.obtenerGastoXRubro(mesStr, anioStr).map {
            ItemTicketDTO(
                seccion = it.seccion,
                total = it.total
            )
        }
    }


    fun obtenerProdcutosMasCompradosPorMes(mes: Int, anio: Int): List<ProductoDTO> {
        val mesStr = mes.toString().padStart(2, '0')
        val anioStr = anio.toString()
        return HomeDataSoucrce.obtenerProdcutosMasComprados(mesStr, anioStr).map {
            ProductoDTO(
                nombre = it.nombre,
                cant_veces = it.cant_veces
            )
        }
    }
}