package com.example.bankingapp.ui.loans.simulator

import com.example.bankingapp.domain.usecase.loan.SimulateLoanUseCase

/**
 * Estado de la pantalla del simulador de préstamos.
 */
data class LoanSimulatorUiState(
    val amount: String = "",
    val rate: Double = 12.0,  // Tasa por defecto: 12% anual
    val termMonths: Int = 12,  // Plazo por defecto: 12 meses
    val simulation: SimulateLoanUseCase.LoanSimulation? = null,
    val isSimulating: Boolean = false,
    val error: String? = null,
    val isRequestingLoan: Boolean = false,
    val loanRequestSuccess: Boolean = false
)