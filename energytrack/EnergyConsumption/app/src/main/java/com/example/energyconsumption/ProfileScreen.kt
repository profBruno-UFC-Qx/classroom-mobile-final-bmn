package com.example.energyconsumption

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.energyconsumption.ui.theme.BrandGreen
import com.example.energyconsumption.ui.theme.BrandGreenDark
import com.example.energyconsumption.ui.theme.BrandGreenSoft
import com.example.energyconsumption.ui.theme.BrandYellow
import com.example.energyconsumption.ui.theme.CardWhite
import com.example.energyconsumption.ui.theme.TextPrimary
import com.example.energyconsumption.ui.theme.TextSecondary

@Composable
fun ProfileScreen(
    profile: UserProfile,
    onSave: (UserProfile) -> Unit,
    onLogout: () -> Unit,
    onNavigate: (AppDestination) -> Unit
) {
    var name by rememberSaveable(profile.nome) { mutableStateOf(profile.nome) }
    var email by rememberSaveable(profile.email) { mutableStateOf(profile.email) }
    var ambiente by rememberSaveable(profile.ambiente) { mutableStateOf(profile.ambiente) }
    var meta by rememberSaveable(profile.metaKwh) { mutableStateOf(profile.metaKwh.toString()) }
    var tarifa by rememberSaveable(profile.tarifa) { mutableStateOf(profile.tarifa) }
    var savedMessage by rememberSaveable { mutableStateOf("") }

    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp)
                .padding(bottom = 132.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            // Pequena marca no topo, mantendo a mesma identidade visual do login.
            androidx.compose.foundation.Image(
                painter = painterResource(R.drawable.energytrack_logo),
                contentDescription = "EnergyTrack",
                modifier = Modifier.width(165.dp).align(Alignment.CenterHorizontally)
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(76.dp).clip(CircleShape).background(BrandGreenSoft),
                    contentAlignment = Alignment.Center
                ) {
                    Text(profile.nome.firstOrNull()?.uppercase() ?: "U", fontSize = 34.sp, fontWeight = FontWeight.Bold, color = BrandGreenDark)
                }
                Spacer(Modifier.width(16.dp))
                Column {
                    Text(profile.nome, fontSize = 25.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Text(profile.email, fontSize = 14.sp, color = TextSecondary, modifier = Modifier.padding(top = 3.dp))
                }
            }

            Surface(shape = RoundedCornerShape(24.dp), color = CardWhite, shadowElevation = 5.dp, modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(42.dp).clip(CircleShape).background(BrandGreenSoft), contentAlignment = Alignment.Center) {
                            Icon(Icons.Filled.Person, null, tint = BrandGreen, modifier = Modifier.size(24.dp))
                        }
                        Spacer(Modifier.width(10.dp))
                        Column {
                            Text("Configurações do usuário", fontSize = 21.sp, fontWeight = FontWeight.Bold, color = BrandGreenDark)
                            Text("Altere seus dados e preferências do aplicativo.", fontSize = 13.sp, color = TextSecondary)
                        }
                    }
                    androidx.compose.material3.HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.60f))

                    ProfileField("Nome", name, { name = it }, Icons.Filled.Person)
                    ProfileField("E-mail", email, { email = it }, Icons.Filled.Email)
                    ProfileField("Ambiente monitorado", ambiente, { ambiente = it }, Icons.Filled.Home)
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                        ProfileField("Meta kWh/mês", meta, { meta = it.filter(Char::isDigit) }, Icons.Filled.Bolt, Modifier.weight(1f))
                        ProfileField("Tarifa R$/kWh", tarifa, { tarifa = it }, Icons.Filled.AttachMoney, Modifier.weight(1f))
                    }

                    Button(
                        onClick = {
                            val profileUpdated = UserProfile(
                                nome = name.trim().ifBlank { profile.nome },
                                email = email.trim().ifBlank { profile.email },
                                ambiente = ambiente.trim().ifBlank { profile.ambiente },
                                metaKwh = meta.toIntOrNull() ?: profile.metaKwh,
                                tarifa = tarifa.trim().ifBlank { profile.tarifa }
                            )
                            onSave(profileUpdated)
                            savedMessage = "Alterações salvas com sucesso"
                        },
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = BrandGreen),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Icon(Icons.Filled.Bolt, null, tint = BrandYellow)
                        Spacer(Modifier.width(8.dp))
                        Text("Salvar alterações", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                    if (savedMessage.isNotBlank()) {
                        Text(savedMessage, color = BrandGreenDark, fontSize = 13.sp, modifier = Modifier.align(Alignment.CenterHorizontally))
                    }
                }
            }

            // O painel “O que foi aplicado no app” foi removido conforme solicitado.
            OutlinedButton(
                onClick = onLogout,
                modifier = Modifier.fillMaxWidth().height(54.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = BrandGreen),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, BrandGreen)
            ) {
                Icon(Icons.Filled.Logout, null)
                Spacer(Modifier.width(10.dp))
                Text("Sair da conta", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
        Box(modifier = Modifier.align(Alignment.BottomCenter)) {
            EnergyBottomNavigationBar(AppDestination.PROFILE, onNavigate)
        }
    }
}

@Composable
private fun ProfileField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(5.dp)) {
        Text(label, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = { Icon(icon, null, tint = BrandGreen) },
            singleLine = true,
            shape = RoundedCornerShape(14.dp)
        )
    }
}
