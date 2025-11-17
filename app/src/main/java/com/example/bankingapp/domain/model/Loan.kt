package com.example.bankingapp.domain.model

import com.example.bankingapp.domain.model.enums.LoanStatus

/**
 * Modelo de dominio para préstamos.
 *
 * Notas sobre la tasa de interés:
 * - rate: Tasa de interés ANUAL expresada como porcentaje (ej: 12.5 = 12.5% anual)
 * - Para cálculos mensuales, se divide entre 12
 */
data class Loan(
    val id: String = "",
    val userId: String = "",
    val amount: Double = 0.0,
    val rate: Double = 0.0,
    val termMonths: Int = 0,
    val monthlyPayment: Double = 0.0,
    val totalToPay: Double = 0.0,
    val status: LoanStatus = LoanStatus.PENDING,
    val purpose: String = "",
    val notes: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long? = null,
    val processedAt: Long? = null,
    val rejectionReason: String? = null
)