package com.example.controlcomprasapp.ui.screens.facturas

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.controlcomprasapp.ui.navegation.Screen

@Composable
fun NuevaFacturaScreen(navController: NavController) {
    val backgroundDark = Color(0xFF111318)
    val surfaceDark = Color(0xFF1A1D24)
    val borderDark = Color(0xFF2A2D35)
    val accentBlue = Color(0xFF4A9EFF)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundDark)
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Título centrado
        Text(
            "Nueva factura",
            color = Color.White,
            fontSize = 22.sp,
            fontWeight = FontWeight.Medium
        )

        androidx.compose.foundation.layout.Spacer(Modifier.height(8.dp))

        // Botón 1: Escanear factura (Primary)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(88.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(accentBlue.copy(alpha = 0.12f))
                .border(1.5.dp, accentBlue, RoundedCornerShape(16.dp))
                .clickable { navController.navigate(Screen.Facturas.route) }
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(accentBlue.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Text("📷", fontSize = 24.sp)
            }
            Text(
                "Escanear factura",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }

        androidx.compose.foundation.layout.Spacer(Modifier.height(16.dp))

        // Botón 2: Cargar manualmente (Secondary)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(88.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(surfaceDark)
                .border(1.dp, borderDark, RoundedCornerShape(16.dp))
                .clickable { navController.navigate(Screen.FacturaManual.route) }
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(surfaceDark)
                    .border(0.5.dp, borderDark, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text("✏️", fontSize = 24.sp)
            }
            Text(
                "Cargar manualmente",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}