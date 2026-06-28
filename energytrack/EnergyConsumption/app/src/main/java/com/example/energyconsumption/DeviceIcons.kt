package com.example.energyconsumption

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.DevicesOther
import androidx.compose.material.icons.filled.Kitchen
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.LocalLaundryService
import androidx.compose.material.icons.filled.Tv
import androidx.compose.ui.graphics.vector.ImageVector

enum class DeviceType(val label: String) {
    AIR_CONDITIONER("Ar-condicionado"),
    REFRIGERATOR("Geladeira"),
    WASHING_MACHINE("Máquina de lavar"),
    LIGHT("Lâmpada"),
    TV("TV"),
    OTHER("Outro")
}

fun deviceIconFor(type: DeviceType): ImageVector = when (type) {
    DeviceType.AIR_CONDITIONER -> Icons.Filled.AcUnit
    DeviceType.REFRIGERATOR -> Icons.Filled.Kitchen
    DeviceType.WASHING_MACHINE -> Icons.Filled.LocalLaundryService
    DeviceType.LIGHT -> Icons.Filled.Lightbulb
    DeviceType.TV -> Icons.Filled.Tv
    DeviceType.OTHER -> Icons.Filled.DevicesOther
}
