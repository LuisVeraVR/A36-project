package com.example.bankingapp.ui.loans.detail

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
 * Pantalla de detalle de préstamo.
 *
 * RF09 - HU12: Ver detalle completo del préstamo con timeline de estado.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoanDetailScreen(
    loanId: String,
    onNavigateBack: () -> Unit,
    viewModel: LoanDetailViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showAdminDialog by remember { mutableStateOf(false) }

    // Cargar detalles al mostrar la pantalla
    LaunchedEffect(loanId) {
        viewModel.loadLoanDetail(loanId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle del Préstamo") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    // Botón de admin (solo para demostración)
                    IconButton(onClick = { showAdminDialog = true }) {
                        Icon(Icons.Default.Settings, contentDescription = "Admin")
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
                is LoanDetailUiState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                is LoanDetailUiState.Error -> {
                    ErrorView(
                        message = state.message,
                        onBack = onNavigateBack,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                is LoanDetailUiState.Success -> {
                    LoanDetailContent(loan = state.loan)
                }
            }
        }
    }

    // Diálogo de administración (para demostración)
    if (showAdminDialog) {
        val currentState = uiState
        if (currentState is LoanDetailUiState.Success) {
            AdminDialog(
                loan = currentState.loan,
                onDismiss = { showAdminDialog = false },
                onStatusChange = { status, reason ->
                    viewModel.updateLoanStatus(loanId, status, reason)
                    showAdminDialog = false
                }
            )
        }
    }
}

@Composable
private fun LoanDetailContent(loan: Loan) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Card de estado principal
        StatusCard(loan = loan)

        // Card de información del préstamo
        LoanInfoCard(loan = loan)

        // Card de pagos
        PaymentInfoCard(loan = loan)

        // Timeline de estado
        StatusTimelineCard(loan = loan)

        // Notas adicionales
        if (!loan.notes.isNullOrEmpty() || !loan.rejectionReason.isNullOrEmpty()) {
            NotesCard(loan = loan)
        }
    }
}

@Composable
private fun StatusCard(loan: Loan) {
    val statusColor = when (loan.status) {
        LoanStatus.PENDING -> MaterialTheme.colorScheme.tertiary
        LoanStatus.APPROVED -> MaterialTheme.colorScheme.primary
        LoanStatus.REJECTED -> MaterialTheme.colorScheme.error
        LoanStatus.ACTIVE -> MaterialTheme.colorScheme.secondary
        LoanStatus.COMPLETED -> MaterialTheme.colorScheme.outline
        LoanStatus.CANCELLED -> MaterialTheme.colorScheme.surfaceVariant
    }

    val statusText = when (loan.status) {
        LoanStatus.PENDING -> "Pendiente de Revisión"
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
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = statusColor.copy(alpha = 0.15f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = statusIcon,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = statusColor
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = "Estado del Préstamo",
                    style = MaterialTheme.typography.labelMedium,
                    color = statusColor
                )
                Text(
                    text = statusText,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = statusColor
                )
            }
        }
    }
}

@Composable
private fun LoanInfoCard(loan: Loan) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Información del Préstamo",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Divider()

            InfoRow(label = "Monto Solicitado", value = formatCurrency(loan.amount))
            InfoRow(label = "Tasa de Interés Anual", value = "${String.format("%.2f", loan.rate)}%")
            InfoRow(label = "Plazo", value = "${loan.termMonths} meses")

            if (loan.purpose.isNotEmpty()) {
                InfoRow(label = "Propósito", value = loan.purpose)
            }

            InfoRow(label = "Fecha de Solicitud", value = formatFullDate(loan.createdAt))

            if (loan.processedAt != null) {
                InfoRow(
                    label = "Fecha de Procesamiento",
                    value = formatFullDate(loan.processedAt)
                )
            }
        }
    }
}

@Composable
private fun PaymentInfoCard(loan: Loan) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Información de Pagos",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )

            Divider(color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.3f))

            PaymentRow(
                label = "Cuota Mensual",
                value = formatCurrency(loan.monthlyPayment),
                highlighted = true
            )
            PaymentRow(
                label = "Total a Pagar",
                value = formatCurrency(loan.totalToPay)
            )
            PaymentRow(
                label = "Intereses Totales",
                value = formatCurrency(loan.totalToPay - loan.amount)
            )
        }
    }
}

@Composable
private fun StatusTimelineCard(loan: Loan) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Historial del Préstamo",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Divider()

            // Siempre mostrar creación
            TimelineItem(
                icon = Icons.Default.Add,
                title = "Solicitud Creada",
                date = formatFullDate(loan.createdAt),
                isCompleted = true
            )

            // Mostrar estado actual
            when (loan.status) {
                LoanStatus.PENDING -> {
                    TimelineItem(
                        icon = Icons.Default.Schedule,
                        title = "En Revisión",
                        date = "En proceso...",
                        isCompleted = false,
                        isActive = true
                    )
                }
                LoanStatus.APPROVED -> {
                    if (loan.processedAt != null) {
                        TimelineItem(
                            icon = Icons.Default.CheckCircle,
                            title = "Préstamo Aprobado",
                            date = formatFullDate(loan.processedAt),
                            isCompleted = true
                        )
                    }
                }
                LoanStatus.REJECTED -> {
                    if (loan.processedAt != null) {
                        TimelineItem(
                            icon = Icons.Default.Cancel,
                            title = "Préstamo Rechazado",
                            date = formatFullDate(loan.processedAt),
                            isCompleted = true,
                            isError = true
                        )
                    }
                }
                LoanStatus.ACTIVE -> {
                    TimelineItem(
                        icon = Icons.Default.Pending,
                        title = "Préstamo Activo",
                        date = "Pagos en curso",
                        isCompleted = true,
                        isActive = true
                    )
                }
                LoanStatus.COMPLETED -> {
                    TimelineItem(
                        icon = Icons.Default.Done,
                        title = "Préstamo Completado",
                        date = "Totalmente pagado",
                        isCompleted = true
                    )
                }
                LoanStatus.CANCELLED -> {
                    TimelineItem(
                        icon = Icons.Default.Close,
                        title = "Préstamo Cancelado",
                        date = formatFullDate(loan.updatedAt ?: loan.createdAt),
                        isCompleted = true,
                        isError = true
                    )
                }
            }
        }
    }
}

@Composable
private fun TimelineItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    date: String,
    isCompleted: Boolean,
    isActive: Boolean = false,
    isError: Boolean = false
) {
    val color = when {
        isError -> MaterialTheme.colorScheme.error
        isActive -> MaterialTheme.colorScheme.primary
        isCompleted -> MaterialTheme.colorScheme.outline
        else -> MaterialTheme.colorScheme.surfaceVariant
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            shape = MaterialTheme.shapes.small,
            color = color.copy(alpha = 0.2f)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier
                    .padding(8.dp)
                    .size(24.dp),
                tint = color
            )
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal
            )
            Text(
                text = date,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun NotesCard(loan: Loan) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Notas Adicionales",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            if (!loan.notes.isNullOrEmpty()) {
                Text(
                    text = loan.notes,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            if (!loan.rejectionReason.isNullOrEmpty()) {
                Divider()
                Text(
                    text = "Motivo de Rechazo:",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.error,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = loan.rejectionReason,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f),
            textAlign = androidx.compose.ui.text.style.TextAlign.End
        )
    }
}

@Composable
private fun PaymentRow(
    label: String,
    value: String,
    highlighted: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = if (highlighted) {
                MaterialTheme.typography.titleSmall
            } else {
                MaterialTheme.typography.bodyMedium
            },
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
        Text(
            text = value,
            style = if (highlighted) {
                MaterialTheme.typography.titleLarge
            } else {
                MaterialTheme.typography.bodyLarge
            },
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}

@Composable
private fun ErrorView(
    message: String,
    onBack: () -> Unit,
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
            text = "Error al cargar préstamo",
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
        Button(onClick = onBack) {
            Text("Volver")
        }
    }
}

/**
 * Diálogo de administración para cambiar el estado del préstamo.
 * Solo para demostración/testing en clase.
 */
@Composable
private fun AdminDialog(
    loan: Loan,
    onDismiss: () -> Unit,
    onStatusChange: (LoanStatus, String?) -> Unit
) {
    var selectedStatus by remember { mutableStateOf(loan.status) }
    var rejectionReason by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Panel de Administración (Demo)") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "Cambiar estado del préstamo:",
                    style = MaterialTheme.typography.bodyMedium
                )

                // Botones de estado
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = selectedStatus == LoanStatus.APPROVED,
                        onClick = { selectedStatus = LoanStatus.APPROVED },
                        label = { Text("Aprobar") }
                    )
                    FilterChip(
                        selected = selectedStatus == LoanStatus.REJECTED,
                        onClick = { selectedStatus = LoanStatus.REJECTED },
                        label = { Text("Rechazar") }
                    )
                }

                // Campo de razón de rechazo
                if (selectedStatus == LoanStatus.REJECTED) {
                    OutlinedTextField(
                        value = rejectionReason,
                        onValueChange = { rejectionReason = it },
                        label = { Text("Motivo de rechazo") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 3
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onStatusChange(
                        selectedStatus,
                        if (selectedStatus == LoanStatus.REJECTED) rejectionReason else null
                    )
                }
            ) {
                Text("Aplicar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

private fun formatCurrency(amount: Double): String {
    val format = NumberFormat.getCurrencyInstance(Locale.US)
    return format.format(amount)
}

private fun formatFullDate(timestamp: Long): String {
    val sdf = SimpleDateFormat("dd 'de' MMMM 'de' yyyy, HH:mm", Locale("es", "ES"))
    return sdf.format(Date(timestamp))
}