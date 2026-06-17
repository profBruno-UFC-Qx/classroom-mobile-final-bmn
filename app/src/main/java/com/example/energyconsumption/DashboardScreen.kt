package com.example.energyconsumption

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Dados hardcoded para a primeira versão
private val dispositivosHardcoded = listOf(
    Triple("Ar-condicionado", "2,4 kWh/dia", 0.80f),
    Triple("Geladeira",       "1,2 kWh/dia", 0.40f),
    Triple("Máquina de lavar","0,9 kWh/dia", 0.30f),
)

@Composable
fun DashboardScreen(
    onHistoricoClick: () -> Unit,
    nomeUsuario: String = "Maria",
    mesBrasil: String = "Maio 2026",
    consumoMesKwh: Int = 187,
    metaMesKwh: Int = 200,
    custoBrl: String = "R$ 149,60",
    variacaoPercent: String = "-8%",
    consumoHoje: String = "6,2",
    consumoOntem: String = "7,8",
    custoHoje: String = "R$ 4,96",
    totalDispositivos: Int = 5
)  {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
    ) {
        // --- Cabeçalho verde ---
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(GreenDark)
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Saudação + avatar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Olá, $nomeUsuario 👋", fontSize = 16.sp, fontWeight = FontWeight.Medium, color = Color.White)
                    Text(mesBrasil, fontSize = 12.sp, color = GreenLight)
                }
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        nomeUsuario.first().toString(),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = GreenDark
                    )
                }
            }

            // Card principal de consumo
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = Color.White.copy(alpha = 0.15f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Consumo este mês", fontSize = 12.sp, color = GreenLight)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.Bottom) {
                                Text("$consumoMesKwh", fontSize = 34.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                                Text(" kWh", fontSize = 16.sp, color = Color.White, modifier = Modifier.padding(bottom = 4.dp))
                            }
                            Text("Meta: $metaMesKwh kWh  ·  $custoBrl", fontSize = 12.sp, color = GreenLight)
                        }
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color.White.copy(alpha = 0.2f)
                        ) {
                            Text(
                                variacaoPercent,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                fontSize = 13.sp,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }

        // --- Corpo ---
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Resumo rápido
            Text("Resumo rápido", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                StatCard(label = "Hoje", value = "$consumoHoje kWh", modifier = Modifier.weight(1f))
                StatCard(label = "Ontem", value = "$consumoOntem kWh", modifier = Modifier.weight(1f))
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                StatCard(label = "Custo hoje", value = custoHoje, modifier = Modifier.weight(1f))
                StatCard(label = "Dispositivos", value = "$totalDispositivos ativos", modifier = Modifier.weight(1f))
            }

            // Maiores consumidores
            Text("Maiores consumidores", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                dispositivosHardcoded.forEach { (nome, consumo, proporcao) ->
                    DispositivoItem(nome = nome, consumo = consumo, proporcao = proporcao)
                }
            }

            // Alerta
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = Color(0xFFFAEEDA),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("⚠", fontSize = 16.sp)
                    Text(
                        "Consumo acima do normal entre 18h–20h",
                        fontSize = 12.sp,
                        color = Color(0xFF633806)
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = onHistoricoClick,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Ver Histórico")
                    }
                }
            }
        }
    }
}

@Composable
private fun StatCard(label: String, value: String, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(10.dp),
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(label, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(value, fontSize = 16.sp, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
private fun DispositivoItem(nome: String, consumo: String, proporcao: Float) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFE1F5EE)),
                contentAlignment = Alignment.Center
            ) {
                Text("⚡", fontSize = 16.sp)
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(nome, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                Text(consumo, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            // Barra de progresso compacta
            LinearProgressIndicator(
                progress = { proporcao },
                modifier = Modifier
                    .width(60.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp)),
                color = GreenPrimary,
                trackColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
            )
        }
    }
}
