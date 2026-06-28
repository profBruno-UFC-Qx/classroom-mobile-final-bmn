package com.example.energyconsumption

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.text.KeyboardOptions
import com.example.energyconsumption.ui.theme.BrandGreen
import com.example.energyconsumption.ui.theme.BrandGreenDark
import com.example.energyconsumption.ui.theme.BrandGreenSoft
import com.example.energyconsumption.ui.theme.BrandYellow
import com.example.energyconsumption.ui.theme.TextPrimary
import com.example.energyconsumption.ui.theme.TextSecondary

@Composable
fun LoginScreen(
    onLoginClick: (String) -> Unit,
    onRegisterClick: () -> Unit,
    onForgotPasswordClick: () -> Unit
) {
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var showPassword by rememberSaveable { mutableStateOf(false) }
    var error by rememberSaveable { mutableStateOf("") }

    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        // Formas decorativas leves, inspiradas no arco verde do protótipo.
        Box(
            modifier = Modifier
                .size(220.dp)
                .padding(start = 0.dp)
                .clip(CircleShape)
                .background(BrandGreenSoft)
                .align(Alignment.TopStart)
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 28.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(Modifier.height(12.dp))
            Image(
                painter = painterResource(R.drawable.energytrack_logo),
                contentDescription = "Logo EnergyTrack",
                modifier = Modifier.width(285.dp).height(235.dp)
            )
            Text(
                text = "O poder de economizar energia\nsob seu controle",
                color = TextSecondary,
                fontSize = 19.sp,
                lineHeight = 25.sp,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Medium
            )
            Spacer(Modifier.height(8.dp))

            LoginTextField(
                label = "E-mail",
                value = email,
                onValueChange = { email = it; error = "" },
                placeholder = "seu@email.com",
                leadingIcon = Icons.Filled.Email,
                keyboardType = KeyboardType.Email
            )
            LoginTextField(
                label = "Senha",
                value = password,
                onValueChange = { password = it; error = "" },
                placeholder = "••••••••",
                leadingIcon = Icons.Filled.Lock,
                keyboardType = KeyboardType.Password,
                visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                trailing = {
                    IconButton(onClick = { showPassword = !showPassword }) {
                        Icon(if (showPassword) Icons.Filled.VisibilityOff else Icons.Filled.Visibility, "Mostrar senha", tint = TextSecondary)
                    }
                }
            )
            TextButton(onClick = onForgotPasswordClick, modifier = Modifier.align(Alignment.End)) {
                Text("Esqueci minha senha", color = BrandGreen, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
            }
            if (error.isNotBlank()) {
                Text(error, color = MaterialTheme.colorScheme.error, fontSize = 13.sp, modifier = Modifier.align(Alignment.Start))
            }
            Button(
                onClick = {
                    when {
                        !email.contains('@') -> error = "Informe um e-mail válido."
                        password.length < 4 -> error = "A senha deve ter pelo menos 4 caracteres."
                        else -> onLoginClick(email.trim())
                    }
                },
                modifier = Modifier.fillMaxWidth().height(54.dp),
                shape = RoundedCornerShape(17.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BrandGreen)
            ) {
                Icon(Icons.Filled.Bolt, null, tint = BrandYellow, modifier = Modifier.size(25.dp))
                Spacer(Modifier.width(10.dp))
                Text("Entrar", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                HorizontalDivider(modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.outline)
                Text("ou", color = TextSecondary, modifier = Modifier.padding(horizontal = 14.dp))
                HorizontalDivider(modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.outline)
            }
            OutlinedButton(
                onClick = onRegisterClick,
                modifier = Modifier.fillMaxWidth().height(54.dp),
                shape = RoundedCornerShape(17.dp),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, BrandGreen),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = BrandGreen)
            ) {
                Icon(Icons.Filled.PersonAdd, null)
                Spacer(Modifier.width(10.dp))
                Text("Criar conta", fontSize = 17.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(28.dp).clip(CircleShape).background(BrandGreenSoft), contentAlignment = Alignment.Center) {
                    Icon(Icons.Filled.Lock, null, tint = BrandGreen, modifier = Modifier.size(15.dp))
                }
                Spacer(Modifier.width(8.dp))
                Text("Seus dados estão protegidos.", fontSize = 12.sp, color = TextSecondary)
            }
        }
    }
}

@Composable
private fun LoginTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    leadingIcon: androidx.compose.ui.graphics.vector.ImageVector,
    keyboardType: KeyboardType,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    trailing: (@Composable (() -> Unit))? = null
) {
    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(label, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(placeholder, color = TextSecondary) },
            leadingIcon = { Icon(leadingIcon, null, tint = BrandGreen) },
            trailingIcon = trailing,
            visualTransformation = visualTransformation,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            singleLine = true,
            shape = RoundedCornerShape(16.dp)
        )
    }
}
