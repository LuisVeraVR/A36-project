package com.example.bankingapp.ui.loans.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bankingapp.data.repository.LoanRepositoryImpl
import com.example.bankingapp.domain.model.enums.LoanStatus
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel para el detalle de préstamo.
 *
 * RF09 - HU12: Ver detalle de préstamo con estado.
 */
class LoanDetailViewModel : ViewModel() {

    private val loanRepository = LoanRepositoryImpl(FirebaseFirestore.getInstance())

    private val _uiState = MutableStateFlow<LoanDetailUiState>(LoanDetailUiState.Loading)
    val uiState: StateFlow<LoanDetailUiState> = _uiState.asStateFlow()

    /**
     * Carga los detalles de un préstamo específico.
     */
    fun loadLoanDetail(loanId: String) {
        viewModelScope.launch {
            _uiState.value = LoanDetailUiState.Loading

            try {
                val result = loanRepository.getLoanById(loanId)

                if (result.isSuccess) {
                    _uiState.value = LoanDetailUiState.Success(result.getOrThrow())
                } else {
                    _uiState.value = LoanDetailUiState.Error(
                        result.exceptionOrNull()?.message ?: "Error al cargar préstamo"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = LoanDetailUiState.Error(e.message ?: "Error desconocido")
            }
        }
    }

    /**
     * Función de administrador para cambiar el estado del préstamo.
     * Solo para propósitos de demostración/testing.
     */
    fun updateLoanStatus(loanId: String, status: LoanStatus, rejectionReason: String? = null) {
        viewModelScope.launch {
            try {
                loanRepository.updateLoanStatus(loanId, status, rejectionReason)
                loadLoanDetail(loanId)
            } catch (e: Exception) {
            }
        }
    }
}