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
import com.example.altokeyaa.ui.orders.OrdersScreen
import com.example.altokeyaa.ui.promo.PromoScreen
import com.example.altokeyaa.ui.firestore.FirestoreDemoScreen
import com.example.altokeyaa.ui.theme.AltokeyaaTheme

private enum class AppScreen {
    HOME,
    PROMOS,
    ORDERS,
    LOGIN,
    REGISTER,
    FORGOT_PASSWORD,
    FIRESTORE
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AltokeyaaTheme {
                var currentScreen by remember { mutableStateOf(AppScreen.HOME) }

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

                Scaffold(
                    bottomBar = {
                        if (currentScreen != AppScreen.LOGIN && 
                            currentScreen != AppScreen.REGISTER && 
                            currentScreen != AppScreen.FORGOT_PASSWORD) {
                            HomeBottomNavigation(
                                selectedScreen = currentScreen.name,
                                onHomeClick = openHome,
                                onPromosClick = openPromos,
                                onOrdersClick = openOrders,
                                onProfileClick = openProfile
                            )
                        }
                    }
                ) { paddingValues ->
                    when (currentScreen) {
                        AppScreen.HOME -> HomeScreen(
                            contentPadding = paddingValues,
                            onFirestoreClick = openFirestore
                        )

                        AppScreen.PROMOS -> Box(modifier = Modifier.padding(paddingValues)) {
                            PromoScreen(onBack = openHome)
                        }

                        AppScreen.ORDERS -> Box(modifier = Modifier.padding(paddingValues)) {
                            OrdersScreen(onBack = openHome)
                        }
                        AppScreen.LOGIN -> LoginScreen(
                            onLoginSuccess = openHome,
                            onRegister = openRegister,
                            onForgotPassword = openForgotPassword,
                            onBack = openHome
                        )
                        AppScreen.REGISTER -> com.example.altokeyaa.ui.login.RegisterScreen(
                            onRegisterSuccess = openHome,
                            onBack = openProfile
                        )
                        AppScreen.FORGOT_PASSWORD -> com.example.altokeyaa.ui.login.ForgotPasswordScreen(
                            onBack = openProfile
                        )
                        AppScreen.FIRESTORE -> Box(modifier = Modifier.padding(paddingValues)) {
                            FirestoreDemoScreen()
                        }
                    }
                }
            }
        }
    }
}
