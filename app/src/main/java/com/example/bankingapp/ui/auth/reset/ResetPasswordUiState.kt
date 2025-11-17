package com.example.bankingapp.ui.auth.reset

data class ResetPasswordUiState(
    val email: String = "",
    val emailError: String? = null,
    val generalError: String? = null,
    val successMessage: String? = null,
    val isLoading: Boolean = false,
    val isResetSuccessful: Boolean = false
)