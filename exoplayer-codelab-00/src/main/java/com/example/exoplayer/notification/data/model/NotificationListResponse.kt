package com.example.exoplayer.notification.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class NotificationListResponse(
    @Json(name = "notifications") val notifications: List<NotificationResponse>,
    @Json(name = "has_unseen_notifications") val hasUnseenNotification: Boolean
)

@JsonClass(generateAdapter = true)
data class NotificationResponse(
    @Json(name = "id") val id: Long,
    @Json(name = "title") val title: String,
    @Json(name = "body") val body: String,
    @Json(name = "url") val url: String,
    @Json(name = "timestamp") val timestamp: Long,
    @Json(name = "category_id") val categoryId: String,
    @Json(name = "image_url") val imageUrl: String?,
    @Json(name = "thumbnail_url") val thumbnailUrl: String?,
    @Json(name = "type") val type: String,
    @Json(name = "seen") val seen: Boolean
)
