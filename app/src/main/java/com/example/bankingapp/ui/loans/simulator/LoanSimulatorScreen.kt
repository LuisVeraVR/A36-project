package com.example.bankingapp.ui.loans.simulator

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import java.text.NumberFormat
import java.util.*

/**
 * Pantalla del simulador de préstamos.
 *
 * RF07 - HU09: Simulador de préstamo.
 * RF08 - HU10: Solicitar préstamo.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoanSimulatorScreen(
    onNavigateBack: () -> Unit,
    onNavigateToRequest: (Double, Double, Int) -> Unit,
    onNavigateToHistory: () -> Unit,
    viewModel: LoanSimulatorViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showRequestDialog by remember { mutableStateOf(false) }

    // Manejar éxito de solicitud
    LaunchedEffect(uiState.loanRequestSuccess) {
        if (uiState.loanRequestSuccess) {
            viewModel.clearRequestSuccess()
            onNavigateToHistory()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Simulador de Préstamos") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateToHistory) {
                        Icon(Icons.Default.History, contentDescription = "Historial")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Sección de entrada de datos
            SimulatorInputSection(
                amount = uiState.amount,
                rate = uiState.rate,
                termMonths = uiState.termMonths,
                onAmountChange = { viewModel.updateAmount(it) },
                onRateChange = { viewModel.updateRate(it) },
                onTermChange = { viewModel.updateTermMonths(it) }
            )

            // Botón de simular
            Button(
                onClick = { viewModel.simulateLoan() },
                modifier = Modifier.fillMaxWidth(),
                enabled = !uiState.isSimulating && uiState.amount.isNotEmpty()
            ) {
                if (uiState.isSimulating) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(if (uiState.isSimulating) "Simulando..." else "Simular Préstamo")
            }

            // Mostrar error si existe
            uiState.error?.let { error ->
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Text(
                        text = error,
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }

            // Resultados de la simulación
            uiState.simulation?.let { simulation ->
                SimulationResultsSection(
                    simulation = simulation,
                    onRequestLoan = { showRequestDialog = true }
                )
            }
        }
    }

    // Diálogo para solicitar préstamo
    if (showRequestDialog) {
        RequestLoanDialog(
            onDismiss = { showRequestDialog = false },
            onConfirm = { purpose ->
                viewModel.requestLoan(purpose)
                showRequestDialog = false
            },
            isLoading = uiState.isRequestingLoan
        )
    }
}

@Composable
private fun SimulatorInputSection(
    amount: String,
    rate: Double,
    termMonths: Int,
    onAmountChange: (String) -> Unit,
    onRateChange: (Double) -> Unit,
    onTermChange: (Int) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Datos del Préstamo",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            // Monto
            OutlinedTextField(
                value = amount,
                onValueChange = onAmountChange,
                label = { Text("Monto del préstamo") },
                leadingIcon = { Text("$") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // Tasa de interés
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Tasa de Interés Anual",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "${String.format("%.1f", rate)}%",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Slider(
                    value = rate.toFloat(),
                    onValueChange = { onRateChange(it.toDouble()) },
                    valueRange = 5f..30f,
                    steps = 49 // 0.5% increments
                )
                Text(
                    text = "Rango: 5% - 30%",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Plazo
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Plazo",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "$termMonths meses",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Slider(
                    value = termMonths.toFloat(),
                    onValueChange = { onTermChange(it.toInt()) },
                    valueRange = 3f..60f,
                    steps = 56 // 1 month increments
                )
                Text(
                    text = "Rango: 3 - 60 meses",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun SimulationResultsSection(
    simulation: com.example.bankingapp.domain.usecase.loan.SimulateLoanUseCase.LoanSimulation,
    onRequestLoan: () -> Unit
) {
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
                text = "Resultados de la Simulación",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )

            Divider(color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.3f))

            // Cuota mensual
            ResultRow(
                label = "Cuota Mensual",
                value = formatCurrency(simulation.monthlyPayment),
                highlighted = true
            )

            // Total a pagar
            ResultRow(
                label = "Total a Pagar",
                value = formatCurrency(simulation.totalToPay)
            )

            // Intereses totales
            ResultRow(
                label = "Intereses Totales",
                value = formatCurrency(simulation.totalInterest)
            )

            // Tasa efectiva anual
            ResultRow(
                label = "Tasa Efectiva Anual",
                value = "${String.format("%.2f", simulation.effectiveAnnualRate)}%"
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = onRequestLoan,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Solicitar este Préstamo")
            }
        }
    }
}

@Composable
private fun ResultRow(
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
private fun RequestLoanDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit,
    isLoading: Boolean
) {
    var purpose by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Solicitar Préstamo") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("¿Para qué usarás este préstamo?")
                OutlinedTextField(
                    value = purpose,
                    onValueChange = { purpose = it },
                    label = { Text("Propósito del préstamo") },
                    placeholder = { Text("Ej: Compra de vehículo, Renovación de hogar...") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3,
                    enabled = !isLoading
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(purpose) },
                enabled = !isLoading && purpose.isNotBlank()
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Solicitar")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, enabled = !isLoading) {
                Text("Cancelar")
            }
        }
    )
}

private fun formatCurrency(amount: Double): String {
    val format = NumberFormat.getCurrencyInstance(Locale.US)
    return format.format(amount)
}