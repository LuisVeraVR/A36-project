package com.example.bankingapp.ui.notifications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bankingapp.data.repository.NotificationRepositoryImpl
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel para la pantalla de notificaciones.
 */
class NotificationsViewModel : ViewModel() {

    private val notificationRepository = NotificationRepositoryImpl(FirebaseFirestore.getInstance())
    private val auth = FirebaseAuth.getInstance()

    private val _uiState = MutableStateFlow<NotificationsUiState>(NotificationsUiState.Loading)
    val uiState: StateFlow<NotificationsUiState> = _uiState.asStateFlow()

    init {
        loadNotifications()
    }

    /**
     * Carga todas las notificaciones del usuario.
     */
    fun loadNotifications() {
        viewModelScope.launch {
            _uiState.value = NotificationsUiState.Loading

            val userId = auth.currentUser?.uid
            if (userId == null) {
                _uiState.value = NotificationsUiState.Error("Usuario no autenticado")
                return@launch
            }

            try {
                val result = notificationRepository.getNotifications(userId)

                if (result.isSuccess) {
                    val notifications = result.getOrThrow()
                    _uiState.value = if (notifications.isEmpty()) {
                        NotificationsUiState.Empty
                    } else {
                        NotificationsUiState.Success(notifications)
                    }
                } else {
                    _uiState.value = NotificationsUiState.Error(
                        result.exceptionOrNull()?.message ?: "Error al cargar notificaciones"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = NotificationsUiState.Error(e.message ?: "Error desconocido")
            }
        }
    }

    /**
     * Marca una notificación como leída.
     */
    fun markAsRead(notificationId: String) {
        viewModelScope.launch {
            try {
                notificationRepository.markAsRead(notificationId)
                loadNotifications() // Recargar para reflejar cambios
            } catch (e: Exception) {
                // Log error
            }
        }
    }

    /**
     * Marca todas las notificaciones como leídas.
     */
    fun markAllAsRead() {
        viewModelScope.launch {
            val userId = auth.currentUser?.uid ?: return@launch

            try {
                notificationRepository.markAllAsRead(userId)
                loadNotifications()
            } catch (e: Exception) {
                // Log error
            }
        }
    }

    /**
     * Elimina una notificación.
     */
    fun deleteNotification(notificationId: String) {
        viewModelScope.launch {
            try {
                notificationRepository.deleteNotification(notificationId)
                loadNotifications()
            } catch (e: Exception) {
                // Log error
            }
        }
    }
}