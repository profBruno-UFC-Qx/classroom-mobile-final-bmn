package com.example.energyconsumption

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Modelo de dados (hardcoded para a primeira versão)
data class RegistroDiario(
    val data: String,
    val descricao: String,
    val kwh: String,
    val custo: String,
    val isAlerta: Boolean = false
)

private val meses = listOf("Jan", "Fev", "Mar", "Abr", "Mai", "Jun", "Jul", "Ago", "Set", "Out", "Nov", "Dez")

// Dados hardcoded — serão substituídos por Room/ViewModel
private val registrosHardcoded = listOf(
    RegistroDiario("Hoje, 31 mai",   "6 registros",  "6,2 kWh", "R$ 4,96", isAlerta = false),
    RegistroDiario("Ontem, 30 mai",  "Pico às 19h",  "7,8 kWh", "R$ 6,24", isAlerta = true),
    RegistroDiario("29 mai",         "Normal",        "5,9 kWh", "R$ 4,72", isAlerta = false),
    RegistroDiario("28 mai",         "Normal",        "6,4 kWh", "R$ 5,12", isAlerta = false),
    RegistroDiario("27 mai",         "Normal",        "6,1 kWh", "R$ 4,88", isAlerta = false),
    RegistroDiario("26 mai",         "Consumo alto",  "9,2 kWh", "R$ 7,36", isAlerta = true),
)

// Alturas relativas para o gráfico de barras (hardcoded)
private val alturasBarra = listOf(0.50f, 0.69f, 0.60f, 0.78f, 0.48f)
private val labelsBarra   = listOf("S1", "S2", "S3", "S4", "Hj")

@Composable
fun HistoricoScreen() {
    var mesSelecionado by remember { mutableIntStateOf(4) } // 0-indexed, "Mai" = índice 4

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // --- Cabeçalho ---
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(GreenDark)
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            Text("Histórico de Consumo", fontSize = 17.sp, fontWeight = FontWeight.Medium, color = Color.White)
            Text("Acompanhe seu uso ao longo do tempo", fontSize = 12.sp, color = GreenLight)
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            // Tabs de mês
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    meses.take(6).forEachIndexed { index, mes ->
                        val ativo = index == mesSelecionado
                        Surface(
                            onClick = { mesSelecionado = index },
                            shape = RoundedCornerShape(16.dp),
                            color = if (ativo) GreenPrimary else MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            Text(
                                mes,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                fontSize = 12.sp,
                                color = if (ativo) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // Gráfico de barras simples
            item {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Consumo por semana — ${meses[mesSelecionado]} (kWh)", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(80.dp),
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.Bottom
                        ) {
                            alturasBarra.forEachIndexed { i, frac ->
                                Column(
                                    modifier = Modifier.weight(1f),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Bottom
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .fillMaxHeight(frac)
                                            .clip(RoundedCornerShape(topStart = 3.dp, topEnd = 3.dp))
                                            .background(if (i == alturasBarra.lastIndex) GreenPrimary else GreenLight)
                                    )
                                    Spacer(Modifier.height(4.dp))
                                    Text(labelsBarra[i], fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        }
                    }
                }
            }

            // Cards de resumo
            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("Total do mês", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("187 kWh", fontSize = 17.sp, fontWeight = FontWeight.Medium)
                        }
                    }
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("Custo estimado", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("R$ 149,60", fontSize = 17.sp, fontWeight = FontWeight.Medium)
                        }
                    }
                }
            }

            // Título lista
            item {
                Text("Registros diários", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            // Lista de registros
            items(registrosHardcoded) { registro ->
                RegistroItem(registro)
            }
        }
    }
}

@Composable
private fun RegistroItem(registro: RegistroDiario) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(if (registro.isAlerta) Color(0xFFEF9F27) else GreenPrimary)
                )
                Column {
                    Text(registro.data, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                    Text(registro.descricao, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(registro.kwh, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                Text(registro.custo, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}
