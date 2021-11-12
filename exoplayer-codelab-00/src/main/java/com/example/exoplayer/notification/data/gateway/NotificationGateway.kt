package com.example.exoplayer.notification.data.gateway

import com.vidio.domain.entity.Inbox
import io.reactivex.Single

interface NotificationGateway {
    fun getNotifications(): Single<Inbox>
}