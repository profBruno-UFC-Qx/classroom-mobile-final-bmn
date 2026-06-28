package com.example.energyconsumption

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.energyconsumption.ui.theme.BrandGreen
import com.example.energyconsumption.ui.theme.EnergyConsumptionTheme

private const val ROUTE_LOGIN = "login"
private const val ROUTE_REGISTER = "register"
private const val ROUTE_FORGOT = "forgot"
private const val ROUTE_HOME = "home"
private const val ROUTE_DEVICES = "devices"
private const val ROUTE_HISTORY = "history"
private const val ROUTE_PROFILE = "profile"

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EnergyConsumptionTheme {
                EnergyTrackApp()
            }
        }
    }
}

@Composable
private fun EnergyTrackApp(viewModel: EnergyViewModel = viewModel()) {
    var route by remember { mutableStateOf(ROUTE_LOGIN) }
    val state by viewModel.uiState.collectAsState()
    val dashboard = state.dashboard

    fun navigate(destination: AppDestination) {
        route = when (destination) {
            AppDestination.HOME -> ROUTE_HOME
            AppDestination.DEVICES -> ROUTE_DEVICES
            AppDestination.HISTORY -> ROUTE_HISTORY
            AppDestination.PROFILE -> ROUTE_PROFILE
        }
    }

    when (route) {
        ROUTE_LOGIN -> LoginScreen(
            onLoginClick = { email ->
                viewModel.fazerLogin(email)
                route = ROUTE_HOME
            },
            onRegisterClick = { route = ROUTE_REGISTER },
            onForgotPasswordClick = { route = ROUTE_FORGOT }
        )

        ROUTE_REGISTER -> RegisterScreen(
            onRegister = { profile ->
                viewModel.cadastrar(profile)
                route = ROUTE_HOME
            },
            onBack = { route = ROUTE_LOGIN }
        )

        ROUTE_FORGOT -> ForgotPasswordScreen(onBack = { route = ROUTE_LOGIN })

        else -> {
            when {
                state.isLoading -> LoadingScreen()
                state.error != null -> ErrorScreen(state.error.orEmpty(), viewModel::carregarDados)
                dashboard == null -> LoadingScreen()
                route == ROUTE_HOME -> DashboardScreen(
                    profile = state.profile,
                    dashboard = dashboard,
                    dispositivos = state.dispositivos,
                    onNavigate = ::navigate
                )
                route == ROUTE_DEVICES -> DevicesScreen(
                    devices = state.dispositivos,
                    onToggleDevice = viewModel::alternarDispositivo,
                    onSaveDevice = viewModel::salvarDispositivo,
                    onDeleteDevice = viewModel::removerDispositivo,
                    onNavigate = ::navigate
                )
                route == ROUTE_HISTORY -> HistoricoScreen(
                    registros = state.historico,
                    onNavigate = ::navigate
                )
                route == ROUTE_PROFILE -> ProfileScreen(
                    profile = state.profile,
                    onSave = viewModel::atualizarPerfil,
                    onLogout = { route = ROUTE_LOGIN },
                    onNavigate = ::navigate
                )
            }
        }
    }
}

@Composable
private fun LoadingScreen() {
    Box(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(color = BrandGreen)
    }
}

@Composable
private fun ErrorScreen(message: String, onRetry: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(message, color = Color.Red)
            Spacer(Modifier.height(12.dp))
            Button(onClick = onRetry) { Text("Tentar novamente") }
        }
    }
}
