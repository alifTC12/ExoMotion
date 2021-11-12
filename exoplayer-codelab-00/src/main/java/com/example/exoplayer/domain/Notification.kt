package com.vidio.domain.entity

import java.io.Serializable

data class Inbox(
    val notification: List<Notification>,
    val hasUnseenNotification: Boolean
)

data class Notification(
    val id: Long,
    val title: String,
    val body: String,
    val url: String,
    val timestamp: Long,
    val categoryId: String,
    val imageUrl: String,
    val thumbnailUrl: String,
    val type: Type,
    var seen: Boolean
) : Serializable {
    enum class Type(val value: String) {
        NORMAL("normal"),
        LARGE("large"),
        UNKNOWN("unknown")
    }
}
