package com.example.bankingapp.data.mapper

import com.example.bankingapp.data.dto.AccountDto
import com.example.bankingapp.domain.model.Account
import com.google.firebase.Timestamp

/**
 * Convierte entre el DTO de Firebase y el modelo de dominio.
 * Mantiene la separación de responsabilidades entre capas.
 */
fun AccountDto.toDomain(): Account {
    return Account(
        userId = this.userId,
        balance = this.balance,
        currency = this.currency,
        accountNumber = this.accountNumber,
        accountType = this.accountType,
        createdAt = this.createdAt?.toDate()?.time ?: System.currentTimeMillis(),
        updatedAt = this.updatedAt?.toDate()?.time ?: System.currentTimeMillis()
    )
}

fun Account.toDto(): AccountDto {
    return AccountDto(
        userId = this.userId,
        balance = this.balance,
        currency = this.currency,
        accountNumber = this.accountNumber,
        accountType = this.accountType,
        createdAt = Timestamp(java.util.Date(this.createdAt)),
        updatedAt = Timestamp(java.util.Date(this.updatedAt))
    )
}