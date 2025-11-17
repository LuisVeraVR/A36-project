package com.example.bankingapp.data.mapper

import com.example.bankingapp.data.dto.LoanDto
import com.example.bankingapp.domain.model.Loan
import com.example.bankingapp.domain.model.enums.LoanStatus
import com.google.firebase.Timestamp

/**
 * Mappers para convertir entre Loan (dominio) y LoanDto (Firebase).
 */
fun LoanDto.toDomain(): Loan {
    return Loan(
        id = this.id,
        userId = this.userId,
        amount = this.amount,
        rate = this.rate,
        termMonths = this.termMonths,
        monthlyPayment = this.monthlyPayment,
        totalToPay = this.totalToPay,
        status = when (this.status.uppercase()) {
            "PENDING", "PENDIENTE" -> LoanStatus.PENDING
            "APPROVED", "APROBADO" -> LoanStatus.APPROVED
            "REJECTED", "RECHAZADO" -> LoanStatus.REJECTED
            "ACTIVE", "ACTIVO" -> LoanStatus.ACTIVE
            "COMPLETED", "COMPLETADO" -> LoanStatus.COMPLETED
            "CANCELLED", "CANCELADO" -> LoanStatus.CANCELLED
            else -> LoanStatus.PENDING
        },
        purpose = this.purpose,
        notes = this.notes,
        createdAt = this.createdAt?.toDate()?.time ?: System.currentTimeMillis(),
        updatedAt = this.updatedAt?.toDate()?.time,
        processedAt = this.processedAt?.toDate()?.time,
        rejectionReason = this.rejectionReason
    )
}

fun Loan.toDto(): LoanDto {
    return LoanDto(
        id = this.id,
        userId = this.userId,
        amount = this.amount,
        rate = this.rate,
        termMonths = this.termMonths,
        monthlyPayment = this.monthlyPayment,
        totalToPay = this.totalToPay,
        status = when (this.status) {
            LoanStatus.PENDING -> "PENDING"
            LoanStatus.APPROVED -> "APPROVED"
            LoanStatus.REJECTED -> "REJECTED"
            LoanStatus.ACTIVE -> "ACTIVE"
            LoanStatus.COMPLETED -> "COMPLETED"
            LoanStatus.CANCELLED -> "CANCELLED"
        },
        purpose = this.purpose,
        notes = this.notes,
        createdAt = Timestamp(java.util.Date(this.createdAt)),
        updatedAt = this.updatedAt?.let { Timestamp(java.util.Date(it)) },
        processedAt = this.processedAt?.let { Timestamp(java.util.Date(it)) },
        rejectionReason = this.rejectionReason
    )
}