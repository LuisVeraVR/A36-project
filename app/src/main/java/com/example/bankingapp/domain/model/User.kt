package com.example.bankingapp.domain.model

data class User(
    val id: String = "",
    val email: String = "",
    val fullName: String = "",
    val phoneNumber: String? = null,
    val accountBalance: Double = 0.0,
    val accountNumber: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)