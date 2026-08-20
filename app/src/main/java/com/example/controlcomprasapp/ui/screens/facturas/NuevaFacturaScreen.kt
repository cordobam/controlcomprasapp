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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
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
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            "Nueva factura",
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
        )

        Text(
            "¿Cómo querés cargarla?",
            color = Color.White,
            fontSize = 13.sp
        )

        // ── Opción OCR ──────────────────────────────────────────────────────
        EntryCard(
            icon = "📷",
            iconBg = Color(0xFF1C2A3A),
            title = "Desde imagen / OCR",
            description = "Sacá una foto o subí un archivo · el sistema lee los datos automáticamente",
            badge = "Rápido",
            badgeColor = accentBlue,
            borderColor = Color(0xFF1F4068),
            onClick = { navController.navigate(Screen.Facturas.route) }
        )

        // ── Divisor ─────────────────────────────────────────────────────────
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            HorizontalDivider(modifier = Modifier.weight(1f), color = Color(0xFF1E1E1E))
            Text("o", color = Color.White, fontSize = 11.sp)
            HorizontalDivider(modifier = Modifier.weight(1f), color = Color(0xFF1E1E1E))
        }

        // ── Opción Manual ────────────────────────────────────────────────────
        EntryCard(
            icon = "✏️",
            iconBg = surfaceDark,
            title = "Carga manual",
            description = "Ingresá local, fecha, items y descuentos a mano",
            badge = null,
            badgeColor = Color.Transparent,
            borderColor = borderDark,
            onClick = { navController.navigate(Screen.FacturaManual.route) }
        )
    }
}

@Composable
private fun EntryCard(
    icon: String,
    iconBg: Color,
    title: String,
    description: String,
    badge: String?,
    badgeColor: Color,
    borderColor: Color,
    onClick: () -> Unit
) {
    val surfaceDark = Color(0xFF1A1D24)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(surfaceDark)
            .border(0.5.dp, borderColor, RoundedCornerShape(14.dp))
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Ícono
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(iconBg),
            contentAlignment = Alignment.Center
        ) {
            Text(icon, fontSize = 22.sp)
        }

        // Texto
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(title, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                badge?.let {
                    Text(
                        it,
                        color = Color.White,
                        fontSize = 9.sp,
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(badgeColor.copy(alpha = 0.15f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
            Text(description, color = Color.White, fontSize = 11.sp, lineHeight = 15.sp)
        }

        // Flecha
        Text("›", color = Color.White, fontSize = 20.sp)
    }
}