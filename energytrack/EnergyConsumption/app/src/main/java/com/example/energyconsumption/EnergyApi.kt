package com.example.energyconsumption

import java.text.NumberFormat
import java.util.Locale
import java.util.UUID
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

/**
 * Modelos que alimentam as telas Compose.
 * Os valores chegam da API e são formatados aqui para a camada visual.
 */
data class Dispositivo(
    val id: String = UUID.randomUUID().toString(),
    val nome: String,
    val ambiente: String,
    val consumoKwhDia: String,
    val proporcao: Float,
    val isAtivo: Boolean = true,
    val tipo: DeviceType = DeviceType.OTHER,
    val potenciaAtualWatts: Double = 0.0
)

data class RegistroDiario(
    val id: String = UUID.randomUUID().toString(),
    val data: String,
    val descricao: String,
    val kwh: String,
    val custo: String,
    val isAlerta: Boolean = false
)

data class DashboardDados(
    val nomeUsuario: String,
    val mesReferencia: String,
    val consumoMesKwh: Int,
    val metaMesKwh: Int,
    val custoBrl: String,
    val variacaoPercent: String,
    val consumoHoje: String,
    val consumoOntem: String,
    val custoHoje: String,
    val totalDispositivos: Int,
    val dispositivos: List<Dispositivo>
)

data class UserProfile(
    val nome: String = "Usuário",
    val email: String = "",
    val ambiente: String = "Casa",
    val metaKwh: Int = 200,
    val tarifa: String = "0,80"
)

/**
 * Altere o IP pelo IPv4 do computador que está executando a API.
 * Exemplo: http://192.168.1.25:8000/
 * Em emulador Android, use http://10.0.2.2:8000/
 */
object ApiConfig {
    const val BASE_URL = "http://192.168.1.144:8000/"
}

// DTOs exatamente no formato JSON retornado pelo backend FastAPI.
data class DashboardDto(
    val monthLabel: String,
    val monthlyConsumptionKwh: Double,
    val goalKwh: Double,
    val monthlyCostBrl: Double,
    val percentVsGoal: Double,
    val todayConsumptionKwh: Double,
    val yesterdayConsumptionKwh: Double,
    val todayCostBrl: Double
)

data class DeviceDto(
    val id: String,
    val name: String,
    val room: String,
    val type: String,
    val active: Boolean,
    val energyKwhToday: Double,
    val currentPowerWatts: Double
)

data class DeviceRequest(
    val id: String? = null,
    val name: String,
    val room: String,
    val type: String,
    val active: Boolean
)

data class HistoryDto(
    val id: String,
    val date: String,
    val description: String,
    val energyKwh: Double,
    val costBrl: Double,
    val alert: Boolean
)

interface EnergyRemoteService {
    @GET("api/dashboard")
    suspend fun getDashboard(): DashboardDto

    @GET("api/devices")
    suspend fun getDevices(): List<DeviceDto>

    @GET("api/history")
    suspend fun getHistory(): List<HistoryDto>

    @POST("api/devices")
    suspend fun createDevice(@Body body: DeviceRequest): DeviceDto

    @PUT("api/devices/{id}")
    suspend fun updateDevice(
        @Path("id") id: String,
        @Body body: DeviceRequest
    ): DeviceDto

    @DELETE("api/devices/{id}")
    suspend fun deleteDevice(@Path("id") id: String)
}

private object RetrofitProvider {
    val service: EnergyRemoteService by lazy {
        Retrofit.Builder()
            .baseUrl(ApiConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(EnergyRemoteService::class.java)
    }
}

data class EnergySnapshot(
    val dashboard: DashboardDados,
    val devices: List<Dispositivo>,
    val history: List<RegistroDiario>
)

class EnergyRepository(
    private val service: EnergyRemoteService = RetrofitProvider.service
) {
    suspend fun load(profile: UserProfile): EnergySnapshot {
        val deviceDtos = service.getDevices()
        val dashboardDto = service.getDashboard()
        val historyDtos = service.getHistory()
        val devices = deviceDtos.map { it.toUiModel() }

        val maxEnergy = devices.maxOfOrNull { device ->
            device.consumoKwhDia.substringBefore(" ").replace(',', '.').toDoubleOrNull() ?: 0.0
        }?.coerceAtLeast(0.1) ?: 1.0

        val devicesWithProportion = devices.map { device ->
            val value = device.consumoKwhDia.substringBefore(" ").replace(',', '.').toDoubleOrNull() ?: 0.0
            device.copy(proporcao = (value / maxEnergy).toFloat().coerceIn(0f, 1f))
        }

        return EnergySnapshot(
            dashboard = DashboardDados(
                nomeUsuario = profile.nome,
                mesReferencia = dashboardDto.monthLabel,
                consumoMesKwh = dashboardDto.monthlyConsumptionKwh.toInt(),
                metaMesKwh = profile.metaKwh,
                custoBrl = money(dashboardDto.monthlyCostBrl),
                variacaoPercent = percent(dashboardDto.percentVsGoal),
                consumoHoje = number(dashboardDto.todayConsumptionKwh),
                consumoOntem = number(dashboardDto.yesterdayConsumptionKwh),
                custoHoje = money(dashboardDto.todayCostBrl),
                totalDispositivos = devicesWithProportion.count { it.isAtivo },
                dispositivos = devicesWithProportion
            ),
            devices = devicesWithProportion,
            history = historyDtos.map { dto ->
                RegistroDiario(
                    id = dto.id,
                    data = dto.date,
                    descricao = dto.description,
                    kwh = "${number(dto.energyKwh)} kWh",
                    custo = money(dto.costBrl),
                    isAlerta = dto.alert
                )
            }
        )
    }

    suspend fun create(device: Dispositivo) {
        service.createDevice(device.toRequest())
    }

    suspend fun update(device: Dispositivo) {
        service.updateDevice(device.id, device.toRequest())
    }

    suspend fun delete(id: String) {
        service.deleteDevice(id)
    }

    private fun DeviceDto.toUiModel(): Dispositivo = Dispositivo(
        id = id,
        nome = name,
        ambiente = room,
        consumoKwhDia = "${number(energyKwhToday)} kWh/dia",
        proporcao = 0f,
        isAtivo = active,
        tipo = type.toDeviceType(),
        potenciaAtualWatts = currentPowerWatts
    )

    private fun Dispositivo.toRequest() = DeviceRequest(
        id = id,
        name = nome,
        room = ambiente,
        type = tipo.name,
        active = isAtivo
    )
}

private fun String.toDeviceType(): DeviceType = runCatching { DeviceType.valueOf(this) }
    .getOrDefault(DeviceType.OTHER)

private val brazilLocale = Locale("pt", "BR")
private fun number(value: Double): String = String.format(brazilLocale, "%.1f", value)
private fun money(value: Double): String = NumberFormat.getCurrencyInstance(brazilLocale).format(value)
private fun percent(value: Double): String = String.format(brazilLocale, "%+.0f%%", value)
