package com.example.bankingapp.data.repository

import com.example.bankingapp.data.dto.LoanDto
import com.example.bankingapp.data.dto.NotificationDto
import com.example.bankingapp.data.mapper.toDomain
import com.example.bankingapp.data.mapper.toDto
import com.example.bankingapp.domain.model.Loan
import com.example.bankingapp.domain.model.enums.LoanStatus
import com.example.bankingapp.domain.repository.LoanRepository
import com.example.bankingapp.domain.usecase.loan.SimulateLoanUseCase
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await

/**
 * Implementación del repositorio de préstamos con Firebase Firestore.
 *
 * RF07, RF08, RF09: Simulación, solicitud e historial de préstamos.
 */
class LoanRepositoryImpl(
    private val firestore: FirebaseFirestore
) : LoanRepository {

    companion object {
        private const val LOANS_COLLECTION = "loans"
        private const val NOTIFICATIONS_COLLECTION = "notifications"
    }

    private val simulateLoanUseCase = SimulateLoanUseCase()

    /**
     * RF07: Simula un préstamo y devuelve cuota mensual y total a pagar.
     */
    override suspend fun simulateLoan(
        amount: Double,
        interestRate: Double,
        termMonths: Int
    ): Result<Pair<Double, Double>> {
        return try {
            val simulationResult = simulateLoanUseCase(amount, interestRate, termMonths)

            if (simulationResult.isSuccess) {
                val simulation = simulationResult.getOrThrow()
                Result.success(Pair(simulation.monthlyPayment, simulation.totalToPay))
            } else {
                Result.failure(simulationResult.exceptionOrNull() ?: Exception("Error en simulación"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * RF08 - HU10: Solicita un nuevo préstamo.
     * Crea el documento en Firestore y genera una notificación.
     */
    override suspend fun requestLoan(loan: Loan): Result<String> {
        return try {
            // Simular para obtener cuota y total
            val simulation = simulateLoanUseCase(loan.amount, loan.rate, loan.termMonths)

            if (simulation.isFailure) {
                return Result.failure(simulation.exceptionOrNull() ?: Exception("Error en simulación"))
            }

            val simulationData = simulation.getOrThrow()

            // Crear préstamo con datos calculados
            val loanWithCalculations = loan.copy(
                monthlyPayment = simulationData.monthlyPayment,
                totalToPay = simulationData.totalToPay,
                status = LoanStatus.PENDING,
                createdAt = System.currentTimeMillis()
            )

            val loanDto = loanWithCalculations.toDto()

            // Guardar en Firestore
            val documentRef = firestore.collection(LOANS_COLLECTION)
                .add(loanDto)
                .await()

            val loanId = documentRef.id

            // Crear notificación de solicitud recibida
            createLoanNotification(
                userId = loan.userId,
                loanId = loanId,
                type = "GENERAL",
                title = "Solicitud de Préstamo Recibida",
                message = "Tu solicitud de préstamo por $${String.format("%.2f", loan.amount)} ha sido recibida y está en proceso de revisión."
            )

            Result.success(loanId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * RF09 - HU11: Obtiene el historial de préstamos del usuario.
     */
    override suspend fun getLoanHistory(userId: String): Result<List<Loan>> {
        return try {
            val snapshot = firestore.collection(LOANS_COLLECTION)
                .whereEqualTo("userId", userId)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .await()

            val loans = snapshot.documents.mapNotNull { doc ->
                doc.toObject(LoanDto::class.java)?.copy(id = doc.id)?.toDomain()
            }

            Result.success(loans)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Obtiene un préstamo específico por ID.
     */
    override suspend fun getLoanById(loanId: String): Result<Loan> {
        return try {
            val snapshot = firestore.collection(LOANS_COLLECTION)
                .document(loanId)
                .get()
                .await()

            if (snapshot.exists()) {
                val loan = snapshot.toObject(LoanDto::class.java)
                    ?.copy(id = snapshot.id)
                    ?.toDomain()

                Result.success(loan ?: throw Exception("Error al parsear préstamo"))
            } else {
                Result.failure(Exception("Préstamo no encontrado"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Obtiene préstamos filtrados por estado.
     */
    override suspend fun getLoansByStatus(
        userId: String,
        status: LoanStatus
    ): Result<List<Loan>> {
        return try {
            val statusString = when (status) {
                LoanStatus.PENDING -> "PENDING"
                LoanStatus.APPROVED -> "APPROVED"
                LoanStatus.REJECTED -> "REJECTED"
                LoanStatus.ACTIVE -> "ACTIVE"
                LoanStatus.COMPLETED -> "COMPLETED"
                LoanStatus.CANCELLED -> "CANCELLED"
            }

            val snapshot = firestore.collection(LOANS_COLLECTION)
                .whereEqualTo("userId", userId)
                .whereEqualTo("status", statusString)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .await()

            val loans = snapshot.documents.mapNotNull { doc ->
                doc.toObject(LoanDto::class.java)?.copy(id = doc.id)?.toDomain()
            }

            Result.success(loans)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Actualiza el estado de un préstamo.
     * Útil para simular aprobación/rechazo (modo admin).
     */
    suspend fun updateLoanStatus(
        loanId: String,
        status: LoanStatus,
        rejectionReason: String? = null
    ): Result<Unit> {
        return try {
            val statusString = when (status) {
                LoanStatus.PENDING -> "PENDING"
                LoanStatus.APPROVED -> "APPROVED"
                LoanStatus.REJECTED -> "REJECTED"
                LoanStatus.ACTIVE -> "ACTIVE"
                LoanStatus.COMPLETED -> "COMPLETED"
                LoanStatus.CANCELLED -> "CANCELLED"
            }

            val updates = mutableMapOf<String, Any>(
                "status" to statusString,
                "updatedAt" to Timestamp.now(),
                "processedAt" to Timestamp.now()
            )

            if (rejectionReason != null) {
                updates["rejectionReason"] = rejectionReason
            }

            firestore.collection(LOANS_COLLECTION)
                .document(loanId)
                .update(updates)
                .await()

            // Obtener datos del préstamo para la notificación
            val loanSnapshot = firestore.collection(LOANS_COLLECTION)
                .document(loanId)
                .get()
                .await()

            val loan = loanSnapshot.toObject(LoanDto::class.java)

            // Crear notificación según el estado
            if (loan != null) {
                when (status) {
                    LoanStatus.APPROVED -> {
                        createLoanNotification(
                            userId = loan.userId,
                            loanId = loanId,
                            type = "LOAN_APPROVED",
                            title = "¡Préstamo Aprobado!",
                            message = "Tu préstamo por $${String.format("%.2f", loan.amount)} ha sido aprobado."
                        )
                    }
                    LoanStatus.REJECTED -> {
                        createLoanNotification(
                            userId = loan.userId,
                            loanId = loanId,
                            type = "LOAN_REJECTED",
                            title = "Préstamo Rechazado",
                            message = "Tu solicitud de préstamo ha sido rechazada. ${rejectionReason ?: ""}"
                        )
                    }
                    else -> {}
                }
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Crea una notificación relacionada con un préstamo.
     */
    private suspend fun createLoanNotification(
        userId: String,
        loanId: String,
        type: String,
        title: String,
        message: String
    ) {
        try {
            val notification = NotificationDto(
                userId = userId,
                type = type,
                title = title,
                message = message,
                timestamp = Timestamp.now(),
                isRead = false,
                relatedLoanId = loanId
            )

            firestore.collection(NOTIFICATIONS_COLLECTION)
                .add(notification)
                .await()
        } catch (e: Exception) {
            // Log error pero no fallar la operación principal
            android.util.Log.e("LoanRepository", "Error creando notificación", e)
        }
    }
}