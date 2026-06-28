package com.example.energyconsumption

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.text.KeyboardOptions
import com.example.energyconsumption.ui.theme.BrandGreen
import com.example.energyconsumption.ui.theme.BrandYellow
import com.example.energyconsumption.ui.theme.TextPrimary
import com.example.energyconsumption.ui.theme.TextSecondary

@Composable
fun RegisterScreen(onRegister: (UserProfile) -> Unit, onBack: () -> Unit) {
    var name by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var ambiente by rememberSaveable { mutableStateOf("Casa") }
    var meta by rememberSaveable { mutableStateOf("200") }
    var tarifa by rememberSaveable { mutableStateOf("0,80") }
    var error by rememberSaveable { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .navigationBarsPadding()
            .padding(horizontal = 24.dp, vertical = 28.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text("Criar conta", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
        Text("Configure seu ambiente e acompanhe seu consumo desde o primeiro acesso.", fontSize = 15.sp, color = TextSecondary)
        RegisterField("Nome", name, { name = it; error = "" }, Icons.Filled.Person)
        RegisterField("E-mail", email, { email = it; error = "" }, Icons.Filled.Email, KeyboardType.Email)
        RegisterField("Senha", password, { password = it; error = "" }, Icons.Filled.Lock, KeyboardType.Password)
        RegisterField("Ambiente monitorado", ambiente, { ambiente = it }, Icons.Filled.Home)
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
            RegisterField("Meta kWh/mês", meta, { meta = it.filter(Char::isDigit) }, Icons.Filled.Bolt, modifier = Modifier.weight(1f))
            RegisterField("Tarifa R$/kWh", tarifa, { tarifa = it }, Icons.Filled.Bolt, modifier = Modifier.weight(1f))
        }
        if (error.isNotBlank()) Text(error, color = MaterialTheme.colorScheme.error, fontSize = 13.sp)
        Button(
            onClick = {
                when {
                    name.isBlank() -> error = "Informe seu nome."
                    !email.contains('@') -> error = "Informe um e-mail válido."
                    password.length < 4 -> error = "A senha deve ter pelo menos 4 caracteres."
                    else -> onRegister(UserProfile(name.trim(), email.trim(), ambiente.trim().ifBlank { "Casa" }, meta.toIntOrNull() ?: 200, tarifa.ifBlank { "0,80" }))
                }
            },
            modifier = Modifier.fillMaxWidth().height(54.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = BrandGreen)
        ) {
            Icon(Icons.Filled.Bolt, null, tint = BrandYellow)
            Spacer(Modifier.width(8.dp))
            Text("Finalizar cadastro", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
        OutlinedButton(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = RoundedCornerShape(16.dp)
        ) { Text("Voltar para o login", fontSize = 16.sp) }
    }
}

@Composable
private fun RegisterField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    keyboardType: KeyboardType = KeyboardType.Text,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(5.dp)) {
        Text(label, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = { Icon(icon, null, tint = BrandGreen) },
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            singleLine = true,
            shape = RoundedCornerShape(14.dp)
        )
    }
}
