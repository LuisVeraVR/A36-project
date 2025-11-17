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
}