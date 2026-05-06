package com.example.altokeyaa.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.altokeyaa.ui.theme.Primary
import com.example.altokeyaa.ui.theme.Secondary
import com.example.altokeyaa.ui.theme.Tertiary

@Composable
fun HomeScreen(
    contentPadding: PaddingValues = PaddingValues(0.dp),
    onFirestoreClick: () -> Unit = {}
) {
    var address by remember { mutableStateOf("Toca para añadir dirección") }
    var isEditingAddress by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
                .background(Color(0xFFF9F9F9))
        ) {
            item {
                HeaderSection(
                    address = address,
                    onAddressClick = { isEditingAddress = true }
                )
            }
            item { PromotionBanner() }
            item { 
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .clickable { onFirestoreClick() },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF4DB6AC))
                ) {
                    Row(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                "🔥 Demo Firebase",
                                color = Color.White,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Black
                            )
                            Text(
                                "Prueba Firestore: Guarda notas en la nube",
                                color = Color.White,
                                fontSize = 13.sp,
                                lineHeight = 16.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                        Text("→", fontSize = 24.sp, color = Color.White)
                    }
                }
            }
            item { MainCategoriesGrid() }
            item { SubCategoriesRow() }
            item { BrandLogosRow() }
        }

        if (isEditingAddress) {
            AddressEditDialog(
                currentAddress = if (address == "Toca para añadir dirección") "" else address,
                onDismiss = { isEditingAddress = false },
                onConfirm = { 
                    address = if (it.isBlank()) "Toca para añadir dirección" else it
                    isEditingAddress = false
                }
            )
        }
    }
}

@Composable
fun AddressEditDialog(
    currentAddress: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var text by remember { mutableStateOf(currentAddress) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nueva dirección", fontWeight = FontWeight.Black, fontSize = 20.sp) },
        text = {
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Ej: Calle Falsa 123") },
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(text) },
                colors = ButtonDefaults.buttonColors(containerColor = Primary)
            ) {
                Text("Confirmar", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", color = Color.Gray)
            }
        }
    )
}

@Composable
fun HeaderSection(
    address: String,
    onAddressClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Primary)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { onAddressClick() }
                    .padding(4.dp)
            ) {
                Text(
                    text = address,
                    color = Color.White,
                    fontWeight = FontWeight.Black,
                    fontSize = 16.sp
                )
                Icon(
                    Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(22.dp)
                )
            }
            Row {
                IconButton(onClick = { }) {
                    Icon(Icons.Outlined.Notifications, contentDescription = null, tint = Color.White)
                }
                IconButton(onClick = { }) {
                    Icon(Icons.Outlined.ShoppingCart, contentDescription = null, tint = Color.White)
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = "",
            onValueChange = {},
            placeholder = { Text("Buscar en Altokeyaa", color = Color.Gray, fontSize = 15.sp) },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .clip(RoundedCornerShape(26.dp)),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
            ),
            trailingIcon = {
                Icon(Icons.Default.Search, contentDescription = null, tint = Primary)
            },
            singleLine = true
        )
        Spacer(modifier = Modifier.height(4.dp))
    }
}

@Composable
fun PromotionBanner() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Primary)
            .padding(bottom = 20.dp)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.28f))
        ) {
            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "¡Altokeyaa llegó!",
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        text = "Tus antojos favoritos ahora más cerca de ti.",
                        color = Color.White,
                        fontSize = 14.sp,
                        lineHeight = 18.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Button(
                        onClick = { },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                        shape = RoundedCornerShape(20.dp),
                        contentPadding = PaddingValues(horizontal = 22.dp),
                        modifier = Modifier.height(40.dp)
                    ) {
                        Text("Ver ofertas", color = Color.Black, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold)
                    }
                }
                Text("🚀", fontSize = 80.sp, modifier = Modifier.padding(start = 8.dp))
            }
        }
    }
}

@Composable
fun MainCategoriesGrid() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        CategoryCard(
            title = "Restaurantes",
            subtitle = "Sabor al instante",
            icon = "🍕",
            modifier = Modifier.weight(1f)
        )
        CategoryCard(
            title = "Supermercado",
            subtitle = "Todo para tu hogar",
            icon = "🛒",
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun CategoryCard(title: String, subtitle: String, icon: String, modifier: Modifier) {
    Card(
        modifier = modifier.height(160.dp),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.TopEnd) {
                Text(icon, fontSize = 58.sp)
            }
            Spacer(modifier = Modifier.weight(1f))
            Text(title, fontWeight = FontWeight.Black, fontSize = 18.sp, color = Color.Black)
            Text(subtitle, fontSize = 13.sp, color = Color.DarkGray, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
fun SubCategoriesRow() {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(22.dp)
    ) {
        val items = listOf(
            "Altokeyaa" to "🏪",
            "Café & Deli" to "☕",
            "Helados" to "🍦",
            "Farmacia" to "💊",
            "Bebidas" to "🍷"
        )
        items(items) { item ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally, 
                modifier = Modifier.width(75.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(70.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    Text(item.second, fontSize = 36.sp)
                }
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = item.first,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.ExtraBold,
                    lineHeight = 15.sp,
                    textAlign = TextAlign.Center,
                    color = Color.Black
                )
            }
        }
    }
}

@Composable
fun BrandLogosRow() {
    Column(modifier = Modifier.padding(top = 12.dp)) {
        Text(
            "Nuestros aliados",
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            fontWeight = FontWeight.Black,
            fontSize = 19.sp,
            color = Color.Black
        )
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 30.dp),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            val brands = listOf(
                "Starbucks" to "☕",
                "McDonald's" to "🍔",
                "Burger King" to "👑",
                "Subway" to "🥖",
                "Pizza Hut" to "🍕"
            )
            items(brands) { brand ->
                Card(
                    modifier = Modifier.size(75.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(3.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(), 
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(brand.second, fontSize = 32.sp)
                        Text(
                            brand.first.take(4), 
                            fontWeight = FontWeight.ExtraBold, 
                            fontSize = 10.sp, 
                            color = Primary
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun HomeBottomNavigation(
    selectedScreen: String,
    onHomeClick: () -> Unit,
    onProfileClick: () -> Unit,
    onPromosClick: () -> Unit,
    onOrdersClick: () -> Unit
) {
    val colors = MaterialTheme.colorScheme
    val navItems = listOf(
        BottomNavItem("HOME", "Inicio", Icons.Default.Home, onHomeClick),
        BottomNavItem("PROMOS", "Promos", Icons.Default.Percent, onPromosClick),
        BottomNavItem("ORDERS", "Pedidos", Icons.AutoMirrored.Filled.ReceiptLong, onOrdersClick),
        BottomNavItem("LOGIN", "Perfil", Icons.Default.PersonOutline, onProfileClick)
    )

    Surface(
        color = colors.surface,
        shadowElevation = 16.dp,
        tonalElevation = 6.dp,
        shape = RoundedCornerShape(topStart = 22.dp, topEnd = 22.dp),
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .border(
                width = 1.dp,
                color = colors.outlineVariant.copy(alpha = 0.45f),
                shape = RoundedCornerShape(topStart = 22.dp, topEnd = 22.dp)
            )
    ) {
        NavigationBar(
            containerColor = Color.Transparent,
            tonalElevation = 0.dp,
            modifier = Modifier.height(84.dp)
        ) {
            navItems.forEach { item ->
                val isSelected = selectedScreen == item.key
                NavigationBarItem(
                    icon = {
                        Icon(
                            item.icon,
                            contentDescription = item.label,
                            modifier = Modifier.size(if (isSelected) 30.dp else 26.dp)
                        )
                    },
                    label = {
                        Text(
                            item.label,
                            fontWeight = if (isSelected) FontWeight.Black else FontWeight.Bold
                        )
                    },
                    alwaysShowLabel = true,
                    selected = isSelected,
                    onClick = item.onClick,
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = colors.primary,
                        selectedTextColor = colors.primary,
                        indicatorColor = colors.primary.copy(alpha = 0.14f),
                        unselectedIconColor = colors.onSurfaceVariant,
                        unselectedTextColor = colors.onSurfaceVariant
                    )
                )
            }
        }
    }
}

private data class BottomNavItem(
    val key: String,
    val label: String,
    val icon: ImageVector,
    val onClick: () -> Unit
)
