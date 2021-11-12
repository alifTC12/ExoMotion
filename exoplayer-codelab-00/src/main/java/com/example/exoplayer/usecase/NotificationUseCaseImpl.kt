package com.example.exoplayer.usecase

import com.example.exoplayer.data.gateway.NotificationGateway
import com.example.exoplayer.domain.Inbox
import io.reactivex.Single

class NotificationUseCaseImpl(private val notificationGateway: NotificationGateway) :
    NotificationUseCase {
    override fun getNotifications(): Single<Inbox> {
        return notificationGateway.getNotifications()
    }
}