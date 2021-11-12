package com.example.exoplayer.usecase

import com.example.exoplayer.domain.Inbox
import io.reactivex.Single

interface NotificationUseCase {
    fun getNotifications(): Single<Inbox>
}