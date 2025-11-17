package com.example.bankingapp.ui.loans.detail

import com.example.bankingapp.domain.model.Loan

/**
 * Estado de la pantalla de detalle de préstamo.
 */
sealed class LoanDetailUiState {
    object Loading : LoanDetailUiState()
    data class Success(val loan: Loan) : LoanDetailUiState()
    data class Error(val message: String) : LoanDetailUiState()
}