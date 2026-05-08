package com.example.altokeyaa.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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
    val errorMessage: String? = null
)

class LoginViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState

    private val auth = Firebase.auth
    private val db = Firebase.firestore

    fun onEmailChange(value: String) {
        _uiState.value = _uiState.value.copy(email = value, emailError = null, errorMessage = null)
    }

    fun onPasswordChange(value: String) {
        _uiState.value = _uiState.value.copy(password = value, passwordError = null, errorMessage = null)
    }

    fun onNameChange(value: String) {
        _uiState.value = _uiState.value.copy(name = value, errorMessage = null)
    }

    fun resetState() {
        _uiState.value = LoginUiState()
    }

    fun onLoginClick() {
        val state = _uiState.value
        if (!validateFields()) return

        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
        
        viewModelScope.launch {
            try {
                auth.signInWithEmailAndPassword(state.email, state.password).await()
                _uiState.value = _uiState.value.copy(isLoginSuccess = true, isLoading = false)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Error: ${e.localizedMessage}",
                    isLoading = false
                )
            }
        }
    }

    fun onRegisterClick() {
        val state = _uiState.value
        if (state.name.isBlank()) {
            _uiState.value = _uiState.value.copy(errorMessage = "El nombre es obligatorio")
            return
        }
        if (!validateFields()) return

        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

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
                }
                
                _uiState.value = _uiState.value.copy(isRegisterSuccess = true, isLoading = false)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Error al registrar: ${e.localizedMessage}",
                    isLoading = false
                )
            }
        }
    }

    fun onResetPasswordClick() {
        val email = _uiState.value.email
        if (email.isBlank() || !email.contains("@")) {
            _uiState.value = _uiState.value.copy(emailError = "Ingresa un correo válido")
            return
        }

        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

        viewModelScope.launch {
            try {
                auth.sendPasswordResetEmail(email).await()
                _uiState.value = _uiState.value.copy(isResetSent = true, isLoading = false)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Error: ${e.localizedMessage}",
                    isLoading = false
                )
            }
        }
    }

    private fun validateFields(): Boolean {
        val state = _uiState.value
        var isValid = true

        if (state.email.isBlank() || !state.email.contains("@")) {
            _uiState.value = _uiState.value.copy(emailError = "Correo inválido")
            isValid = false
        }
        if (state.password.length < 6) {
            _uiState.value = _uiState.value.copy(passwordError = "Mínimo 6 caracteres")
            isValid = false
        }
        return isValid
    }
}
