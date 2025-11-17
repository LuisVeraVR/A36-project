package com.example.bankingapp.ui.transactions.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bankingapp.data.repository.TransactionRepositoryImpl
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel para la pantalla de detalle de transacción.
 *
 * RF06 - HU08: Ver detalle de transacción.
 */
class TransactionDetailViewModel : ViewModel() {

    private val transactionRepository = TransactionRepositoryImpl(FirebaseFirestore.getInstance())

    private val _uiState = MutableStateFlow<TransactionDetailUiState>(TransactionDetailUiState.Loading)
    val uiState: StateFlow<TransactionDetailUiState> = _uiState.asStateFlow()

    /**
     * Carga los detalles de una transacción específica.
     */
    fun loadTransactionDetail(transactionId: String) {
        viewModelScope.launch {
            _uiState.value = TransactionDetailUiState.Loading

            try {
                val result = transactionRepository.getTransactionById(transactionId)

                if (result.isSuccess) {
                    _uiState.value = TransactionDetailUiState.Success(result.getOrThrow())
                } else {
                    _uiState.value = TransactionDetailUiState.Error(
                        result.exceptionOrNull()?.message ?: "Error al cargar transacción"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = TransactionDetailUiState.Error(e.message ?: "Error desconocido")
            }
        }
    }
}