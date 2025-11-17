package com.example.bankingapp.data.dto

import com.google.firebase.Timestamp
import com.google.firebase.firestore.PropertyName

/**
 * DTO para préstamos en Firestore.
 * Estructura de la colección 'loans'.
 */
data class LoanDto(
    @PropertyName("id") val id: String = "",
    @PropertyName("userId") val userId: String = "",
    @PropertyName("amount") val amount: Double = 0.0,
    @PropertyName("rate") val rate: Double = 0.0,
    @PropertyName("termMonths") val termMonths: Int = 0,
    @PropertyName("monthlyPayment") val monthlyPayment: Double = 0.0,
    @PropertyName("totalToPay") val totalToPay: Double = 0.0,
    @PropertyName("status") val status: String = "PENDING",
    @PropertyName("purpose") val purpose: String = "",
    @PropertyName("notes") val notes: String? = null,
    @PropertyName("createdAt") val createdAt: Timestamp? = null,
    @PropertyName("updatedAt") val updatedAt: Timestamp? = null,
    @PropertyName("processedAt") val processedAt: Timestamp? = null,
    @PropertyName("rejectionReason") val rejectionReason: String? = null
)