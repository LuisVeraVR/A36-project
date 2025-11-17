package com.example.bankingapp.data.dto

import com.example.bankingapp.domain.model.Account
import com.google.firebase.Timestamp

/**
 * Data Transfer Object para Account en Firestore.
 */
data class AccountDto(
    val userId: String = "",
    val balance: Double = 0.0,
    val currency: String = "USD",
    val accountNumber: String = "",
    val accountType: String = "SAVINGS",
    val createdAt: Timestamp? = null,
    val updatedAt: Timestamp? = null
) {
    /**
     * Convierte AccountDto a Account (modelo de dominio).
     */
    fun toDomain(): Account {
        return Account(
            userId = this.userId,
            balance = this.balance,
            currency = this.currency,
            accountNumber = this.accountNumber,
            accountType = this.accountType,
            createdAt = this.createdAt?.toDate()?.time ?: 0L,
            updatedAt = this.updatedAt?.toDate()?.time ?: 0L
        )
    }
}