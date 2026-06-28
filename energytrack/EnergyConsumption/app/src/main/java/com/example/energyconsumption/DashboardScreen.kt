package com.example.energyconsumption

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Power
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.energyconsumption.ui.theme.BrandGreen
import com.example.energyconsumption.ui.theme.BrandGreenDark
import com.example.energyconsumption.ui.theme.BrandGreenSoft
import com.example.energyconsumption.ui.theme.BrandYellow
import com.example.energyconsumption.ui.theme.BrandYellowSoft
import com.example.energyconsumption.ui.theme.CardWhite
import com.example.energyconsumption.ui.theme.TextPrimary
import com.example.energyconsumption.ui.theme.TextSecondary

@Composable
fun DashboardScreen(
    profile: UserProfile,
    dashboard: DashboardDados,
    dispositivos: List<Dispositivo>,
    onNavigate: (AppDestination) -> Unit
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = { EnergyBottomNavigationBar(AppDestination.HOME, onNavigate) }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
            Box(
                modifier = Modifier
                    .size(170.dp)
                    .clip(CircleShape)
                    .background(BrandGreenSoft.copy(alpha = 0.75f))
                    .align(Alignment.TopStart)
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 20.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                DashboardHeader(profile, dashboard.mesReferencia) { onNavigate(AppDestination.PROFILE) }
                MonthlySummaryCard(dashboard)

                Text("Resumo rápido", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                        QuickStatCard("Hoje", "${dashboard.consumoHoje} kWh", Icons.Filled.CalendarToday, Modifier.weight(1f))
                        QuickStatCard("Ontem", "${dashboard.consumoOntem} kWh", Icons.Filled.History, Modifier.weight(1f))
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                        QuickStatCard("Custo hoje", dashboard.custoHoje, Icons.Filled.AttachMoney, Modifier.weight(1f))
                        QuickStatCard("Ativos", "${dispositivos.count { it.isAtivo }} ativos", Icons.Filled.Power, Modifier.weight(1f))
                    }
                }

                Text("Maiores consumidores", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                Surface(
                    shape = RoundedCornerShape(22.dp),
                    color = CardWhite,
                    shadowElevation = 5.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                        dispositivos.sortedByDescending { it.proporcao }.take(3).forEachIndexed { index, device ->
                            ConsumerRow(device)
                            if (index < dispositivos.sortedByDescending { it.proporcao }.take(3).lastIndex) {
                                androidx.compose.material3.HorizontalDivider(
                                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.55f)
                                )
                            }
                        }
                    }
                }

                Surface(
                    onClick = { onNavigate(AppDestination.HISTORY) },
                    shape = RoundedCornerShape(18.dp),
                    color = BrandYellowSoft,
                    modifier = Modifier.fillMaxWidth(),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BrandYellow.copy(alpha = 0.6f))
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier.size(42.dp).clip(RoundedCornerShape(12.dp)).background(BrandYellow),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Filled.WarningAmber, null, tint = Color.White, modifier = Modifier.size(25.dp))
                        }
                        Spacer(Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Consumo acima do normal", fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                            Text("entre 18h–20h", fontSize = 13.sp, color = TextSecondary)
                        }
                        Icon(Icons.Filled.ChevronRight, null, tint = BrandYellow, modifier = Modifier.size(28.dp))
                    }
                }
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun DashboardHeader(profile: UserProfile, mes: String, onProfile: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Column(modifier = Modifier.weight(1f)) {
            Text("Olá, ${profile.nome}", fontSize = 30.sp, fontWeight = FontWeight.Bold, color = TextPrimary, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(mes, fontSize = 16.sp, color = BrandGreenDark, modifier = Modifier.padding(top = 4.dp))
        }
        Surface(onClick = onProfile, modifier = Modifier.size(54.dp), shape = CircleShape, color = BrandGreen, shadowElevation = 4.dp) {
            Box(contentAlignment = Alignment.Center) {
                Text(profile.nome.firstOrNull()?.uppercase() ?: "U", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun MonthlySummaryCard(dashboard: DashboardDados) {
    val progress = (dashboard.consumoMesKwh.toFloat() / dashboard.metaMesKwh.coerceAtLeast(1).toFloat()).coerceIn(0f, 1f)
    Surface(shape = RoundedCornerShape(24.dp), color = CardWhite, shadowElevation = 7.dp, modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(68.dp).clip(CircleShape).background(BrandGreenSoft), contentAlignment = Alignment.Center) {
                    Icon(Icons.Filled.Bolt, null, tint = BrandYellow, modifier = Modifier.size(40.dp))
                }
                Spacer(Modifier.width(14.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("Consumo este mês", fontSize = 16.sp, color = TextSecondary)
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text("${dashboard.consumoMesKwh}", fontSize = 48.sp, lineHeight = 50.sp, fontWeight = FontWeight.Bold, color = BrandGreenDark)
                        Text(" kWh", fontSize = 21.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary, modifier = Modifier.padding(bottom = 7.dp))
                    }
                }
            }
            Spacer(Modifier.height(16.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                LinearProgressIndicator(
                    progress = { progress },
                    color = BrandGreen,
                    trackColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.35f),
                    modifier = Modifier.weight(1f).height(9.dp).clip(RoundedCornerShape(10.dp))
                )
                Spacer(Modifier.width(12.dp))
                Surface(shape = RoundedCornerShape(14.dp), color = BrandGreenSoft) {
                    Column(modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(dashboard.variacaoPercent, color = BrandGreenDark, fontSize = 19.sp, fontWeight = FontWeight.Bold)
                        Text("vs. meta", color = TextSecondary, fontSize = 10.sp)
                    }
                }
            }
            Spacer(Modifier.height(12.dp))
            Text("Meta: ${dashboard.metaMesKwh} kWh  •  ${dashboard.custoBrl}", fontSize = 16.sp, color = TextSecondary)
        }
    }
}

@Composable
private fun QuickStatCard(
    label: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(7.dp)) {
            Box(modifier = Modifier.size(38.dp).clip(CircleShape).background(BrandGreenSoft), contentAlignment = Alignment.Center) {
                Icon(icon, null, tint = BrandYellow, modifier = Modifier.size(23.dp))
            }
            Text(label, fontSize = 13.sp, color = TextSecondary, maxLines = 1)
            Text(value, fontSize = 18.sp, lineHeight = 20.sp, fontWeight = FontWeight.Bold, color = BrandGreenDark, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
    }
}

@Composable
private fun ConsumerRow(device: Dispositivo) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(48.dp).clip(RoundedCornerShape(13.dp)).background(BrandGreenSoft), contentAlignment = Alignment.Center) {
            Icon(deviceIconFor(device.tipo), null, tint = BrandGreenDark, modifier = Modifier.size(26.dp))
        }
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(device.nome, fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(device.consumoKwhDia, fontSize = 13.sp, color = TextSecondary)
        }
        Column(horizontalAlignment = Alignment.End, modifier = Modifier.width(94.dp)) {
            Text(device.consumoKwhDia.substringBefore("/"), fontSize = 15.sp, fontWeight = FontWeight.Bold, color = BrandGreenDark)
            Spacer(Modifier.height(7.dp))
            LinearProgressIndicator(
                progress = { device.proporcao.coerceIn(0f, 1f) },
                color = BrandGreen,
                trackColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.30f),
                modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(8.dp))
            )
        }
    }
}
