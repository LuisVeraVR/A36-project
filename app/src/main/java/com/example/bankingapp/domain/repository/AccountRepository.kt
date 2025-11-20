package com.example.bankingapp.domain.repository

import com.example.bankingapp.domain.model.Account
import com.example.bankingapp.domain.model.User

/**
 * Repositorio para operaciones de cuenta bancaria.
 */
interface AccountRepository {
    /**
     * Obtiene la cuenta completa del usuario.
     */
    suspend fun getAccount(userId: String): Result<Account>

    /**
     * Obtiene solo el balance de la cuenta.
     */
    suspend fun getAccountBalance(userId: String): Result<Double>

    /**
     * Actualiza el balance de la cuenta.
     */
    suspend fun updateBalance(userId: String, newBalance: Double): Result<Unit>

    /**
     * Obtiene los ingresos recientes.
     */
    suspend fun getRecentIncome(userId: String, limit: Int): Result<List<Double>>

    /**
     * Obtiene el perfil del usuario.
     */
    suspend fun getUserProfile(userId: String): Result<User>

    /**
     * Actualiza el perfil del usuario.
     */
    suspend fun updateUserProfile(user: User): Result<Unit>

    /**
     * Busca una cuenta por número de cuenta.
     */
    suspend fun getAccountByAccountNumber(accountNumber: String): Result<Account>

    /**
     * Transfiere dinero entre dos cuentas.
     */
    suspend fun transferMoney(
        fromUserId: String,
        toAccountNumber: String,
        amount: Double,
        description: String
    ): Result<Unit>
}