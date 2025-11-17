package com.example.bankingapp.ui.loans.history

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.bankingapp.domain.model.Loan
import com.example.bankingapp.domain.model.enums.LoanStatus
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

/**
 * Pantalla de historial de préstamos.
 *
 * RF09 - HU11: Ver historial de préstamos con estado.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoanHistoryScreen(
    onNavigateBack: () -> Unit,
    onLoanClick: (String) -> Unit = {},
    viewModel: LoanHistoryViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis Préstamos") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.loadLoanHistory() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Actualizar")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (val state = uiState) {
                is LoanHistoryUiState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                is LoanHistoryUiState.Empty -> {
                    EmptyLoansView(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                is LoanHistoryUiState.Error -> {
                    ErrorView(
                        message = state.message,
                        onRetry = { viewModel.loadLoanHistory() },
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                is LoanHistoryUiState.Success -> {
                    LoansList(
                        loans = state.loans,
                        onLoanClick = onLoanClick
                    )
                }
            }
        }
    }
}

@Composable
private fun LoansList(
    loans: List<Loan>,
    onLoanClick: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(loans, key = { it.id }) { loan ->
            LoanCard(
                loan = loan,
                onClick = { onLoanClick(loan.id) }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LoanCard(
    loan: Loan,
    onClick: () -> Unit
) {
    val statusColor = when (loan.status) {
        LoanStatus.PENDING -> MaterialTheme.colorScheme.tertiary
        LoanStatus.APPROVED -> MaterialTheme.colorScheme.primary
        LoanStatus.REJECTED -> MaterialTheme.colorScheme.error
        LoanStatus.ACTIVE -> MaterialTheme.colorScheme.secondary
        LoanStatus.COMPLETED -> MaterialTheme.colorScheme.outline
        LoanStatus.CANCELLED -> MaterialTheme.colorScheme.surfaceVariant
    }

    val statusText = when (loan.status) {
        LoanStatus.PENDING -> "Pendiente"
        LoanStatus.APPROVED -> "Aprobado"
        LoanStatus.REJECTED -> "Rechazado"
        LoanStatus.ACTIVE -> "Activo"
        LoanStatus.COMPLETED -> "Completado"
        LoanStatus.CANCELLED -> "Cancelado"
    }

    val statusIcon = when (loan.status) {
        LoanStatus.PENDING -> Icons.Default.Schedule
        LoanStatus.APPROVED -> Icons.Default.CheckCircle
        LoanStatus.REJECTED -> Icons.Default.Cancel
        LoanStatus.ACTIVE -> Icons.Default.Pending
        LoanStatus.COMPLETED -> Icons.Default.Done
        LoanStatus.CANCELLED -> Icons.Default.Close
    }

    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header con monto y estado
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = formatCurrency(loan.amount),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = formatDate(loan.createdAt),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Surface(
                    shape = MaterialTheme.shapes.small,
                    color = statusColor.copy(alpha = 0.2f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = statusIcon,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = statusColor
                        )
                        Text(
                            text = statusText,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Medium,
                            color = statusColor
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Divider()

            Spacer(modifier = Modifier.height(12.dp))

            // Detalles
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                DetailColumn(
                    label = "Cuota Mensual",
                    value = formatCurrency(loan.monthlyPayment)
                )
                DetailColumn(
                    label = "Plazo",
                    value = "${loan.termMonths} meses"
                )
                DetailColumn(
                    label = "Tasa",
                    value = "${String.format("%.1f", loan.rate)}%"
                )
            }

            if (loan.purpose.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Propósito: ${loan.purpose}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun DetailColumn(
    label: String,
    value: String
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun EmptyLoansView(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.AccountBalance,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "No tienes préstamos",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Solicita tu primer préstamo desde el simulador",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun ErrorView(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.Warning,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.error
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Error al cargar préstamos",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.error
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onRetry) {
            Text("Reintentar")
        }
    }
}

private fun formatCurrency(amount: Double): String {
    val format = NumberFormat.getCurrencyInstance(Locale.US)
    return format.format(amount)
}

private fun formatDate(timestamp: Long): String {
    val sdf = SimpleDateFormat("dd MMM yyyy", Locale("es", "ES"))
    return sdf.format(Date(timestamp))
}