package com.example.bankingapp.domain.model

import com.example.bankingapp.domain.model.enums.TransactionType

data class Transaction(
    val id: String = "",
    val userId: String = "",
    val amount: Double = 0.0,
    val type: TransactionType = TransactionType.INCOME,
    val category: String = "",
    val description: String = "",
    val reference: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val balanceAfter: Double = 0.0
)