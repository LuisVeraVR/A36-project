package com.example.bankingapp.ui.transactions.list

import com.example.bankingapp.domain.model.Transaction
import com.example.bankingapp.domain.model.enums.TransactionType

/**
 * Estado de la pantalla de listado de transacciones.
 */
sealed class TransactionsUiState {
    object Loading : TransactionsUiState()
    data class Success(val transactions: List<Transaction>) : TransactionsUiState()
    data class Error(val message: String) : TransactionsUiState()
    object Empty : TransactionsUiState()
}

/**
 * Filtros disponibles para transacciones.
 */
data class TransactionFilters(
    val type: TransactionType? = null,
    val startDate: Long? = null,
    val endDate: Long? = null
)