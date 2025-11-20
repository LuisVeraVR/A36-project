package com.example.bankingapp.ui.transfer

import com.example.bankingapp.domain.model.Account

/**
 * Estados de UI para la pantalla de transferencia.
 */
sealed class TransferUiState {
    object Loading : TransferUiState()
    data class Ready(
        val currentAccount: Account,
        val toAccountNumber: String = "",
        val amount: String = "",
        val description: String = "",
        val toAccountNumberError: String? = null,
        val amountError: String? = null,
        val generalError: String? = null,
        val isTransferring: Boolean = false
    ) : TransferUiState()
    data class Success(val message: String) : TransferUiState()
    data class Error(val message: String) : TransferUiState()
}
