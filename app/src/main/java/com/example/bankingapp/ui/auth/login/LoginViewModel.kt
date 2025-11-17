package com.example.bankingapp.ui.auth.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bankingapp.data.repository.AuthRepositoryImpl
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel para login con username.
 */
class LoginViewModel : ViewModel() {

    private val authRepository = AuthRepositoryImpl(
        FirebaseAuth.getInstance(),
        FirebaseFirestore.getInstance()
    )

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun updateUsername(username: String) {
        _uiState.update { it.copy(username = username, usernameError = null, generalError = null) }
    }

    fun updatePassword(password: String) {
        _uiState.update { it.copy(password = password, passwordError = null, generalError = null) }
    }

    fun login() {
        // Validar campos
        if (!validateFields()) {
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, generalError = null) }

            try {
                // Login con username (internamente convertirá a email si es necesario)
                val result = authRepository.loginWithUsername(
                    username = _uiState.value.username.trim(),
                    password = _uiState.value.password
                )

                if (result.isSuccess) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isLoginSuccessful = true
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            generalError = result.exceptionOrNull()?.message
                                ?: "Error al iniciar sesión"
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        generalError = e.message ?: "Error desconocido"
                    )
                }
            }
        }
    }

    private fun validateFields(): Boolean {
        val username = _uiState.value.username.trim()
        val password = _uiState.value.password

        var isValid = true

        // Validar username
        if (username.isEmpty()) {
            _uiState.update { it.copy(usernameError = "El usuario es requerido") }
            isValid = false
        } else if (username.length < 3) {
            _uiState.update { it.copy(usernameError = "Usuario inválido") }
            isValid = false
        }

        // Validar contraseña
        if (password.isEmpty()) {
            _uiState.update { it.copy(passwordError = "La contraseña es requerida") }
            isValid = false
        }

        return isValid
    }
}