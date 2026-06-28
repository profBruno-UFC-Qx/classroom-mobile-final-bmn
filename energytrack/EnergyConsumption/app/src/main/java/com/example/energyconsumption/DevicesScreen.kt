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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.example.energyconsumption.ui.theme.CardWhite
import com.example.energyconsumption.ui.theme.DisabledGrey
import com.example.energyconsumption.ui.theme.TextPrimary
import com.example.energyconsumption.ui.theme.TextSecondary

private enum class DeviceFilter { TODOS, ATIVOS, INATIVOS }

@Composable
fun DevicesScreen(
    devices: List<Dispositivo>,
    onToggleDevice: (String) -> Unit,
    onSaveDevice: (Dispositivo) -> Unit,
    onDeleteDevice: (String) -> Unit,
    onNavigate: (AppDestination) -> Unit
) {
    var search by remember { mutableStateOf("") }
    var filter by remember { mutableStateOf(DeviceFilter.TODOS) }
    var editorOpen by remember { mutableStateOf(false) }
    var editingDevice by remember { mutableStateOf<Dispositivo?>(null) }
    var deviceToDelete by remember { mutableStateOf<Dispositivo?>(null) }

    val filteredDevices = devices.filter { device ->
        val matchesSearch = device.nome.contains(search, ignoreCase = true) ||
            device.ambiente.contains(search, ignoreCase = true)
        val matchesFilter = when (filter) {
            DeviceFilter.TODOS -> true
            DeviceFilter.ATIVOS -> device.isAtivo
            DeviceFilter.INATIVOS -> !device.isAtivo
        }
        matchesSearch && matchesFilter
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = { EnergyBottomNavigationBar(AppDestination.DEVICES, onNavigate) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    editingDevice = null
                    editorOpen = true
                },
                containerColor = BrandGreen,
                contentColor = Color.White,
                shape = CircleShape
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Adicionar dispositivo")
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 22.dp, bottom = 104.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Text("Dispositivos", fontSize = 31.sp, fontWeight = FontWeight.Bold, color = BrandGreenDark)
                Text(
                    "Gerencie os equipamentos monitorados",
                    fontSize = 16.sp,
                    color = TextSecondary,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            item {
                OutlinedTextField(
                    value = search,
                    onValueChange = { search = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Buscar dispositivo ou ambiente") },
                    leadingIcon = { Icon(Icons.Filled.Search, null, tint = TextSecondary) },
                    singleLine = true,
                    shape = RoundedCornerShape(18.dp)
                )
            }

            item {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    DeviceFilterChip("Todos", filter == DeviceFilter.TODOS) { filter = DeviceFilter.TODOS }
                    DeviceFilterChip("Ativos", filter == DeviceFilter.ATIVOS) { filter = DeviceFilter.ATIVOS }
                    DeviceFilterChip("Inativos", filter == DeviceFilter.INATIVOS) { filter = DeviceFilter.INATIVOS }
                }
            }

            item {
                Text(
                    text = "${filteredDevices.size} dispositivos",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )
            }

            if (filteredDevices.isEmpty()) {
                item {
                    EmptyDevicesCard(onAdd = {
                        editingDevice = null
                        editorOpen = true
                    })
                }
            } else {
                items(filteredDevices, key = { it.id }) { device ->
                    DeviceCard(
                        device = device,
                        onToggle = { onToggleDevice(device.id) },
                        onEdit = {
                            editingDevice = device
                            editorOpen = true
                        },
                        onDelete = { deviceToDelete = device }
                    )
                }
            }

            item {
                val total = devices.sumOf { it.consumoKwhDia.substringBefore(" ").replace(',', '.').toDoubleOrNull() ?: 0.0 }
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = BrandGreenSoft,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(46.dp)
                                .clip(CircleShape)
                                .background(Color.White),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Filled.Bolt, null, tint = BrandGreen, modifier = Modifier.size(26.dp))
                        }
                        Spacer(Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Consumo total hoje", fontSize = 13.sp, color = TextSecondary)
                            Text(
                                String.format("%.1f kWh", total).replace('.', ','),
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = BrandGreenDark
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("Ativos", fontSize = 13.sp, color = TextSecondary)
                            Text(
                                "${devices.count { it.isAtivo }} de ${devices.size}",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = BrandGreenDark
                            )
                        }
                    }
                }
            }
        }
    }

    if (editorOpen) {
        DeviceEditorDialog(
            existingDevice = editingDevice,
            onDismiss = { editorOpen = false },
            onSave = { device ->
                onSaveDevice(device)
                editorOpen = false
            }
        )
    }

    deviceToDelete?.let { device ->
        AlertDialog(
            onDismissRequest = { deviceToDelete = null },
            title = { Text("Remover dispositivo?") },
            text = { Text("${device.nome} deixará de aparecer no monitoramento.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDeleteDevice(device.id)
                        deviceToDelete = null
                    }
                ) { Text("Remover", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = {
                TextButton(onClick = { deviceToDelete = null }) { Text("Cancelar") }
            }
        )
    }
}

@Composable
private fun DeviceFilterChip(text: String, selected: Boolean, onClick: () -> Unit) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(text, maxLines = 1) },
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = BrandGreen,
            selectedLabelColor = Color.White
        ),
        border = FilterChipDefaults.filterChipBorder(
            enabled = true,
            selected = selected,
            borderColor = MaterialTheme.colorScheme.outline,
            selectedBorderColor = BrandGreen
        )
    )
}

@Composable
private fun DeviceCard(
    device: Dispositivo,
    onToggle: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(62.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(BrandGreenSoft),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = deviceIconFor(device.tipo),
                        contentDescription = device.tipo.label,
                        tint = BrandGreenDark,
                        modifier = Modifier.size(31.dp)
                    )
                }

                Spacer(Modifier.width(13.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        device.nome,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(device.ambiente, fontSize = 14.sp, color = TextSecondary, modifier = Modifier.padding(top = 3.dp))
                    Row(
                        modifier = Modifier.padding(top = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(if (device.isAtivo) BrandGreen else BrandYellow)
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            if (device.isAtivo) "Ativo" else "Standby",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (device.isAtivo) BrandGreenDark else BrandYellow
                        )
                    }
                }

                Switch(
                    checked = device.isAtivo,
                    onCheckedChange = { onToggle() },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = BrandGreen,
                        uncheckedThumbColor = Color.White,
                        uncheckedTrackColor = DisabledGrey.copy(alpha = 0.35f)
                    )
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Filled.Bolt, null, tint = BrandYellow, modifier = Modifier.size(22.dp))
                Spacer(Modifier.width(4.dp))
                Text(
                    device.consumoKwhDia.substringBefore("/"),
                    color = BrandGreenDark,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(" hoje", color = TextSecondary, fontSize = 13.sp)
                Spacer(Modifier.weight(1f))
                IconButton(onClick = onEdit) {
                    Icon(Icons.Filled.Edit, contentDescription = "Editar ${device.nome}", tint = BrandGreenDark)
                }
                IconButton(onClick = onDelete) {
                    Icon(
                        Icons.Filled.DeleteOutline,
                        contentDescription = "Remover ${device.nome}",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyDevicesCard(onAdd: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = BrandGreenSoft,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(22.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("Nenhum dispositivo encontrado", fontWeight = FontWeight.SemiBold, color = TextPrimary)
            Text("Cadastre um equipamento para iniciar o monitoramento.", color = TextSecondary, fontSize = 13.sp)
            OutlinedButton(onClick = onAdd) { Text("Adicionar dispositivo") }
        }
    }
}

@Composable
private fun DeviceEditorDialog(
    existingDevice: Dispositivo?,
    onDismiss: () -> Unit,
    onSave: (Dispositivo) -> Unit
) {
    var name by remember(existingDevice?.id) { mutableStateOf(existingDevice?.nome.orEmpty()) }
    var room by remember(existingDevice?.id) { mutableStateOf(existingDevice?.ambiente ?: "Casa") }
    var type by remember(existingDevice?.id) { mutableStateOf(existingDevice?.tipo ?: DeviceType.OTHER) }
    var active by remember(existingDevice?.id) { mutableStateOf(existingDevice?.isAtivo ?: true) }
    var error by remember(existingDevice?.id) { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(if (existingDevice == null) "Adicionar dispositivo" else "Editar dispositivo")
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it; error = "" },
                    label = { Text("Nome do dispositivo") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = room,
                    onValueChange = { room = it; error = "" },
                    label = { Text("Ambiente") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Text("Tipo", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                DeviceType.entries.chunked(2).forEach { line ->
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                        line.forEach { item ->
                            FilterChip(
                                selected = type == item,
                                onClick = { type = item },
                                label = { Text(item.label, maxLines = 1, overflow = TextOverflow.Ellipsis) },
                                modifier = Modifier.weight(1f),
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = BrandGreen,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }
                }
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                    Text("Monitorar como ativo", modifier = Modifier.weight(1f), color = TextPrimary)
                    Switch(checked = active, onCheckedChange = { active = it })
                }
                if (error.isNotBlank()) {
                    Text(error, color = MaterialTheme.colorScheme.error, fontSize = 13.sp)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    when {
                        name.isBlank() -> error = "Informe o nome do dispositivo."
                        room.isBlank() -> error = "Informe o ambiente."
                        else -> {
                            val device = if (existingDevice == null) {
                                Dispositivo(
                                    nome = name.trim(),
                                    ambiente = room.trim(),
                                    consumoKwhDia = "0,0 kWh/dia",
                                    proporcao = 0f,
                                    isAtivo = active,
                                    tipo = type
                                )
                            } else {
                                existingDevice.copy(
                                    nome = name.trim(),
                                    ambiente = room.trim(),
                                    isAtivo = active,
                                    tipo = type
                                )
                            }
                            onSave(device)
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = BrandGreen)
            ) {
                Text("Salvar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}
