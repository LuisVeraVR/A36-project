package com.example.bankingapp.ui.transactions.detail

import com.example.bankingapp.domain.model.Transaction

/**
 * Estado de la pantalla de detalle de transacción.
 */
sealed class TransactionDetailUiState {
    object Loading : TransactionDetailUiState()
    data class Success(val transaction: Transaction) : TransactionDetailUiState()
    data class Error(val message: String) : TransactionDetailUiState()
}