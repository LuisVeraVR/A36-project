package com.example.bankingapp.data.dto

import com.google.firebase.Timestamp
import com.google.firebase.firestore.PropertyName

/**
 * DTO para notificaciones en Firestore.
 */
data class NotificationDto(
    @PropertyName("id") val id: String = "",
    @PropertyName("userId") val userId: String = "",
    @PropertyName("type") val type: String = "GENERAL",
    @PropertyName("title") val title: String = "",
    @PropertyName("message") val message: String = "",
    @PropertyName("timestamp") val timestamp: Timestamp? = null,
    @PropertyName("isRead") val isRead: Boolean = false,
    @PropertyName("relatedLoanId") val relatedLoanId: String? = null,
    @PropertyName("relatedTransactionId") val relatedTransactionId: String? = null
)