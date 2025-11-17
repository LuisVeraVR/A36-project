package com.example.bankingapp.ui.loans.request

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun LoanRequestScreen(
    amount: Double,
    rate: Double,
    term: Int,
    onNavigateBack: () -> Unit,
    onRequestSuccess: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Loan Request Screen - TODO\nAmount: $$amount, Rate: $rate%, Term: $term months")
        }
    }
}