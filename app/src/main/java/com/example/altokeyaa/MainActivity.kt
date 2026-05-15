package com.example.altokeyaa

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.compose.runtime.*
import com.example.altokeyaa.ui.home.HomeScreen
import com.example.altokeyaa.ui.home.HomeBottomNavigation
import com.example.altokeyaa.ui.login.LoginScreen
import com.example.altokeyaa.ui.login.LoginViewModel
import com.example.altokeyaa.ui.login.ProfileScreen
import com.example.altokeyaa.ui.orders.OrdersScreen
import com.example.altokeyaa.ui.promo.PromoScreen
import com.example.altokeyaa.ui.firestore.FirestoreDemoScreen
import com.example.altokeyaa.ui.theme.AltokeyaaTheme
import androidx.lifecycle.viewmodel.compose.viewModel

import com.example.altokeyaa.ui.orders.OrdersViewModel
import com.example.altokeyaa.ui.home.ProductSelectionScreen

private enum class AppScreen {
    HOME,
    PROMOS,
    ORDERS,
    LOGIN,
    REGISTER,
    FORGOT_PASSWORD,
    FIRESTORE,
    PRODUCT_SELECTION
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val loginViewModel: LoginViewModel = viewModel()
            val ordersViewModel: OrdersViewModel = viewModel()
            val loginState by loginViewModel.uiState.collectAsState()
            val searchQuery by ordersViewModel.searchQuery.collectAsState()
            val searchResults by ordersViewModel.searchResults.collectAsState()

            AltokeyaaTheme {
                var currentScreen by remember { mutableStateOf(AppScreen.HOME) }
                var selectedCategory by remember { mutableStateOf("") }

                BackHandler(enabled = currentScreen != AppScreen.HOME) {
                    currentScreen = AppScreen.HOME
                }

                val openHome = { currentScreen = AppScreen.HOME }
                val openPromos = { currentScreen = AppScreen.PROMOS }
                val openOrders = { currentScreen = AppScreen.ORDERS }
                val openProfile = { currentScreen = AppScreen.LOGIN }
                val openRegister = { currentScreen = AppScreen.REGISTER }
                val openForgotPassword = { currentScreen = AppScreen.FORGOT_PASSWORD }
                val openFirestore = { currentScreen = AppScreen.FIRESTORE }
                
                val openProductSelection = { category: String ->
                    selectedCategory = category
                    currentScreen = AppScreen.PRODUCT_SELECTION
                }

                Scaffold(
                    bottomBar = {
                        if (currentScreen != AppScreen.REGISTER && 
                            currentScreen != AppScreen.FORGOT_PASSWORD &&
                            currentScreen != AppScreen.PRODUCT_SELECTION) {
                            
                            if (currentScreen != AppScreen.LOGIN || loginState.isUserLoggedIn) {
                                HomeBottomNavigation(
                                    selectedScreen = currentScreen.name,
                                    onHomeClick = openHome,
                                    onPromosClick = openPromos,
                                    onOrdersClick = openOrders,
                                    onProfileClick = openProfile
                                )
                            }
                        }
                    }
                ) { paddingValues ->
                    when (currentScreen) {
                        AppScreen.HOME -> HomeScreen(
                            contentPadding = paddingValues,
                            userName = loginState.currentUserDisplayName,
                            searchQuery = searchQuery,
                            searchResults = searchResults,
                            onSearchQueryChange = ordersViewModel::onSearchQueryChange,
                            onProductClick = { product ->
                                ordersViewModel.addOrderFromProduct(product)
                                ordersViewModel.onSearchQueryChange("")
                                openOrders()
                            },
                            onCategoryClick = openProductSelection,
                            onFirestoreClick = openFirestore
                        )

                        AppScreen.PROMOS -> Box(modifier = Modifier.padding(paddingValues)) {
                            PromoScreen(onBack = openHome)
                        }

                        AppScreen.ORDERS -> Box(modifier = Modifier.padding(paddingValues)) {
                            OrdersScreen(
                                viewModel = ordersViewModel,
                                onBack = openHome
                            )
                        }
                        
                        AppScreen.LOGIN -> {
                            if (loginState.isUserLoggedIn) {
                                Box(modifier = Modifier.padding(paddingValues)) {
                                    ProfileScreen(
                                        loginViewModel = loginViewModel,
                                        ordersViewModel = ordersViewModel,
                                        onLogout = openHome
                                    )
                                }
                            } else {
                                LoginScreen(
                                    viewModel = loginViewModel,
                                    onLoginSuccess = {
                                        openHome()
                                        loginViewModel.resetState()
                                    },
                                    onRegister = openRegister,
                                    onForgotPassword = openForgotPassword,
                                    onBack = openHome
                                )
                            }
                        }
                        
                        AppScreen.REGISTER -> com.example.altokeyaa.ui.login.RegisterScreen(
                            viewModel = loginViewModel,
                            onRegisterSuccess = {
                                openHome()
                                loginViewModel.resetState()
                            },
                            onBack = openProfile
                        )
                        
                        AppScreen.FORGOT_PASSWORD -> com.example.altokeyaa.ui.login.ForgotPasswordScreen(
                            viewModel = loginViewModel,
                            onBack = openProfile
                        )

                        AppScreen.FIRESTORE -> Box(modifier = Modifier.padding(paddingValues)) {
                            FirestoreDemoScreen()
                        }

                        AppScreen.PRODUCT_SELECTION -> ProductSelectionScreen(
                            category = selectedCategory,
                            onBack = openHome,
                            onProductSelected = { product ->
                                ordersViewModel.addOrderFromProduct(product)
                                openOrders()
                            }
                        )
                    }
                }
            }
        }
    }
}
