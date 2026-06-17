package com.example.energyconsumption


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.energyconsumption.ui.theme.EnergyConsumptionTheme
import com.example.energyconsumption.DashboardScreen
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EnergyConsumptionTheme {

                var telaAtual by remember { mutableStateOf("login") }

                when (telaAtual) {

                    "login" -> LoginScreen(
                        onLoginClick = { _, _ ->
                            telaAtual = "dashboard"
                        },
                        onRegisterClick = {},
                        onForgotPasswordClick = {}
                    )

                    "dashboard" -> DashboardScreen(
                        onHistoricoClick = {
                            telaAtual = "historico"
                        }
                    )

                    "historico" -> HistoricoScreen()
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    EnergyConsumptionTheme {
        Greeting("Android")
    }
}