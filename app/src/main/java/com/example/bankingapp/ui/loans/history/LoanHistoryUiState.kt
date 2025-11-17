package com.example.bankingapp.ui.loans.history

import com.example.bankingapp.domain.model.Loan

/**
 * Estado de la pantalla de historial de préstamos.
 */
sealed class LoanHistoryUiState {
    object Loading : LoanHistoryUiState()
    data class Success(val loans: List<Loan>) : LoanHistoryUiState()
    data class Error(val message: String) : LoanHistoryUiState()
    object Empty : LoanHistoryUiState()
}