package com.example.bankingapp.ui.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bankingapp.data.repository.AccountRepositoryImpl
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel para la pantalla principal.
 */
class HomeViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val accountRepository = AccountRepositoryImpl(
        FirebaseFirestore.getInstance()
    )

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

            val userId = auth.currentUser?.uid
            Log.d("HomeViewModel", "👤 UserID: $userId")

            if (userId == null) {
                Log.e("HomeViewModel", "❌ Usuario no autenticado")
                _uiState.value = HomeUiState.Error("Usuario no autenticado")
                return@launch
            }

            try {
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

                _uiState.value = HomeUiState.Success(
                    account = account,
                    recentIncome = emptyList()
                )
                Log.d("HomeViewModel", "✅ Estado: Success")
            } catch (e: Exception) {
                Log.e("HomeViewModel", "❌ Exception: ${e.message}", e)
                _uiState.value = HomeUiState.Error(e.message ?: "Error desconocido")
            }
        }
    }
}