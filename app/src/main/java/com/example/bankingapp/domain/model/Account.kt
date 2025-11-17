package com.example.bankingapp.domain.model

/**
 * Modelo de dominio que representa la cuenta bancaria del usuario.
 * Este modelo es independiente de Firebase (Clean Architecture).
 */
data class Account(
    val userId: String = "",
    val balance: Double = 0.0,
    val currency: String = "USD", // USD, EUR, COP, etc.
    val accountNumber: String = "",
    val accountType: String = "SAVINGS", // SAVINGS, CHECKING
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)