package com.example.energyconsumption

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LockReset
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.energyconsumption.ui.theme.BrandGreen
import com.example.energyconsumption.ui.theme.BrandGreenSoft
import com.example.energyconsumption.ui.theme.TextPrimary
import com.example.energyconsumption.ui.theme.TextSecondary

@Composable
fun ForgotPasswordScreen(onBack: () -> Unit) {
    var email by rememberSaveable { mutableStateOf("") }
    var message by rememberSaveable { mutableStateOf("") }
    Column(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).navigationBarsPadding().padding(28.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(modifier = Modifier.size(84.dp).background(BrandGreenSoft, CircleShape), contentAlignment = Alignment.Center) {
            Icon(Icons.Filled.LockReset, null, tint = BrandGreen, modifier = Modifier.size(42.dp))
        }
        Spacer(Modifier.height(18.dp))
        Text("Recuperar senha", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
        Text("Informe seu e-mail para receber as instruções de redefinição.", fontSize = 15.sp, color = TextSecondary, modifier = Modifier.padding(top = 8.dp), textAlign = androidx.compose.ui.text.style.TextAlign.Center)
        Spacer(Modifier.height(24.dp))
        OutlinedTextField(
            value = email,
            onValueChange = { email = it; message = "" },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("E-mail") },
            leadingIcon = { Icon(Icons.Filled.Email, null, tint = BrandGreen) },
            singleLine = true,
            shape = RoundedCornerShape(16.dp)
        )
        if (message.isNotBlank()) Text(message, color = BrandGreen, fontSize = 13.sp, modifier = Modifier.padding(top = 10.dp))
        Spacer(Modifier.height(16.dp))
        Button(
            onClick = { message = if (email.contains('@')) "Instruções enviadas para $email" else "Digite um e-mail válido." },
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = BrandGreen)
        ) { Text("Enviar instruções", fontWeight = FontWeight.Bold) }
        Spacer(Modifier.height(10.dp))
        OutlinedButton(onClick = onBack, modifier = Modifier.fillMaxWidth().height(52.dp), shape = RoundedCornerShape(16.dp)) {
            Text("Voltar para o login")
        }
    }
}
