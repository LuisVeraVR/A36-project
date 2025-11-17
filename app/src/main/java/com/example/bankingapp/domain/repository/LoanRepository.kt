package com.example.bankingapp.domain.repository

import com.example.bankingapp.domain.model.Loan
import com.example.bankingapp.domain.model.enums.LoanStatus

interface LoanRepository {
    suspend fun simulateLoan(
        amount: Double,
        interestRate: Double,
        termMonths: Int
    ): Result<Pair<Double, Double>> // (monthlyPayment, totalPayment)

    suspend fun requestLoan(loan: Loan): Result<String>

    suspend fun getLoanHistory(userId: String): Result<List<Loan>>

    suspend fun getLoanById(loanId: String): Result<Loan>

    suspend fun getLoansByStatus(
        userId: String,
        status: LoanStatus
    ): Result<List<Loan>>
}