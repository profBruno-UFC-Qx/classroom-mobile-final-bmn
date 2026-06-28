package com.example.energyconsumption

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.energyconsumption.ui.theme.BrandGreen
import com.example.energyconsumption.ui.theme.BrandGreenDark
import com.example.energyconsumption.ui.theme.BrandGreenSoft
import com.example.energyconsumption.ui.theme.BrandYellow
import com.example.energyconsumption.ui.theme.CardWhite
import com.example.energyconsumption.ui.theme.TextPrimary
import com.example.energyconsumption.ui.theme.TextSecondary

@Composable
fun HistoricoScreen(registros: List<RegistroDiario>, onNavigate: (AppDestination) -> Unit) {
    val totalKwh = registros.sumOf { it.kwh.toDoubleFromKwh() }
    val totalCost = registros.sumOf { it.custo.toDoubleFromCurrency() }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = { EnergyBottomNavigationBar(AppDestination.HISTORY, onNavigate) }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 22.dp, bottom = 104.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text("Histórico", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = BrandGreenDark)
                Text(
                    "Leituras recebidas da API de telemetria",
                    fontSize = 16.sp,
                    color = TextSecondary,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            item { ConsumptionChart(registros) }

            item {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                    HistoryStat(
                        "Total registrado",
                        String.format(java.util.Locale("pt", "BR"), "%.1f kWh", totalKwh),
                        Icons.Filled.Bolt,
                        Modifier.weight(1f)
                    )
                    HistoryStat(
                        "Custo estimado",
                        java.text.NumberFormat.getCurrencyInstance(java.util.Locale("pt", "BR")).format(totalCost),
                        Icons.Filled.AttachMoney,
                        Modifier.weight(1f)
                    )
                }
            }

            item {
                Text("Registros diários", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            }

            if (registros.isEmpty()) {
                item {
                    Surface(
                        shape = RoundedCornerShape(18.dp),
                        color = BrandGreenSoft,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            "Ainda não há leituras. Inicie o hardware ou o simulador para enviar telemetria.",
                            color = TextSecondary,
                            modifier = Modifier.padding(18.dp)
                        )
                    }
                }
            } else {
                items(registros, key = { it.id }) { RegistroCard(it) }
            }
        }
    }
}

@Composable
private fun ConsumptionChart(registros: List<RegistroDiario>) {
    val chartData = registros.take(5).reversed()
    val maxValue = chartData.maxOfOrNull { it.kwh.toDoubleFromKwh() }?.coerceAtLeast(0.1) ?: 1.0

    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(38.dp).clip(CircleShape).background(BrandGreenSoft),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Filled.BarChart, null, tint = BrandGreen, modifier = Modifier.size(22.dp))
                }
                Spacer(Modifier.width(10.dp))
                Text("Consumo por dia", fontSize = 17.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
            }
            Spacer(Modifier.height(16.dp))

            if (chartData.isEmpty()) {
                Text("Sem dados de telemetria ainda.", color = TextSecondary, modifier = Modifier.padding(vertical = 28.dp))
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth().height(150.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.Bottom
                ) {
                    chartData.forEachIndexed { index, record ->
                        val fraction = (record.kwh.toDoubleFromKwh() / maxValue).toFloat().coerceIn(0.08f, 1f)
                        Column(
                            modifier = Modifier.weight(1f),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Bottom
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height((105 * fraction).dp)
                                    .clip(RoundedCornerShape(topStart = 7.dp, topEnd = 7.dp))
                                    .background(if (index == chartData.lastIndex) BrandGreen else BrandGreenSoft)
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(record.data.take(5), fontSize = 10.sp, color = TextSecondary)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun HistoryStat(
    label: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Icon(icon, null, tint = BrandYellow, modifier = Modifier.size(24.dp))
            Text(label, fontSize = 12.sp, color = TextSecondary, modifier = Modifier.padding(top = 10.dp))
            Text(value, fontSize = 17.sp, fontWeight = FontWeight.Bold, color = BrandGreenDark, modifier = Modifier.padding(top = 2.dp))
        }
    }
}

@Composable
private fun RegistroCard(registro: RegistroDiario) {
    Surface(shape = RoundedCornerShape(18.dp), color = CardWhite, shadowElevation = 3.dp, modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.padding(15.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(if (registro.isAlerta) com.example.energyconsumption.ui.theme.BrandYellowSoft else BrandGreenSoft),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Filled.CalendarMonth,
                    null,
                    tint = if (registro.isAlerta) BrandYellow else BrandGreen,
                    modifier = Modifier.size(23.dp)
                )
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(registro.data, fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                Text(registro.descricao, fontSize = 13.sp, color = TextSecondary, modifier = Modifier.padding(top = 2.dp))
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(registro.kwh, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = BrandGreenDark)
                Text(registro.custo, fontSize = 12.sp, color = TextSecondary, modifier = Modifier.padding(top = 2.dp))
            }
        }
    }
}

private fun String.toDoubleFromKwh(): Double =
    substringBefore(" ").replace(',', '.').toDoubleOrNull() ?: 0.0

private fun String.toDoubleFromCurrency(): Double =
    replace("R$", "").replace(".", "").replace(',', '.').trim().toDoubleOrNull() ?: 0.0
