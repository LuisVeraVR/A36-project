package com.example.bankingapp.ui.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bankingapp.data.repository.AccountRepositoryImpl
import com.example.bankingapp.data.repository.TransactionRepositoryImpl
import com.example.bankingapp.domain.model.enums.TransactionType
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

/**
 * ViewModel para la pantalla principal.
 */
class HomeViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    private val accountRepository = AccountRepositoryImpl(firestore)
    private val transactionRepository = TransactionRepositoryImpl(firestore)

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        Log.d("HomeViewModel", "🚀 HomeViewModel inicializado")
        loadAccountData()
    }

    fun loadAccountData() {
        Log.d("HomeViewModel", "📊 Cargando datos de cuenta...")

        viewModelScope.launch {
            _uiState.value = HomeUiState.Loading
            Log.d("HomeViewModel", "⏳ Estado: Loading")

            val currentUser = auth.currentUser
            val userId = currentUser?.uid
            Log.d("HomeViewModel", "👤 UserID: $userId")

            if (userId == null) {
                Log.e("HomeViewModel", "❌ Usuario no autenticado")
                _uiState.value = HomeUiState.Error("Usuario no autenticado")
                return@launch
            }

            try {
                // Obtener nombre del usuario desde Firebase Auth
                val userName = getUserName(currentUser.displayName, currentUser.email)
                Log.d("HomeViewModel", "👤 Nombre de usuario: $userName")

                // Obtener cuenta
                Log.d("HomeViewModel", "🔍 Obteniendo cuenta...")
                val accountResult = accountRepository.getAccount(userId)

                if (accountResult.isFailure) {
                    Log.e("HomeViewModel", "❌ Error: ${accountResult.exceptionOrNull()?.message}")
                    _uiState.value = HomeUiState.Error(
                        accountResult.exceptionOrNull()?.message ?: "Error al cargar cuenta"
                    )
                    return@launch
                }

                val account = accountResult.getOrThrow()
                Log.d("HomeViewModel", "✅ Cuenta cargada: ${account.accountNumber}")

                // Calcular ingresos recientes (últimos 7 días)
                val recentIncome = calculateRecentIncome(userId)
                Log.d("HomeViewModel", "💰 Ingresos recientes calculados: ${recentIncome.size} transacciones")

                _uiState.value = HomeUiState.Success(
                    account = account,
                    userName = userName,
                    recentIncome = recentIncome
                )
                Log.d("HomeViewModel", "✅ Estado: Success")
            } catch (e: Exception) {
                Log.e("HomeViewModel", "❌ Exception: ${e.message}", e)
                _uiState.value = HomeUiState.Error(e.message ?: "Error desconocido")
            }
        }
    }

    /**
     * Obtiene el nombre del usuario desde Firebase Auth.
     * Si no hay displayName, extrae el nombre del email.
     */
    private fun getUserName(displayName: String?, email: String?): String {
        return when {
            !displayName.isNullOrBlank() -> displayName
            !email.isNullOrBlank() -> {
                // Extraer nombre antes del @ del email y capitalizarlo
                email.substringBefore("@")
                    .split(".", "_", "-")
                    .joinToString(" ") { part ->
                        part.replaceFirstChar { it.uppercase() }
                    }
            }
            else -> "Usuario"
        }
    }

    /**
     * Calcula los montos de ingresos recientes (últimos 7 días).
     */
    private suspend fun calculateRecentIncome(userId: String): List<Double> {
        return try {
            val sevenDaysAgo = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(7)
            val transactionsResult = transactionRepository.getTransactions(
                userId = userId,
                startDate = sevenDaysAgo,
                endDate = null,
                type = TransactionType.INCOME
            )

            if (transactionsResult.isSuccess) {
                val transactions = transactionsResult.getOrThrow()
                transactions.map { it.amount }
            } else {
                Log.w("HomeViewModel", "⚠️ No se pudieron obtener transacciones recientes")
                emptyList()
            }
        } catch (e: Exception) {
            Log.e("HomeViewModel", "❌ Error calculando ingresos recientes: ${e.message}", e)
            emptyList()
        }
    }
}