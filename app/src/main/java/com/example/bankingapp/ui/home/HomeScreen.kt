package com.example.bankingapp.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.bankingapp.domain.model.Account
import java.text.NumberFormat
import java.util.*

/**
 * Pantalla principal con diseño personalizado.
 *
 * RF04 - HU04, HU05: Visualizar saldo y dinero entrante.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToTransactions: () -> Unit,
    onNavigateToTransfer: () -> Unit,
    onNavigateToLoans: () -> Unit,
    onNavigateToNotifications: () -> Unit,
    onLogout: () -> Unit,
    viewModel: HomeViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                onNavigateToTransactions = onNavigateToTransactions,
                onNavigateToLoans = onNavigateToLoans
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF5F5F5))
        ) {
            when (val state = uiState) {
                is HomeUiState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                is HomeUiState.Error -> {
                    ErrorView(
                        message = state.message,
                        onRetry = { viewModel.loadAccountData() }
                    )
                }
                is HomeUiState.Success -> {
                    HomeContent(
                        account = state.account,
                        userName = state.userName,
                        recentIncome = state.recentIncome,
                        onNavigateToLoans = onNavigateToLoans,
                        onNavigateToTransactions = onNavigateToTransactions,
                        onNavigateToTransfer = onNavigateToTransfer,
                        onLogout = onLogout
                    )
                }
            }
        }
    }
}

@Composable
private fun HomeContent(
    account: Account,
    userName: String,
    recentIncome: List<Double>,
    onNavigateToLoans: () -> Unit,
    onNavigateToTransactions: () -> Unit,
    onNavigateToTransfer: () -> Unit,
    onLogout: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Header con saludo
        HeaderSection(
            userName = userName,
            onLogout = onLogout
        )

        // Saldo disponible
        BalanceSection(balance = account.balance)

        // Tarjetas de cuenta
        AccountCardsSection(
            balance = account.balance,
            accountNumber = account.accountNumber,
            pockets = recentIncome.sum()
        )

        // Sección Principal
        MainActionsSection(
            onNavigateToTransactions = onNavigateToTransactions,
            onNavigateToTransfer = onNavigateToTransfer
        )

        // Otras opciones
        OtherOptionsSection(onNavigateToLoans = onNavigateToLoans)
    }
}

@Composable
private fun HeaderSection(
    userName: String,
    onLogout: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "Bienvenido",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF666666)
            )
            Text(
                text = userName,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A1A1A)
            )
        }

        Box {
            Surface(
                shape = MaterialTheme.shapes.large,
                color = Color(0xFF003D5C)
            ) {
                IconButton(onClick = { showMenu = true }) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Perfil",
                        tint = Color.White
                    )
                }
            }

            DropdownMenu(
                expanded = showMenu,
                onDismissRequest = { showMenu = false }
            ) {
                DropdownMenuItem(
                    text = { Text("Cerrar Sesión") },
                    onClick = {
                        showMenu = false
                        onLogout()
                    },
                    leadingIcon = {
                        Icon(Icons.Default.ExitToApp, contentDescription = null)
                    }
                )
            }
        }
    }
}

@Composable
private fun BalanceSection(balance: Double) {
    Column {
        Text(
            text = "Saldo Disponible",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1A1A1A)
        )
        Text(
            text = formatCurrency(balance),
            style = MaterialTheme.typography.displaySmall,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF003D5C)
        )
    }
}

@Composable
private fun AccountCardsSection(
    balance: Double,
    accountNumber: String,
    pockets: Double
) {
    // Formatear número de cuenta para mostrar últimos 4 dígitos
    val lastFourDigits = if (accountNumber.length >= 4) {
        accountNumber.takeLast(4)
    } else {
        accountNumber
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Tarjeta Visa - Saldo
        AccountCard(
            modifier = Modifier.weight(1f),
            gradient = Brush.verticalGradient(
                colors = listOf(
                    Color(0xFF87CEEB),
                    Color(0xFF4682B4)
                )
            ),
            cardType = "VISA",
            title = "Saldo",
            amount = balance,
            cardNumber = "** $lastFourDigits"
        )

        // Tarjeta Visa - Bolsillos
        AccountCard(
            modifier = Modifier.weight(1f),
            gradient = Brush.verticalGradient(
                colors = listOf(
                    Color(0xFFFFD700),
                    Color(0xFFFF8C00)
                )
            ),
            cardType = "VISA",
            title = "Montos en bolsillos",
            amount = pockets,
            cardNumber = "** $lastFourDigits"
        )
    }
}

@Composable
private fun AccountCard(
    modifier: Modifier = Modifier,
    gradient: Brush,
    cardType: String,
    title: String,
    amount: Double,
    cardNumber: String
) {
    Surface(
        modifier = modifier
            .height(180.dp)
            .shadow(4.dp, RoundedCornerShape(24.dp)),
        shape = RoundedCornerShape(24.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(gradient)
                .padding(20.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = cardType,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Column {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = formatCurrency(amount),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Text(
                    text = cardNumber,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }
        }
    }
}

@Composable
private fun MainActionsSection(
    onNavigateToTransactions: () -> Unit,
    onNavigateToTransfer: () -> Unit
) {
    Column {
        Text(
            text = "Principal:",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1A1A1A)
        )
        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ActionButton(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.Send,
                text = "Transferir dinero",
                onClick = onNavigateToTransfer
            )

            ActionButton(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.AccountBalance,
                text = "Ver transacciones",
                onClick = onNavigateToTransactions
            )
        }
    }
}

@Composable
private fun ActionButton(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    text: String,
    onClick: () -> Unit
) {
    OutlinedCard(
        onClick = onClick,
        modifier = modifier.height(120.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.outlinedCardColors(
            containerColor = Color.White
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = text,
                modifier = Modifier.size(32.dp),
                tint = Color(0xFF003D5C)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF1A1A1A),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}

@Composable
private fun OtherOptionsSection(onNavigateToLoans: () -> Unit) {
    Column {
        Text(
            text = "Otras opciones:",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1A1A1A)
        )
        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OptionButton(
                icon = Icons.Default.CreditCard,
                text = "Tarjetas",
                onClick = { /* TODO */ }
            )
            OptionButton(
                icon = Icons.Default.AccountBalanceWallet,
                text = "Bolsillos",
                onClick = { /* TODO */ }
            )
            OptionButton(
                icon = Icons.Default.Calculate,
                text = "Prestamos",
                onClick = onNavigateToLoans
            )
            OptionButton(
                icon = Icons.Default.Savings,
                text = "Ahorros",
                onClick = { /* TODO */ }
            )
        }
    }
}

@Composable
private fun RowScope.OptionButton(
    icon: ImageVector,
    text: String,
    onClick: () -> Unit
) {
    OutlinedCard(
        onClick = onClick,
        modifier = Modifier
            .weight(1f)
            .aspectRatio(1f),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.outlinedCardColors(
            containerColor = Color.White
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = text,
                modifier = Modifier.size(28.dp),
                tint = Color(0xFF003D5C)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF1A1A1A),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}

@Composable
private fun BottomNavigationBar(
    onNavigateToTransactions: () -> Unit,
    onNavigateToLoans: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp),
        shape = RoundedCornerShape(32.dp),
        color = Color(0xFF003D5C),
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            NavigationButton(
                icon = Icons.Default.Home,
                isSelected = true,
                onClick = { }
            )
            NavigationButton(
                icon = Icons.Default.CreditCard,
                isSelected = false,
                onClick = onNavigateToTransactions
            )
            NavigationButton(
                icon = Icons.Default.Search,
                isSelected = false,
                onClick = { }
            )
            NavigationButton(
                icon = Icons.Default.AttachMoney,
                isSelected = false,
                onClick = onNavigateToLoans
            )
        }
    }
}

@Composable
private fun NavigationButton(
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = MaterialTheme.shapes.large,
        color = if (isSelected) Color(0xFF4A90A4) else Color.Transparent
    ) {
        Box(
            modifier = Modifier.size(56.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

@Composable
private fun ErrorView(
    message: String,
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Warning,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.error
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Error al cargar datos",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.error
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onRetry) {
            Text("Reintentar")
        }
    }
}

private fun formatCurrency(amount: Double): String {
    val format = NumberFormat.getCurrencyInstance(Locale.US)
    format.currency = Currency.getInstance("USD")
    return format.format(amount).replace("$", "$ ")
}