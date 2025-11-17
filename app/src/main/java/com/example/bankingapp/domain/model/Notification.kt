package com.example.bankingapp.domain.model

import com.example.bankingapp.domain.model.enums.NotificationType

/**
 * Modelo de dominio para notificaciones.
 */
data class Notification(
    val id: String = "",
    val userId: String = "",
    val type: NotificationType = NotificationType.GENERAL,
    val title: String = "",
    val message: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false,
    val relatedLoanId: String? = null,
    val relatedTransactionId: String? = null
)