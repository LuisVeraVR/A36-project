package com.example.bankingapp.domain.repository

import com.example.bankingapp.domain.model.User

interface AuthRepository {
    suspend fun login(email: String, password: String): Result<User>
    suspend fun loginWithUsername(username: String, password: String): Result<User>
    suspend fun register(email: String, password: String, fullName: String, username: String): Result<User>
    suspend fun resetPassword(email: String): Result<Unit>
    suspend fun logout(): Result<Unit>
    suspend fun getCurrentUser(): Result<User?>
    suspend fun isUserLoggedIn(): Boolean
}