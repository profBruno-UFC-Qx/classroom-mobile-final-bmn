package com.example.energyconsumption

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Power
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.energyconsumption.ui.theme.BrandGreen
import com.example.energyconsumption.ui.theme.CardWhite
import com.example.energyconsumption.ui.theme.TextSecondary

enum class AppDestination { HOME, DEVICES, HISTORY, PROFILE }

@Composable
fun EnergyBottomNavigationBar(
    destinoAtivo: AppDestination,
    onNavigate: (AppDestination) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 12.dp, vertical = 6.dp),
        shape = RoundedCornerShape(24.dp),
        color = CardWhite,
        shadowElevation = 10.dp,
        tonalElevation = 0.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BottomItem("Início", AppDestination.HOME, destinoAtivo, Icons.Filled.Home, onNavigate)
            BottomItem("Dispositivos", AppDestination.DEVICES, destinoAtivo, Icons.Filled.Power, onNavigate)
            BottomItem("Histórico", AppDestination.HISTORY, destinoAtivo, Icons.Filled.History, onNavigate)
            BottomItem("Perfil", AppDestination.PROFILE, destinoAtivo, Icons.Filled.Person, onNavigate)
        }
    }
}

@Composable
private fun BottomItem(
    label: String,
    destination: AppDestination,
    active: AppDestination,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onNavigate: (AppDestination) -> Unit
) {
    val selected = destination == active

    Column(
        modifier = Modifier
            .width(72.dp)
            .height(64.dp)
            .clickable { onNavigate(destination) },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(width = 30.dp, height = 3.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(if (selected) BrandGreen else Color.Transparent)
        )
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (selected) BrandGreen else TextSecondary,
            modifier = Modifier
                .padding(top = 6.dp)
                .size(22.dp)
        )
        Text(
            text = label,
            color = if (selected) BrandGreen else TextSecondary,
            fontSize = 10.sp,
            maxLines = 1,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
            modifier = Modifier.padding(top = 2.dp)
        )
    }
}
