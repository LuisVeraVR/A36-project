package com.example.bankingapp.ui.home

import com.example.bankingapp.domain.model.Account

/**
 * Estados posibles de la pantalla Home.
 * Patrón común en arquitectura MVVM con Compose.
 */
sealed class HomeUiState {
    object Loading : HomeUiState()
    data class Success(
        val account: Account,
        val userName: String,
        val recentIncome: List<Double>
    ) : HomeUiState()
    data class Error(val message: String) : HomeUiState()
}