package com.example.exoplayer.notification.data.gateway

import com.example.exoplayer.notification.data.api.NotificationApi
import com.example.exoplayer.notification.data.model.NotificationListResponse
import com.vidio.domain.entity.Inbox
import com.vidio.domain.entity.Notification
import io.reactivex.Single

class NotificationGatewayImpl(
    private val api: NotificationApi
) : NotificationGateway {
    override fun getNotifications(): Single<Inbox> {
        return api.getNotificationList().mapToInbox()
    }

    private fun Single<NotificationListResponse>.mapToInbox(): Single<Inbox> {
        return map { response ->
            Inbox(
                response.notifications
                    .map {
                        Notification(
                            it.id,
                            it.title,
                            it.body,
                            it.url,
                            it.timestamp,
                            it.categoryId,
                            it.imageUrl ?: "",
                            it.thumbnailUrl ?: "",
                            it.type.mapToType(),
                            it.seen
                        )
                    },
                response.hasUnseenNotification
            )
        }
    }

    private fun String.mapToType(): Notification.Type {
        return when {
            this.equals(Notification.Type.NORMAL.value, true) -> Notification.Type.NORMAL
            this.equals(Notification.Type.LARGE.value, true) -> Notification.Type.LARGE
            else -> Notification.Type.UNKNOWN
        }
    }
}