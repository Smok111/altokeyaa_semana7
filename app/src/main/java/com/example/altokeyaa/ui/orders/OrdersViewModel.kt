package com.example.altokeyaa.ui.orders

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import com.example.altokeyaa.ui.theme.Secondary
import com.example.altokeyaa.ui.theme.Tertiary
import java.util.Locale

data class Product(
    val name: String,
    val price: Double,
    val icon: String,
    val category: String
)

data class PaymentMethod(
    val type: String, // "Yape" or "Tarjeta"
    val detail: String
)

class OrdersViewModel : ViewModel() {
    val allProducts = listOf(
        // Restaurantes
        Product("Pizza Pepperoni", 25.0, "🍕", "Restaurantes"),
        Product("Hamburguesa Clásica", 15.0, "🍔", "Restaurantes"),
        Product("Sushi Roll", 30.0, "🍣", "Restaurantes"),
        Product("Pollo a la Brasa", 45.0, "🍗", "Restaurantes"),
        Product("Tacos al Pastor", 20.0, "🌮", "Restaurantes"),
        Product("Pasta Carbonara", 28.0, "🍝", "Restaurantes"),
        // Supermercado
        Product("Leche Entera", 4.5, "🥛", "Supermercado"),
        Product("Pan de Molde", 6.0, "🍞", "Supermercado"),
        Product("Manzanas (kg)", 5.5, "🍎", "Supermercado"),
        Product("Detergente", 12.0, "🧼", "Supermercado"),
        Product("Huevos (12 un)", 8.5, "🥚", "Supermercado"),
        Product("Papel Higiénico", 15.0, "🧻", "Supermercado")
    )

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _searchResults = MutableStateFlow<List<Product>>(emptyList())
    val searchResults = _searchResults.asStateFlow()

    private val _paymentMethods = MutableStateFlow<List<PaymentMethod>>(emptyList())
    val paymentMethods = _paymentMethods.asStateFlow()

    private val _selectedPaymentMethod = MutableStateFlow<PaymentMethod?>(null)
    val selectedPaymentMethod = _selectedPaymentMethod.asStateFlow()

    private val _orders = MutableStateFlow<List<Order>>(listOf(
        Order(
            "#PE001",
            "Starbucks",
            "Entregado",
            "Hoy, 10:30 AM",
            "S/. 12.50",
            "☕",
            Color(0xFF4CAF50),
            listOf("Latte grande", "Croissant"),
            "Tarjeta **** 1234"
        )
    ))
    val orders: StateFlow<List<Order>> = _orders
        .map { list ->
            list.sortedBy { it.isDelivered }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
        if (query.isBlank()) {
            _searchResults.value = emptyList()
        } else {
            _searchResults.value = allProducts.filter { 
                it.name.contains(query, ignoreCase = true) 
            }
        }
    }

    fun addPaymentMethod(type: String, detail: String) {
        val newMethod = PaymentMethod(type, detail)
        _paymentMethods.update { it + newMethod }
        if (_selectedPaymentMethod.value == null) {
            _selectedPaymentMethod.value = newMethod
        }
    }

    fun addOrderFromProduct(product: Product) {
        val paymentDetail = _selectedPaymentMethod.value?.let { "${it.type}: ${it.detail}" } ?: "Efectivo"
        val newOrder = Order(
            id = "#PE${(100..999).random()}",
            merchant = if (product.category == "Restaurantes") "Restaurante Local" else "Supermercado Altokeyaa",
            status = "Pendiente de pago",
            date = "Recién pedido",
            amount = "S/. ${String.format(Locale.getDefault(), "%.2f", product.price)}",
            icon = product.icon,
            statusColor = Color(0xFFFFA500), // Orange for pending
            items = listOf(product.name),
            paymentMethod = paymentDetail,
            isPaid = false,
            isDelivered = false
        )
        _orders.update { listOf(newOrder) + it }
    }

    fun payOrder(orderId: String) {
        _orders.update { currentOrders ->
            currentOrders.map { order ->
                if (order.id == orderId) {
                    order.copy(
                        status = "En preparación",
                        statusColor = Tertiary,
                        isPaid = true
                    )
                } else order
            }
        }
    }

    fun deliverOrder(orderId: String) {
        _orders.update { currentOrders ->
            currentOrders.map { order ->
                if (order.id == orderId) {
                    order.copy(
                        status = "Entregado con éxito",
                        statusColor = Color.Gray,
                        isDelivered = true
                    )
                } else order
            }
        }
    }
}
