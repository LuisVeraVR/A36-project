package com.example.bankingapp.ui.loans.simulator

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bankingapp.data.repository.LoanRepositoryImpl
import com.example.bankingapp.domain.model.Loan
import com.example.bankingapp.domain.model.enums.LoanStatus
import com.example.bankingapp.domain.usecase.loan.SimulateLoanUseCase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel para el simulador de préstamos.
 *
 * RF07 - HU09: Simulación de préstamos.
 * RF08 - HU10: Solicitud de préstamos.
 */
class LoanSimulatorViewModel : ViewModel() {

    private val simulateLoanUseCase = SimulateLoanUseCase()
    private val loanRepository = LoanRepositoryImpl(FirebaseFirestore.getInstance())
    private val auth = FirebaseAuth.getInstance()

    private val _uiState = MutableStateFlow(LoanSimulatorUiState())
    val uiState: StateFlow<LoanSimulatorUiState> = _uiState.asStateFlow()

    /**
     * Actualiza el monto del préstamo.
     */
    fun updateAmount(amount: String) {
        _uiState.update { it.copy(amount = amount, error = null) }
    }

    /**
     * Actualiza la tasa de interés.
     */
    fun updateRate(rate: Double) {
        _uiState.update { it.copy(rate = rate, error = null) }
    }

    /**
     * Actualiza el plazo en meses.
     */
    fun updateTermMonths(termMonths: Int) {
        _uiState.update { it.copy(termMonths = termMonths, error = null) }
    }

    /**
     * RF07: Simula el préstamo con los parámetros actuales.
     */
    fun simulateLoan() {
        viewModelScope.launch {
            _uiState.update { it.copy(isSimulating = true, error = null) }

            try {
                val amount = _uiState.value.amount.toDoubleOrNull()

                if (amount == null || amount <= 0) {
                    _uiState.update {
                        it.copy(
                            isSimulating = false,
                            error = "Por favor ingresa un monto válido"
                        )
                    }
                    return@launch
                }

                val result = simulateLoanUseCase(
                    amount = amount,
                    annualRate = _uiState.value.rate,
                    termMonths = _uiState.value.termMonths
                )

                if (result.isSuccess) {
                    _uiState.update {
                        it.copy(
                            simulation = result.getOrThrow(),
                            isSimulating = false,
                            error = null
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            isSimulating = false,
                            error = result.exceptionOrNull()?.message ?: "Error en la simulación"
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isSimulating = false,
                        error = "Error: ${e.message}"
                    )
                }
            }
        }
    }

    /**
     * RF08 - HU10: Solicita el préstamo simulado.
     */
    fun requestLoan(purpose: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isRequestingLoan = true, error = null) }

            val userId = auth.currentUser?.uid
            if (userId == null) {
                _uiState.update {
                    it.copy(
                        isRequestingLoan = false,
                        error = "Usuario no autenticado"
                    )
                }
                return@launch
            }

            try {
                val amount = _uiState.value.amount.toDoubleOrNull()
                val simulation = _uiState.value.simulation

                if (amount == null || simulation == null) {
                    _uiState.update {
                        it.copy(
                            isRequestingLoan = false,
                            error = "Por favor simula el préstamo primero"
                        )
                    }
                    return@launch
                }

                val loan = Loan(
                    userId = userId,
                    amount = amount,
                    rate = _uiState.value.rate,
                    termMonths = _uiState.value.termMonths,
                    monthlyPayment = simulation.monthlyPayment,
                    totalToPay = simulation.totalToPay,
                    status = LoanStatus.PENDING,
                    purpose = purpose,
                    createdAt = System.currentTimeMillis()
                )

                val result = loanRepository.requestLoan(loan)

                if (result.isSuccess) {
                    _uiState.update {
                        it.copy(
                            isRequestingLoan = false,
                            loanRequestSuccess = true,
                            error = null
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            isRequestingLoan = false,
                            error = result.exceptionOrNull()?.message ?: "Error al solicitar préstamo"
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isRequestingLoan = false,
                        error = "Error: ${e.message}"
                    )
                }
            }
        }
    }

    /**
     * Limpia el estado de éxito de solicitud.
     */
    fun clearRequestSuccess() {
        _uiState.update { it.copy(loanRequestSuccess = false) }
    }

    /**
     * Limpia el error.
     */
    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}