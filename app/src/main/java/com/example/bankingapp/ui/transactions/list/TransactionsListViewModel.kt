package com.example.bankingapp.ui.transactions.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bankingapp.data.repository.TransactionRepositoryImpl
import com.example.bankingapp.domain.model.enums.TransactionType
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel para la pantalla de listado de transacciones.
 *
 * RF05 - HU06, HU07: Listar transacciones con filtros.
 */
class TransactionsListViewModel : ViewModel() {

    private val transactionRepository = TransactionRepositoryImpl(FirebaseFirestore.getInstance())
    private val auth = FirebaseAuth.getInstance()

    private val _uiState = MutableStateFlow<TransactionsUiState>(TransactionsUiState.Loading)
    val uiState: StateFlow<TransactionsUiState> = _uiState.asStateFlow()

    private val _filters = MutableStateFlow(TransactionFilters())
    val filters: StateFlow<TransactionFilters> = _filters.asStateFlow()

    init {
        loadTransactions()
    }

    /**
     * Carga las transacciones aplicando los filtros actuales.
     */
    fun loadTransactions() {
        viewModelScope.launch {
            _uiState.value = TransactionsUiState.Loading

            val userId = auth.currentUser?.uid
            if (userId == null) {
                _uiState.value = TransactionsUiState.Error("Usuario no autenticado")
                return@launch
            }

            try {
                val currentFilters = _filters.value
                val result = transactionRepository.getTransactions(
                    userId = userId,
                    startDate = currentFilters.startDate,
                    endDate = currentFilters.endDate,
                    type = currentFilters.type
                )

                if (result.isSuccess) {
                    val transactions = result.getOrThrow()
                    _uiState.value = if (transactions.isEmpty()) {
                        TransactionsUiState.Empty
                    } else {
                        TransactionsUiState.Success(transactions)
                    }
                } else {
                    _uiState.value = TransactionsUiState.Error(
                        result.exceptionOrNull()?.message ?: "Error al cargar transacciones"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = TransactionsUiState.Error(e.message ?: "Error desconocido")
            }
        }
    }

    /**
     * Actualiza el filtro de tipo de transacción.
     */
    fun setTypeFilter(type: TransactionType?) {
        _filters.value = _filters.value.copy(type = type)
        loadTransactions()
    }

    /**
     * Actualiza el filtro de rango de fechas.
     */
    fun setDateRange(startDate: Long?, endDate: Long?) {
        _filters.value = _filters.value.copy(
            startDate = startDate,
            endDate = endDate
        )
        loadTransactions()
    }

    /**
     * Limpia todos los filtros.
     */
    fun clearFilters() {
        _filters.value = TransactionFilters()
        loadTransactions()
    }
}