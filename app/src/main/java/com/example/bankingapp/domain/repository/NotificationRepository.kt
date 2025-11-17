package com.example.bankingapp.domain.repository

import com.example.bankingapp.domain.model.Notification

interface NotificationRepository {
    suspend fun getNotifications(userId: String): Result<List<Notification>>

    suspend fun getUnreadNotifications(userId: String): Result<List<Notification>>

    suspend fun markAsRead(notificationId: String): Result<Unit>

    suspend fun markAllAsRead(userId: String): Result<Unit>

    suspend fun createNotification(notification: Notification): Result<String>

    suspend fun deleteNotification(notificationId: String): Result<Unit>
}