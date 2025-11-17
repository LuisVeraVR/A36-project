package com.example.bankingapp.ui.auth.register

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
 * ViewModel para la pantalla de registro.
 *
 * RF01 - HU01: Registro de usuario con validaciones.
 */
class RegisterViewModel : ViewModel() {

    private val authRepository = AuthRepositoryImpl(
        FirebaseAuth.getInstance(),
        FirebaseFirestore.getInstance()
    )

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    fun updateFirstName(firstName: String) {
        _uiState.update { it.copy(firstName = firstName, firstNameError = null, generalError = null) }
    }

    fun updateLastName(lastName: String) {
        _uiState.update { it.copy(lastName = lastName, lastNameError = null, generalError = null) }
    }

    fun updateUsername(username: String) {
        _uiState.update { it.copy(username = username, usernameError = null, generalError = null) }
    }

    fun updateEmail(email: String) {
        _uiState.update { it.copy(email = email, emailError = null, generalError = null) }
    }

    fun updatePassword(password: String) {
        _uiState.update { it.copy(password = password, passwordError = null, generalError = null) }
    }

    fun register() {
        // Validar campos
        if (!validateFields()) {
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, generalError = null) }

            try {
                val fullName = "${_uiState.value.firstName.trim()} ${_uiState.value.lastName.trim()}"

                val result = authRepository.register(
                    email = _uiState.value.email.trim(),
                    password = _uiState.value.password,
                    fullName = fullName,
                    username = _uiState.value.username.trim()  // ← Agregar username
                )

                if (result.isSuccess) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isRegisterSuccessful = true
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            generalError = result.exceptionOrNull()?.message
                                ?: "Error al registrar usuario"
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
        val firstName = _uiState.value.firstName.trim()
        val lastName = _uiState.value.lastName.trim()
        val username = _uiState.value.username.trim()
        val email = _uiState.value.email.trim()
        val password = _uiState.value.password

        var isValid = true

        // Validar nombres
        if (firstName.isEmpty()) {
            _uiState.update { it.copy(firstNameError = "Requerido") }
            isValid = false
        }

        // Validar apellidos
        if (lastName.isEmpty()) {
            _uiState.update { it.copy(lastNameError = "Requerido") }
            isValid = false
        }

        // Validar username
        if (username.isEmpty()) {
            _uiState.update { it.copy(usernameError = "El usuario es requerido") }
            isValid = false
        } else if (username.length < 3) {
            _uiState.update { it.copy(usernameError = "Mínimo 3 caracteres") }
            isValid = false
        }

        // Validar email
        if (email.isEmpty()) {
            _uiState.update { it.copy(emailError = "El email es requerido") }
            isValid = false
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _uiState.update { it.copy(emailError = "Email inválido") }
            isValid = false
        }

        // Validar contraseña (RF01: mín 8 chars, 1 mayúscula, 1 número)
        if (password.isEmpty()) {
            _uiState.update { it.copy(passwordError = "La contraseña es requerida") }
            isValid = false
        } else if (password.length < 8) {
            _uiState.update { it.copy(passwordError = "Mínimo 8 caracteres") }
            isValid = false
        } else if (!password.any { it.isUpperCase() }) {
            _uiState.update { it.copy(passwordError = "Debe contener al menos 1 mayúscula") }
            isValid = false
        } else if (!password.any { it.isDigit() }) {
            _uiState.update { it.copy(passwordError = "Debe contener al menos 1 número") }
            isValid = false
        }

        return isValid
    }
}