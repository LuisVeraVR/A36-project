package com.example.bankingapp.data.repository

import com.example.bankingapp.data.dto.NotificationDto
import com.example.bankingapp.data.mapper.toDomain
import com.example.bankingapp.data.mapper.toDto
import com.example.bankingapp.domain.model.Notification
import com.example.bankingapp.domain.repository.NotificationRepository
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await

/**
 * Implementación del repositorio de notificaciones con Firebase Firestore.
 */
class NotificationRepositoryImpl(
    private val firestore: FirebaseFirestore
) : NotificationRepository {

    companion object {
        private const val NOTIFICATIONS_COLLECTION = "notifications"
    }

    /**
     * Obtiene todas las notificaciones del usuario.
     */
    override suspend fun getNotifications(userId: String): Result<List<Notification>> {
        return try {
            val snapshot = firestore.collection(NOTIFICATIONS_COLLECTION)
                .whereEqualTo("userId", userId)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .await()

            val notifications = snapshot.documents.mapNotNull { doc ->
                doc.toObject(NotificationDto::class.java)?.copy(id = doc.id)?.toDomain()
            }

            Result.success(notifications)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Obtiene solo las notificaciones no leídas.
     */
    override suspend fun getUnreadNotifications(userId: String): Result<List<Notification>> {
        return try {
            val snapshot = firestore.collection(NOTIFICATIONS_COLLECTION)
                .whereEqualTo("userId", userId)
                .whereEqualTo("isRead", false)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .await()

            val notifications = snapshot.documents.mapNotNull { doc ->
                doc.toObject(NotificationDto::class.java)?.copy(id = doc.id)?.toDomain()
            }

            Result.success(notifications)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Marca una notificación como leída.
     */
    override suspend fun markAsRead(notificationId: String): Result<Unit> {
        return try {
            firestore.collection(NOTIFICATIONS_COLLECTION)
                .document(notificationId)
                .update("isRead", true)
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Marca todas las notificaciones del usuario como leídas.
     */
    override suspend fun markAllAsRead(userId: String): Result<Unit> {
        return try {
            val snapshot = firestore.collection(NOTIFICATIONS_COLLECTION)
                .whereEqualTo("userId", userId)
                .whereEqualTo("isRead", false)
                .get()
                .await()

            val batch = firestore.batch()
            snapshot.documents.forEach { doc ->
                batch.update(doc.reference, "isRead", true)
            }
            batch.commit().await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Crea una nueva notificación.
     */
    override suspend fun createNotification(notification: Notification): Result<String> {
        return try {
            val notificationDto = notification.toDto()

            val documentRef = firestore.collection(NOTIFICATIONS_COLLECTION)
                .add(notificationDto)
                .await()

            Result.success(documentRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Elimina una notificación.
     */
    override suspend fun deleteNotification(notificationId: String): Result<Unit> {
        return try {
            firestore.collection(NOTIFICATIONS_COLLECTION)
                .document(notificationId)
                .delete()
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}