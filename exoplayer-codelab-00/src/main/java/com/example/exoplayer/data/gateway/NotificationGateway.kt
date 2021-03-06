package com.example.exoplayer.data.gateway

import com.example.exoplayer.domain.Inbox
import io.reactivex.Single

interface NotificationGateway {
    fun getNotifications(): Single<Inbox>
}