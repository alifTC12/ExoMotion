package com.example.exoplayer.data_factory

import com.example.exoplayer.domain.Inbox
import com.example.exoplayer.domain.Notification

fun newInbox(
    withNotifications: List<Notification> = emptyList(),
    hasUnseen: Boolean = false
): Inbox {
    return Inbox(
        withNotifications,
        hasUnseen
    )
}

fun newNewNotification(
    withId: Long = 123L,
    withTitle: String = "title",
    withBody: String = "body",
    withUrl: String = "url",
    withTimestamp: Long = 123123L,
    withCategoryId: String = "categoryId",
    withImageUrl: String = "image url",
    withThumbnailUrl: String = "thumbnail url",
    withType: Notification.Type = Notification.Type.NORMAL,
    withSeen: Boolean = true
): Notification {
    return Notification(
        withId,
        withTitle,
        withBody,
        withUrl,
        withTimestamp,
        withCategoryId,
        withImageUrl,
        withThumbnailUrl,
        withType,
        withSeen
    )
}
