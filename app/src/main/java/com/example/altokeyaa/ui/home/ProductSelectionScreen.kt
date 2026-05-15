package com.example.altokeyaa.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.altokeyaa.ui.orders.Product
import com.example.altokeyaa.ui.theme.Primary
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductSelectionScreen(
    category: String,
    onBack: () -> Unit,
    onProductSelected: (Product) -> Unit
) {
    val products = when (category) {
        "Restaurantes" -> listOf(
            Product("Pizza Pepperoni", 25.0, "🍕", "Restaurantes"),
            Product("Hamburguesa Clásica", 15.0, "🍔", "Restaurantes"),
            Product("Sushi Roll", 30.0, "🍣", "Restaurantes"),
            Product("Pollo a la Brasa", 45.0, "🍗", "Restaurantes"),
            Product("Tacos al Pastor", 20.0, "🌮", "Restaurantes"),
            Product("Pasta Carbonara", 28.0, "🍝", "Restaurantes")
        )
        "Supermercado" -> listOf(
            Product("Leche Entera", 4.5, "🥛", "Supermercado"),
            Product("Pan de Molde", 6.0, "🍞", "Supermercado"),
            Product("Manzanas (kg)", 5.5, "🍎", "Supermercado"),
            Product("Detergente", 12.0, "🧼", "Supermercado"),
            Product("Huevos (12 un)", 8.5, "🥚", "Supermercado"),
            Product("Papel Higiénico", 15.0, "🧻", "Supermercado")
        )
        else -> emptyList()
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(category, color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Primary)
            )
        }
    ) { padding ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(16.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFF9F9F9)),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(products) { product ->
                ProductCard(product = product, onClick = { onProductSelected(product) })
            }
        }
    }
}

@Composable
fun ProductCard(product: Product, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(product.icon, fontSize = 48.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = product.name,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                maxLines = 1,
                color = Color.Black
            )
            Text(
                text = "S/. ${String.format(Locale.getDefault(), "%.2f", product.price)}",
                color = Primary,
                fontWeight = FontWeight.Black,
                fontSize = 16.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = onClick,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Primary)
            ) {
                Text("Agregar", fontSize = 12.sp)
            }
        }
    }
}
