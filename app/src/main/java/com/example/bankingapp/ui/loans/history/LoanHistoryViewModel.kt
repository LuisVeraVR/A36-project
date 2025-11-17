package com.example.bankingapp.ui.loans.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bankingapp.data.repository.LoanRepositoryImpl
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel para el historial de préstamos.
 *
 * RF09 - HU11: Ver historial de préstamos.
 */
class LoanHistoryViewModel : ViewModel() {

    private val loanRepository = LoanRepositoryImpl(FirebaseFirestore.getInstance())
    private val auth = FirebaseAuth.getInstance()

    private val _uiState = MutableStateFlow<LoanHistoryUiState>(LoanHistoryUiState.Loading)
    val uiState: StateFlow<LoanHistoryUiState> = _uiState.asStateFlow()

    init {
        loadLoanHistory()
    }

    /**
     * Carga el historial de préstamos del usuario.
     */
    fun loadLoanHistory() {
        viewModelScope.launch {
            _uiState.value = LoanHistoryUiState.Loading

            val userId = auth.currentUser?.uid
            if (userId == null) {
                _uiState.value = LoanHistoryUiState.Error("Usuario no autenticado")
                return@launch
            }

            try {
                val result = loanRepository.getLoanHistory(userId)

                if (result.isSuccess) {
                    val loans = result.getOrThrow()
                    _uiState.value = if (loans.isEmpty()) {
                        LoanHistoryUiState.Empty
                    } else {
                        LoanHistoryUiState.Success(loans)
                    }
                } else {
                    _uiState.value = LoanHistoryUiState.Error(
                        result.exceptionOrNull()?.message ?: "Error al cargar préstamos"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = LoanHistoryUiState.Error(e.message ?: "Error desconocido")
            }
        }
    }
}