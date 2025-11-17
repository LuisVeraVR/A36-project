package com.example.bankingapp.domain.repository

import com.example.bankingapp.domain.model.Transaction
import com.example.bankingapp.domain.model.enums.TransactionType

interface TransactionRepository {
    suspend fun getTransactions(
        userId: String,
        startDate: Long? = null,
        endDate: Long? = null,
        type: TransactionType? = null
    ): Result<List<Transaction>>

    suspend fun getTransactionById(transactionId: String): Result<Transaction>

    suspend fun createTransaction(transaction: Transaction): Result<String>

    suspend fun getTransactionsByCategory(
        userId: String,
        category: String
    ): Result<List<Transaction>>
}