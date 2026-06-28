package com.example.energyconsumption

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class EnergyUiState(
    val isLoading: Boolean = true,
    val profile: UserProfile = UserProfile(),
    val dashboard: DashboardDados? = null,
    val historico: List<RegistroDiario> = emptyList(),
    val dispositivos: List<Dispositivo> = emptyList(),
    val error: String? = null
)

class EnergyViewModel : ViewModel() {
    private val repository = EnergyRepository()

    private val _uiState = MutableStateFlow(EnergyUiState())
    val uiState: StateFlow<EnergyUiState> = _uiState.asStateFlow()

    init {
        carregarDados()
    }

    fun carregarDados() {
        viewModelScope.launch { refreshFromApi() }
    }

    fun fazerLogin(email: String) {
        val newProfile = _uiState.value.profile.copy(
            nome = nomeDoEmail(email),
            email = email.trim()
        )
        _uiState.value = _uiState.value.copy(profile = newProfile)
        carregarDados()
    }

    fun cadastrar(profile: UserProfile) {
        _uiState.value = _uiState.value.copy(profile = profile)
        carregarDados()
    }

    fun atualizarPerfil(profile: UserProfile) {
        val old = _uiState.value
        val dashboard = old.dashboard?.copy(
            nomeUsuario = profile.nome,
            metaMesKwh = profile.metaKwh
        )
        _uiState.value = old.copy(profile = profile, dashboard = dashboard)
    }

    fun alternarDispositivo(id: String) {
        val device = _uiState.value.dispositivos.firstOrNull { it.id == id } ?: return
        salvarDispositivo(device.copy(isAtivo = !device.isAtivo))
    }

    fun salvarDispositivo(device: Dispositivo) {
        viewModelScope.launch {
            try {
                val exists = _uiState.value.dispositivos.any { it.id == device.id }
                if (exists) repository.update(device) else repository.create(device)
                refreshFromApi(showLoader = false)
            } catch (_: Exception) {
                setApiError("Não foi possível salvar o dispositivo. Verifique se a API está em execução.")
            }
        }
    }

    fun removerDispositivo(id: String) {
        viewModelScope.launch {
            try {
                repository.delete(id)
                refreshFromApi(showLoader = false)
            } catch (_: Exception) {
                setApiError("Não foi possível remover o dispositivo. Verifique a conexão com a API.")
            }
        }
    }

    private suspend fun refreshFromApi(showLoader: Boolean = true) {
        if (showLoader) {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
        }
        try {
            val snapshot = repository.load(_uiState.value.profile)
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                dashboard = snapshot.dashboard,
                historico = snapshot.history,
                dispositivos = snapshot.devices,
                error = null
            )
        } catch (_: Exception) {
            setApiError(
                "Não foi possível obter as medições. Confira o IP em ApiConfig, a rede Wi‑Fi e se a API está ligada."
            )
        }
    }

    private fun setApiError(message: String) {
        _uiState.value = _uiState.value.copy(isLoading = false, error = message)
    }

    private fun nomeDoEmail(email: String): String {
        val raw = email.substringBefore('@').replace(Regex("[._-]+"), " ").trim()
        return raw.split(" ")
            .filter { it.isNotBlank() }
            .joinToString(" ") { word -> word.replaceFirstChar { it.uppercase() } }
            .ifBlank { "Usuário" }
    }
}
