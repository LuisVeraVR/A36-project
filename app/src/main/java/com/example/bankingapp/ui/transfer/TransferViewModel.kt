package com.example.bankingapp.ui.transfer

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
 * ViewModel para la pantalla de transferencia entre usuarios.
 */
class TransferViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val accountRepository = AccountRepositoryImpl(FirebaseFirestore.getInstance())

    private val _uiState = MutableStateFlow<TransferUiState>(TransferUiState.Loading)
    val uiState: StateFlow<TransferUiState> = _uiState.asStateFlow()

    companion object {
        private const val TAG = "TransferViewModel"
    }

    init {
        loadCurrentAccount()
    }

    private fun loadCurrentAccount() {
        viewModelScope.launch {
            _uiState.value = TransferUiState.Loading

            val userId = auth.currentUser?.uid
            if (userId == null) {
                _uiState.value = TransferUiState.Error("Usuario no autenticado")
                return@launch
            }

            try {
                val accountResult = accountRepository.getAccount(userId)
                if (accountResult.isSuccess) {
                    _uiState.value = TransferUiState.Ready(
                        currentAccount = accountResult.getOrThrow()
                    )
                } else {
                    _uiState.value = TransferUiState.Error(
                        accountResult.exceptionOrNull()?.message ?: "Error al cargar cuenta"
                    )
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error cargando cuenta", e)
                _uiState.value = TransferUiState.Error(e.message ?: "Error desconocido")
            }
        }
    }

    fun updateToAccountNumber(accountNumber: String) {
        val currentState = _uiState.value
        if (currentState is TransferUiState.Ready) {
            _uiState.value = currentState.copy(
                toAccountNumber = accountNumber,
                toAccountNumberError = null,
                generalError = null
            )
        }
    }

    fun updateAmount(amount: String) {
        val currentState = _uiState.value
        if (currentState is TransferUiState.Ready) {
            _uiState.value = currentState.copy(
                amount = amount,
                amountError = null,
                generalError = null
            )
        }
    }

    fun updateDescription(description: String) {
        val currentState = _uiState.value
        if (currentState is TransferUiState.Ready) {
            _uiState.value = currentState.copy(
                description = description,
                generalError = null
            )
        }
    }

    fun transfer() {
        val currentState = _uiState.value
        if (currentState !is TransferUiState.Ready) return

        // Validar campos
        if (!validateFields(currentState)) {
            return
        }

        viewModelScope.launch {
            _uiState.value = currentState.copy(isTransferring = true, generalError = null)

            val userId = auth.currentUser?.uid
            if (userId == null) {
                _uiState.value = currentState.copy(
                    isTransferring = false,
                    generalError = "Usuario no autenticado"
                )
                return@launch
            }

            try {
                val amount = currentState.amount.toDoubleOrNull() ?: 0.0
                val result = accountRepository.transferMoney(
                    fromUserId = userId,
                    toAccountNumber = currentState.toAccountNumber.trim(),
                    amount = amount,
                    description = currentState.description.trim()
                )

                if (result.isSuccess) {
                    _uiState.value = TransferUiState.Success(
                        "Transferencia completada exitosamente"
                    )
                } else {
                    _uiState.value = currentState.copy(
                        isTransferring = false,
                        generalError = result.exceptionOrNull()?.message ?: "Error al transferir"
                    )
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error en transferencia", e)
                _uiState.value = currentState.copy(
                    isTransferring = false,
                    generalError = e.message ?: "Error desconocido"
                )
            }
        }
    }

    private fun validateFields(state: TransferUiState.Ready): Boolean {
        var isValid = true

        // Validar número de cuenta destino
        val toAccountNumber = state.toAccountNumber.trim()
        if (toAccountNumber.isEmpty()) {
            _uiState.value = state.copy(toAccountNumberError = "Ingrese el número de cuenta")
            isValid = false
        } else if (toAccountNumber.length < 10) {
            _uiState.value = state.copy(toAccountNumberError = "Número de cuenta inválido")
            isValid = false
        } else if (toAccountNumber == state.currentAccount.accountNumber) {
            _uiState.value = state.copy(toAccountNumberError = "No puedes transferir a tu propia cuenta")
            isValid = false
        }

        // Validar monto
        val amount = state.amount.toDoubleOrNull()
        if (state.amount.isEmpty()) {
            _uiState.value = state.copy(amountError = "Ingrese el monto")
            isValid = false
        } else if (amount == null || amount <= 0) {
            _uiState.value = state.copy(amountError = "Monto inválido")
            isValid = false
        } else if (amount > state.currentAccount.balance) {
            _uiState.value = state.copy(amountError = "Saldo insuficiente")
            isValid = false
        }

        return isValid
    }

    fun resetToReady() {
        loadCurrentAccount()
    }
}
