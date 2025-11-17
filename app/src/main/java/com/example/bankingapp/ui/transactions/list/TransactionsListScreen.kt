package com.example.bankingapp.ui.transactions.list

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
import com.example.bankingapp.domain.model.Transaction
import com.example.bankingapp.domain.model.enums.TransactionType
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

/**
 * Pantalla de listado de transacciones con filtros.
 *
 * RF05 - HU06, HU07: Listar transacciones con filtros por fecha/tipo.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionsListScreen(
    onNavigateBack: () -> Unit,
    onTransactionClick: (String) -> Unit,
    viewModel: TransactionsListViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val filters by viewModel.filters.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Transacciones") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.loadTransactions() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Actualizar")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Sección de filtros
            FilterSection(
                currentFilter = filters.type,
                onFilterChange = { viewModel.setTypeFilter(it) },
                onClearFilters = { viewModel.clearFilters() }
            )

            Divider()

            // Contenido principal
            Box(modifier = Modifier.fillMaxSize()) {
                when (val state = uiState) {
                    is TransactionsUiState.Loading -> {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                    is TransactionsUiState.Empty -> {
                        EmptyTransactionsView(
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                    is TransactionsUiState.Error -> {
                        ErrorView(
                            message = state.message,
                            onRetry = { viewModel.loadTransactions() },
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                    is TransactionsUiState.Success -> {
                        TransactionsList(
                            transactions = state.transactions,
                            onTransactionClick = onTransactionClick
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FilterSection(
    currentFilter: TransactionType?,
    onFilterChange: (TransactionType?) -> Unit,
    onClearFilters: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "Filtrar por tipo",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = currentFilter == null,
                onClick = { onFilterChange(null) },
                label = { Text("Todas") },
                leadingIcon = if (currentFilter == null) {
                    { Icon(Icons.Default.Check, contentDescription = null, Modifier.size(18.dp)) }
                } else null
            )
            FilterChip(
                selected = currentFilter == TransactionType.INCOME,
                onClick = { onFilterChange(TransactionType.INCOME) },
                label = { Text("Ingresos") },
                leadingIcon = if (currentFilter == TransactionType.INCOME) {
                    { Icon(Icons.Default.Check, contentDescription = null, Modifier.size(18.dp)) }
                } else null
            )
            FilterChip(
                selected = currentFilter == TransactionType.EXPENSE,
                onClick = { onFilterChange(TransactionType.EXPENSE) },
                label = { Text("Egresos") },
                leadingIcon = if (currentFilter == TransactionType.EXPENSE) {
                    { Icon(Icons.Default.Check, contentDescription = null, Modifier.size(18.dp)) }
                } else null
            )
        }

        if (currentFilter != null) {
            Spacer(modifier = Modifier.height(8.dp))
            TextButton(onClick = onClearFilters) {
                Icon(Icons.Default.Clear, contentDescription = null, Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Limpiar filtros")
            }
        }
    }
}

@Composable
private fun TransactionsList(
    transactions: List<Transaction>,
    onTransactionClick: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(transactions, key = { it.id }) { transaction ->
            TransactionCard(
                transaction = transaction,
                onClick = { onTransactionClick(transaction.id) }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TransactionCard(
    transaction: Transaction,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                // Icono según tipo
                Surface(
                    shape = MaterialTheme.shapes.small,
                    color = if (transaction.type == TransactionType.INCOME) {
                        MaterialTheme.colorScheme.primaryContainer
                    } else {
                        MaterialTheme.colorScheme.errorContainer
                    }
                ) {
                    Icon(
                        imageVector = if (transaction.type == TransactionType.INCOME) {
                            Icons.Default.Add
                        } else {
                            Icons.Default.Remove
                        },
                        contentDescription = null,
                        modifier = Modifier.padding(8.dp),
                        tint = if (transaction.type == TransactionType.INCOME) {
                            MaterialTheme.colorScheme.onPrimaryContainer
                        } else {
                            MaterialTheme.colorScheme.onErrorContainer
                        }
                    )
                }

                Column {
                    Text(
                        text = transaction.category.ifEmpty { "Sin categoría" },
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = formatDate(transaction.timestamp),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Text(
                text = formatAmount(transaction.amount, transaction.type),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = if (transaction.type == TransactionType.INCOME) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.error
                }
            )
        }
    }
}

@Composable
private fun EmptyTransactionsView(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.Search,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "No hay transacciones",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Aún no tienes transacciones registradas",
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
            text = "Error al cargar",
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

private fun formatDate(timestamp: Long): String {
    val sdf = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale("es", "ES"))
    return sdf.format(Date(timestamp))
}

private fun formatAmount(amount: Double, type: TransactionType): String {
    val format = NumberFormat.getCurrencyInstance(Locale.US)
    val prefix = if (type == TransactionType.INCOME) "+" else "-"
    return "$prefix ${format.format(amount)}"
}