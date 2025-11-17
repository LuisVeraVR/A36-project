package com.example.bankingapp.data.mapper

import com.example.bankingapp.data.dto.TransactionDto
import com.example.bankingapp.domain.model.Transaction
import com.example.bankingapp.domain.model.enums.TransactionType
import com.google.firebase.Timestamp

/**
 * Mappers para convertir entre Transaction (dominio) y TransactionDto (Firebase).
 */
fun TransactionDto.toDomain(): Transaction {
    return Transaction(
        id = this.id,
        userId = this.userId,
        amount = this.amount,
        type = when (this.type.uppercase()) {
            "INCOME", "INGRESO" -> TransactionType.INCOME
            "EXPENSE", "EGRESO" -> TransactionType.EXPENSE
            else -> TransactionType.EXPENSE
        },
        category = this.category,
        description = this.description,
        reference = this.reference,
        timestamp = this.date?.toDate()?.time ?: System.currentTimeMillis(),
        balanceAfter = this.balanceAfter
    )
}

fun Transaction.toDto(): TransactionDto {
    return TransactionDto(
        id = this.id,
        userId = this.userId,
        amount = this.amount,
        type = when (this.type) {
            TransactionType.INCOME -> "INCOME"
            TransactionType.EXPENSE -> "EXPENSE"
        },
        category = this.category,
        description = this.description,
        reference = this.reference,
        date = Timestamp(java.util.Date(this.timestamp)),
        createdAt = Timestamp(java.util.Date(this.timestamp)),
        balanceAfter = this.balanceAfter
    )
}