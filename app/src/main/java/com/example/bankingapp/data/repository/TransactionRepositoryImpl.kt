package com.example.bankingapp.data.repository

import com.example.bankingapp.data.dto.TransactionDto
import com.example.bankingapp.data.mapper.toDomain
import com.example.bankingapp.data.mapper.toDto
import com.example.bankingapp.domain.model.Transaction
import com.example.bankingapp.domain.model.enums.TransactionType
import com.example.bankingapp.domain.repository.TransactionRepository
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await

/**
 * Implementación del repositorio de transacciones con Firebase Firestore.
 *
 * Estructura en Firestore:
 * - Colección: "transactions"
 * - Document ID: autogenerado por Firestore
 * - Campos: userId, amount, type, category, description, reference, date, createdAt, balanceAfter
 */
class TransactionRepositoryImpl(
    private val firestore: FirebaseFirestore
) : TransactionRepository {

    companion object {
        private const val TRANSACTIONS_COLLECTION = "transactions"
    }

    /**
     * RF05 - HU06: Obtiene todas las transacciones del usuario con filtros opcionales.
     *
     * @param userId ID del usuario
     * @param startDate timestamp de inicio (opcional)
     * @param endDate timestamp de fin (opcional)
     * @param type tipo de transacción INCOME/EXPENSE (opcional)
     */
    override suspend fun getTransactions(
        userId: String,
        startDate: Long?,
        endDate: Long?,
        type: TransactionType?
    ): Result<List<Transaction>> {
        return try {
            // Construir query base
            var query: Query = firestore.collection(TRANSACTIONS_COLLECTION)
                .whereEqualTo("userId", userId)
                .orderBy("date", Query.Direction.DESCENDING)

            // Aplicar filtro de tipo si existe
            if (type != null) {
                val typeString = when (type) {
                    TransactionType.INCOME -> "INCOME"
                    TransactionType.EXPENSE -> "EXPENSE"
                }
                query = query.whereEqualTo("type", typeString)
            }

            // Aplicar filtro de fecha inicial
            if (startDate != null) {
                query = query.whereGreaterThanOrEqualTo("date", Timestamp(java.util.Date(startDate)))
            }

            // Aplicar filtro de fecha final
            if (endDate != null) {
                query = query.whereLessThanOrEqualTo("date", Timestamp(java.util.Date(endDate)))
            }

            val snapshot = query.get().await()

            val transactions = snapshot.documents.mapNotNull { doc ->
                doc.toObject(TransactionDto::class.java)?.copy(id = doc.id)?.toDomain()
            }

            Result.success(transactions)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * RF06 - HU08: Obtiene una transacción específica por su ID.
     */
    override suspend fun getTransactionById(transactionId: String): Result<Transaction> {
        return try {
            val snapshot = firestore.collection(TRANSACTIONS_COLLECTION)
                .document(transactionId)
                .get()
                .await()

            if (snapshot.exists()) {
                val transactionDto = snapshot.toObject(TransactionDto::class.java)
                    ?.copy(id = snapshot.id)
                Result.success(transactionDto?.toDomain() ?: throw Exception("Transaction not found"))
            } else {
                Result.failure(Exception("Transaction with ID $transactionId not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Crea una nueva transacción en Firestore.
     * Retorna el ID generado del documento.
     */
    override suspend fun createTransaction(transaction: Transaction): Result<String> {
        return try {
            val transactionDto = transaction.toDto()
            val documentRef = firestore.collection(TRANSACTIONS_COLLECTION)
                .add(transactionDto)
                .await()

            Result.success(documentRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Obtiene transacciones filtradas por categoría.
     */
    override suspend fun getTransactionsByCategory(
        userId: String,
        category: String
    ): Result<List<Transaction>> {
        return try {
            val snapshot = firestore.collection(TRANSACTIONS_COLLECTION)
                .whereEqualTo("userId", userId)
                .whereEqualTo("category", category)
                .orderBy("date", Query.Direction.DESCENDING)
                .get()
                .await()

            val transactions = snapshot.documents.mapNotNull { doc ->
                doc.toObject(TransactionDto::class.java)?.copy(id = doc.id)?.toDomain()
            }

            Result.success(transactions)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}