package com.example.altokeyaa.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val name: String = "",
    val emailError: String? = null,
    val passwordError: String? = null,
    val isLoginSuccess: Boolean = false,
    val isRegisterSuccess: Boolean = false,
    val isResetSent: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val currentUserDisplayName: String? = null,
    val currentUserEmail: String? = null,
    val isUserLoggedIn: Boolean = false
)

class LoginViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState

    private val auth = Firebase.auth
    private val db = Firebase.firestore
    
    private val authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
        val user = firebaseAuth.currentUser
        if (user != null) {
            _uiState.update { it.copy(
                isUserLoggedIn = true,
                currentUserEmail = user.email
            ) }
            fetchUserData(user.uid)
        } else {
            _uiState.update { it.copy(
                isUserLoggedIn = false,
                currentUserDisplayName = null,
                currentUserEmail = null
            ) }
        }
    }

    init {
        auth.addAuthStateListener(authStateListener)
    }

    override fun onCleared() {
        super.onCleared()
        auth.removeAuthStateListener(authStateListener)
    }

    private fun fetchUserData(uid: String) {
        viewModelScope.launch {
            try {
                val document = db.collection("users").document(uid).get().await()
                val name = document.getString("name") ?: "Usuario"
                _uiState.update { it.copy(currentUserDisplayName = name) }
            } catch (e: Exception) {
                _uiState.update { it.copy(currentUserDisplayName = "Usuario") }
            }
        }
    }

    fun onEmailChange(value: String) {
        _uiState.update { it.copy(email = value, emailError = null, errorMessage = null) }
    }

    fun onPasswordChange(value: String) {
        _uiState.update { it.copy(password = value, passwordError = null, errorMessage = null) }
    }

    fun onNameChange(value: String) {
        _uiState.update { it.copy(name = value, errorMessage = null) }
    }

    fun resetState() {
        _uiState.update { it.copy(
            email = "",
            password = "",
            name = "",
            emailError = null,
            passwordError = null,
            isLoginSuccess = false,
            isRegisterSuccess = false,
            isResetSent = false,
            errorMessage = null
        ) }
    }

    fun onLoginClick() {
        val state = _uiState.value
        if (!validateFields()) return

        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        
        viewModelScope.launch {
            try {
                auth.signInWithEmailAndPassword(state.email, state.password).await()
                // Forzamos actualización inmediata del estado para evitar el "bug" de navegación
                val user = auth.currentUser
                if (user != null) {
                    _uiState.update { it.copy(
                        isUserLoggedIn = true,
                        isLoginSuccess = true, 
                        isLoading = false,
                        currentUserEmail = user.email
                    ) }
                    fetchUserData(user.uid)
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(
                    errorMessage = "Error: ${e.localizedMessage}",
                    isLoading = false
                ) }
            }
        }
    }

    fun onRegisterClick() {
        val state = _uiState.value
        if (state.name.isBlank()) {
            _uiState.update { it.copy(errorMessage = "El nombre es obligatorio") }
            return
        }
        if (!validateFields()) return

        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch {
            try {
                val result = auth.createUserWithEmailAndPassword(state.email, state.password).await()
                val uid = result.user?.uid
                
                if (uid != null) {
                    val userMap = mapOf(
                        "uid" to uid,
                        "name" to state.name,
                        "email" to state.email,
                        "createdAt" to System.currentTimeMillis()
                    )
                    db.collection("users").document(uid).set(userMap).await()
                    
                    _uiState.update { it.copy(
                        isUserLoggedIn = true,
                        isRegisterSuccess = true, 
                        isLoading = false,
                        currentUserDisplayName = state.name,
                        currentUserEmail = state.email
                    ) }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(
                    errorMessage = "Error al registrar: ${e.localizedMessage}",
                    isLoading = false
                ) }
            }
        }
    }

    fun onLogoutClick() {
        auth.signOut()
    }

    fun onResetPasswordClick() {
        val email = _uiState.value.email
        if (email.isBlank() || !email.contains("@")) {
            _uiState.update { it.copy(emailError = "Ingresa un correo válido") }
            return
        }

        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch {
            try {
                auth.sendPasswordResetEmail(email).await()
                _uiState.update { it.copy(isResetSent = true, isLoading = false) }
            } catch (e: Exception) {
                _uiState.update { it.copy(
                    errorMessage = "Error: ${e.localizedMessage}",
                    isLoading = false
                ) }
            }
        }
    }

    private fun validateFields(): Boolean {
        val state = _uiState.value
        var isValid = true

        if (state.email.isBlank() || !state.email.contains("@")) {
            _uiState.update { it.copy(emailError = "Correo inválido") }
            isValid = false
        }
        if (state.password.length < 6) {
            _uiState.update { it.copy(passwordError = "Mínimo 6 caracteres") }
            isValid = false
        }
        return isValid
    }
}
