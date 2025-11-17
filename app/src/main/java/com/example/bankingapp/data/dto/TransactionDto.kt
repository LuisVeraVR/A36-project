package com.example.bankingapp.data.dto

import com.google.firebase.Timestamp
import com.google.firebase.firestore.PropertyName

/**
 * DTO para transacciones en Firestore.
 * Los campos coinciden con la estructura de la colección 'transactions'.
 */
data class TransactionDto(
    @PropertyName("id") val id: String = "",
    @PropertyName("userId") val userId: String = "",
    @PropertyName("amount") val amount: Double = 0.0,
    @PropertyName("type") val type: String = "", // "INCOME" o "EXPENSE"
    @PropertyName("category") val category: String = "",
    @PropertyName("description") val description: String = "",
    @PropertyName("reference") val reference: String = "",
    @PropertyName("date") val date: Timestamp? = null,
    @PropertyName("createdAt") val createdAt: Timestamp? = null,
    @PropertyName("balanceAfter") val balanceAfter: Double = 0.0
)