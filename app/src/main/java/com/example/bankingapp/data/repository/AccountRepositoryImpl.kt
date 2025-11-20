package com.example.bankingapp.data.repository

import android.util.Log
import com.example.bankingapp.data.dto.AccountDto
import com.example.bankingapp.domain.model.Account
import com.example.bankingapp.domain.model.User
import com.example.bankingapp.domain.repository.AccountRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

/**
 * Implementación del repositorio de cuentas.
 */
class AccountRepositoryImpl(
    private val firestore: FirebaseFirestore
) : AccountRepository {

    companion object {
        private const val ACCOUNTS_COLLECTION = "accounts"
        private const val USERS_COLLECTION = "users"
        private const val TAG = "AccountRepository"
    }

    override suspend fun getAccount(userId: String): Result<Account> {
        return try {
            Log.d(TAG, "Obteniendo cuenta para userId: $userId")

            val document = firestore.collection(ACCOUNTS_COLLECTION)
                .document(userId)
                .get()
                .await()

            if (!document.exists()) {
                Log.w(TAG, "Cuenta no encontrada para userId: $userId")
                return Result.failure(Exception("Cuenta no encontrada"))
            }

            val accountDto = document.toObject(AccountDto::class.java)
                ?: return Result.failure(Exception("Error al parsear cuenta"))

            val account = accountDto.toDomain()
            Log.d(TAG, "Cuenta obtenida: ${account.accountNumber}, Balance: ${account.balance}")

            Result.success(account)
        } catch (e: Exception) {
            Log.e(TAG, "Error obteniendo cuenta", e)
            Result.failure(Exception("Error al obtener cuenta: ${e.localizedMessage}"))
        }
    }

    override suspend fun getAccountBalance(userId: String): Result<Double> {
        return try {
            Log.d(TAG, "Obteniendo balance para userId: $userId")

            val document = firestore.collection(ACCOUNTS_COLLECTION)
                .document(userId)
                .get()
                .await()

            if (!document.exists()) {
                Log.w(TAG, "Cuenta no encontrada para userId: $userId")
                return Result.failure(Exception("Cuenta no encontrada"))
            }

            val balance = document.getDouble("balance") ?: 0.0
            Log.d(TAG, "Balance obtenido: $balance")

            Result.success(balance)
        } catch (e: Exception) {
            Log.e(TAG, "Error obteniendo balance", e)
            Result.failure(Exception("Error al obtener balance: ${e.localizedMessage}"))
        }
    }

    override suspend fun updateBalance(userId: String, newBalance: Double): Result<Unit> {
        return try {
            Log.d(TAG, "Actualizando balance para userId: $userId, nuevo balance: $newBalance")

            firestore.collection(ACCOUNTS_COLLECTION)
                .document(userId)
                .update("balance", newBalance)
                .await()

            Log.d(TAG, "Balance actualizado exitosamente")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error actualizando balance", e)
            Result.failure(Exception("Error al actualizar balance: ${e.localizedMessage}"))
        }
    }

    override suspend fun getRecentIncome(userId: String, limit: Int): Result<List<Double>> {
        return try {
            // Por ahora retornar lista vacía para evitar errores de índices
            Log.d(TAG, "getRecentIncome llamado - retornando lista vacía")
            Result.success(emptyList())
        } catch (e: Exception) {
            Log.e(TAG, "Error en getRecentIncome", e)
            Result.failure(e)
        }
    }

    override suspend fun getUserProfile(userId: String): Result<User> {
        return try {
            Log.d(TAG, "Obteniendo perfil de usuario para userId: $userId")

            val document = firestore.collection(USERS_COLLECTION)
                .document(userId)
                .get()
                .await()

            if (!document.exists()) {
                Log.w(TAG, "Usuario no encontrado: $userId")
                return Result.failure(Exception("Usuario no encontrado"))
            }

            val user = User(
                id = document.id,
                email = document.getString("email") ?: "",
                fullName = document.getString("fullName") ?: "",
                createdAt = document.getTimestamp("createdAt")?.toDate()?.time ?: 0L
            )

            Log.d(TAG, "Perfil de usuario obtenido: ${user.fullName}")
            Result.success(user)
        } catch (e: Exception) {
            Log.e(TAG, "Error obteniendo perfil de usuario", e)
            Result.failure(Exception("Error al obtener perfil: ${e.localizedMessage}"))
        }
    }

    override suspend fun updateUserProfile(user: User): Result<Unit> {
        return try {
            Log.d(TAG, "Actualizando perfil de usuario: ${user.id}")

            val updates = hashMapOf<String, Any>(
                "fullName" to user.fullName,
                "email" to user.email
            )

            firestore.collection(USERS_COLLECTION)
                .document(user.id)
                .update(updates)
                .await()

            Log.d(TAG, "Perfil de usuario actualizado exitosamente")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error actualizando perfil de usuario", e)
            Result.failure(Exception("Error al actualizar perfil: ${e.localizedMessage}"))
        }
    }

    override suspend fun getAccountByAccountNumber(accountNumber: String): Result<Account> {
        return try {
            Log.d(TAG, "Buscando cuenta por número: $accountNumber")

            val querySnapshot = firestore.collection(ACCOUNTS_COLLECTION)
                .whereEqualTo("accountNumber", accountNumber)
                .limit(1)
                .get()
                .await()

            if (querySnapshot.isEmpty) {
                Log.w(TAG, "Cuenta no encontrada con número: $accountNumber")
                return Result.failure(Exception("Cuenta no encontrada"))
            }

            val document = querySnapshot.documents[0]
            val accountDto = document.toObject(AccountDto::class.java)
                ?: return Result.failure(Exception("Error al parsear cuenta"))

            val account = accountDto.toDomain()
            Log.d(TAG, "Cuenta encontrada: ${account.userId}")

            Result.success(account)
        } catch (e: Exception) {
            Log.e(TAG, "Error buscando cuenta por número", e)
            Result.failure(Exception("Error al buscar cuenta: ${e.localizedMessage}"))
        }
    }

    override suspend fun transferMoney(
        fromUserId: String,
        toAccountNumber: String,
        amount: Double,
        description: String
    ): Result<Unit> {
        return try {
            Log.d(TAG, "Iniciando transferencia de $amount desde $fromUserId a cuenta $toAccountNumber")

            // Validar monto
            if (amount <= 0) {
                return Result.failure(Exception("El monto debe ser mayor a 0"))
            }

            // Obtener cuenta origen
            val fromAccountResult = getAccount(fromUserId)
            if (fromAccountResult.isFailure) {
                return Result.failure(Exception("Error al obtener cuenta de origen"))
            }
            val fromAccount = fromAccountResult.getOrThrow()

            // Verificar saldo suficiente
            if (fromAccount.balance < amount) {
                return Result.failure(Exception("Saldo insuficiente"))
            }

            // Obtener cuenta destino
            val toAccountResult = getAccountByAccountNumber(toAccountNumber)
            if (toAccountResult.isFailure) {
                return Result.failure(Exception("Cuenta destino no encontrada"))
            }
            val toAccount = toAccountResult.getOrThrow()

            // No permitir transferirse a sí mismo
            if (fromAccount.userId == toAccount.userId) {
                return Result.failure(Exception("No puedes transferir a tu propia cuenta"))
            }

            // Calcular nuevos balances
            val newFromBalance = fromAccount.balance - amount
            val newToBalance = toAccount.balance + amount

            // Ejecutar transferencia usando batch
            val batch = firestore.batch()

            // Actualizar balance origen
            val fromAccountRef = firestore.collection(ACCOUNTS_COLLECTION).document(fromUserId)
            batch.update(fromAccountRef, "balance", newFromBalance)
            batch.update(fromAccountRef, "updatedAt", com.google.firebase.Timestamp.now())

            // Actualizar balance destino
            val toAccountRef = firestore.collection(ACCOUNTS_COLLECTION).document(toAccount.userId)
            batch.update(toAccountRef, "balance", newToBalance)
            batch.update(toAccountRef, "updatedAt", com.google.firebase.Timestamp.now())

            // Crear transacción de salida (EXPENSE)
            val fromTransactionRef = firestore.collection("transactions").document()
            val fromTransactionData = hashMapOf(
                "id" to fromTransactionRef.id,
                "userId" to fromUserId,
                "amount" to amount,
                "type" to "EXPENSE",
                "category" to "Transferencia",
                "description" to description.ifEmpty { "Transferencia a cuenta $toAccountNumber" },
                "reference" to toAccountNumber,
                "date" to com.google.firebase.Timestamp.now(),
                "createdAt" to com.google.firebase.Timestamp.now(),
                "balanceAfter" to newFromBalance
            )
            batch.set(fromTransactionRef, fromTransactionData)

            // Crear transacción de entrada (INCOME)
            val toTransactionRef = firestore.collection("transactions").document()
            val toTransactionData = hashMapOf(
                "id" to toTransactionRef.id,
                "userId" to toAccount.userId,
                "amount" to amount,
                "type" to "INCOME",
                "category" to "Transferencia",
                "description" to "Transferencia desde cuenta ${fromAccount.accountNumber}",
                "reference" to fromAccount.accountNumber,
                "date" to com.google.firebase.Timestamp.now(),
                "createdAt" to com.google.firebase.Timestamp.now(),
                "balanceAfter" to newToBalance
            )
            batch.set(toTransactionRef, toTransactionData)

            // Ejecutar batch
            batch.commit().await()

            Log.d(TAG, "Transferencia completada exitosamente")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error en transferencia", e)
            Result.failure(Exception("Error al transferir: ${e.localizedMessage}"))
        }
    }
}