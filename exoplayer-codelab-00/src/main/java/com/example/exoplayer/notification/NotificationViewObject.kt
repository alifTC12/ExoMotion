package com.example.exoplayer.notification

import com.example.exoplayer.domain.Notification
import com.example.exoplayer.notification.NotificationViewObject.NotificationType.*

sealed class NotificationViewObject(open val id: Long) {

    object EnableReminder : NotificationViewObject(-1)

    data class NormalNotification(
        override val id: Long,
        val url: String,
        val title: String,
        val message: String,
        val timestamp: Long,
        val category: String,
        val imageUrl: String,
        val icon: String?,
        val type: NotificationType,
        val hasRead: Boolean
    ) : NotificationViewObject(id)

    enum class NotificationType {
        Normal,
        Large,
        Unknown
    }

    companion object {

        fun create(notification: Notification, icon: String?): NotificationViewObject {
            val type = when (notification.type) {
                Notification.Type.NORMAL -> Normal
                Notification.Type.LARGE -> Large
                Notification.Type.UNKNOWN -> Unknown
            }

            return with(notification) {
                NormalNotification(
                    id,
                    url,
                    title,
                    body,
                    timestamp,
                    categoryId,
                    imageUrl,
                    icon,
                    type,
                    seen
                )
            }
        }
    }
}
