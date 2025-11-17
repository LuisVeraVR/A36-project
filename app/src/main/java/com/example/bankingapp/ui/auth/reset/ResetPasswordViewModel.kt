package com.example.bankingapp.ui.auth.reset

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

class ResetPasswordViewModel : ViewModel() {

    private val authRepository = AuthRepositoryImpl(
        FirebaseAuth.getInstance(),
        FirebaseFirestore.getInstance()
    )

    private val _uiState = MutableStateFlow(ResetPasswordUiState())
    val uiState: StateFlow<ResetPasswordUiState> = _uiState.asStateFlow()

    fun updateEmail(email: String) {
        _uiState.update {
            it.copy(
                email = email,
                emailError = null,
                generalError = null,
                successMessage = null
            )
        }
    }

    fun resetPassword() {
        // Validar email
        val email = _uiState.value.email.trim()
        if (email.isEmpty()) {
            _uiState.update { it.copy(emailError = "El email es requerido") }
            return
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _uiState.update { it.copy(emailError = "Email inválido") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, generalError = null) }

            try {
                val result = authRepository.resetPassword(email)

                if (result.isSuccess) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            successMessage = "Se han enviado las instrucciones a tu correo electrónico",
                            isResetSuccessful = true
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            generalError = result.exceptionOrNull()?.message
                                ?: "Error al enviar instrucciones"
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
}