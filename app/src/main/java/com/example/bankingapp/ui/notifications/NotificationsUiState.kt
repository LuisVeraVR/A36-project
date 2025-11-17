package com.example.bankingapp.ui.notifications

import com.example.bankingapp.domain.model.Notification

/**
 * Estado de la pantalla de notificaciones.
 */
sealed class NotificationsUiState {
    object Loading : NotificationsUiState()
    data class Success(val notifications: List<Notification>) : NotificationsUiState()
    data class Error(val message: String) : NotificationsUiState()
    object Empty : NotificationsUiState()
}