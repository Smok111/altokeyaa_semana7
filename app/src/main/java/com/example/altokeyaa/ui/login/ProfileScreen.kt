package com.example.altokeyaa.ui.login

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.altokeyaa.ui.orders.PaymentMethod
import com.example.altokeyaa.ui.theme.Primary

@Composable
fun ProfileScreen(
    loginViewModel: LoginViewModel = viewModel(),
    ordersViewModel: com.example.altokeyaa.ui.orders.OrdersViewModel,
    onLogout: () -> Unit
) {
    val loginState by loginViewModel.uiState.collectAsState()
    val paymentMethods by ordersViewModel.paymentMethods.collectAsState()
    var showPaymentDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(Primary),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = null,
                            modifier = Modifier.size(50.dp),
                            tint = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = loginState.currentUserDisplayName ?: "Usuario",
                        color = Color.White,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Info Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .offset(y = (-30).dp),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text("Mi Cuenta", fontWeight = FontWeight.Black, fontSize = 18.sp)
                    Divider()
                    DetailRow("Nombre", loginState.currentUserDisplayName ?: "-")
                    DetailRow("Correo", loginState.email.ifBlank { "Conectado" })
                }
            }
        }

        // Payment Methods Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 24.dp)
                    .offset(y = (-15).dp),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Métodos de Pago", fontWeight = FontWeight.Black, fontSize = 18.sp)
                        IconButton(onClick = { showPaymentDialog = true }) {
                            Icon(Icons.Default.Add, contentDescription = "Agregar", tint = Primary)
                        }
                    }
                    Divider(modifier = Modifier.padding(vertical = 8.dp))
                    
                    if (paymentMethods.isEmpty()) {
                        Text("No tienes métodos de pago", color = Color.Gray, fontSize = 14.sp)
                    } else {
                        paymentMethods.forEach { method ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = if (method.type == "Yape") Icons.Default.QrCodeScanner else Icons.Default.CreditCard,
                                    contentDescription = null,
                                    tint = Primary,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(method.type, fontWeight = FontWeight.Bold)
                                    Text(method.detail, fontSize = 12.sp, color = Color.Gray)
                                }
                            }
                        }
                    }
                }
            }
        }

        item {
            Button(
                onClick = {
                    loginViewModel.onLogoutClick()
                    onLogout()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Cerrar Sesión", fontWeight = FontWeight.Bold)
            }
        }
    }

    if (showPaymentDialog) {
        AddPaymentMethodDialog(
            onDismiss = { showPaymentDialog = false },
            onAdd = { type, detail ->
                ordersViewModel.addPaymentMethod(type, detail)
                showPaymentDialog = false
            }
        )
    }
}

@Composable
fun AddPaymentMethodDialog(onDismiss: () -> Unit, onAdd: (String, String) -> Unit) {
    var selectedType by remember { mutableStateOf("Yape") }
    var detail by remember { mutableStateOf("") }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Agregar Método de Pago", fontWeight = FontWeight.Black) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Selecciona el tipo:")
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(selected = selectedType == "Yape", onClick = { selectedType = "Yape" })
                    Text("Yape", modifier = Modifier.clickable { selectedType = "Yape" })
                    Spacer(modifier = Modifier.width(16.dp))
                    RadioButton(selected = selectedType == "Tarjeta", onClick = { selectedType = "Tarjeta" })
                    Text("Tarjeta", modifier = Modifier.clickable { selectedType = "Tarjeta" })
                }
                OutlinedTextField(
                    value = detail,
                    onValueChange = { detail = it },
                    label = { Text(if (selectedType == "Yape") "Número de celular" else "Número de tarjeta") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
            }
        },
        confirmButton = {
            Button(onClick = { if (detail.isNotBlank()) onAdd(selectedType, detail) }) {
                Text("Agregar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}

@Composable
fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = Color.Gray)
        Text(value, fontWeight = FontWeight.Medium)
    }
}
