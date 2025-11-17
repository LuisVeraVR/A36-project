package com.example.bankingapp.data.mapper

import com.example.bankingapp.data.dto.NotificationDto
import com.example.bankingapp.domain.model.Notification
import com.example.bankingapp.domain.model.enums.NotificationType
import com.google.firebase.Timestamp

/**
 * Mappers para convertir entre Notification (dominio) y NotificationDto (Firebase).
 */
fun NotificationDto.toDomain(): Notification {
    return Notification(
        id = this.id,
        userId = this.userId,
        type = when (this.type.uppercase()) {
            "LOAN_APPROVED" -> NotificationType.LOAN_APPROVED
            "LOAN_REJECTED" -> NotificationType.LOAN_REJECTED
            "PAYMENT_RECEIVED" -> NotificationType.PAYMENT_RECEIVED
            "PAYMENT_DUE" -> NotificationType.PAYMENT_DUE
            "ACCOUNT_UPDATE" -> NotificationType.ACCOUNT_UPDATE
            else -> NotificationType.GENERAL
        },
        title = this.title,
        message = this.message,
        timestamp = this.timestamp?.toDate()?.time ?: System.currentTimeMillis(),
        isRead = this.isRead,
        relatedLoanId = this.relatedLoanId,
        relatedTransactionId = this.relatedTransactionId
    )
}

fun Notification.toDto(): NotificationDto {
    return NotificationDto(
        id = this.id,
        userId = this.userId,
        type = when (this.type) {
            NotificationType.LOAN_APPROVED -> "LOAN_APPROVED"
            NotificationType.LOAN_REJECTED -> "LOAN_REJECTED"
            NotificationType.PAYMENT_RECEIVED -> "PAYMENT_RECEIVED"
            NotificationType.PAYMENT_DUE -> "PAYMENT_DUE"
            NotificationType.ACCOUNT_UPDATE -> "ACCOUNT_UPDATE"
            NotificationType.GENERAL -> "GENERAL"
        },
        title = this.title,
        message = this.message,
        timestamp = Timestamp(java.util.Date(this.timestamp)),
        isRead = this.isRead,
        relatedLoanId = this.relatedLoanId,
        relatedTransactionId = this.relatedTransactionId
    )
}