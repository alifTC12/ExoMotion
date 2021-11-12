package com.example.exoplayer.usecase

import com.example.exoplayer.data.gateway.NotificationGateway
import com.example.exoplayer.data_factory.newInbox
import com.example.exoplayer.data_factory.newNewNotification
import com.nhaarman.mockitokotlin2.whenever
import io.reactivex.Single
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock

class NotificationUseCaseImplTest {
    private lateinit var useCase: NotificationUseCaseImpl
    private val notificationGateway: NotificationGateway = mock(NotificationGateway::class.java)

    @Before
    fun setup() {
        useCase = NotificationUseCaseImpl(notificationGateway)
    }

    @Test
    fun `given get notifications, should returns list of notification`() {
        val notifications = listOf(newNewNotification())
        val inbox = newInbox(withNotifications = notifications)

        whenever(notificationGateway.getNotifications()).thenReturn(Single.just(inbox))

        useCase.getNotifications().test().assertValue(inbox)
    }
}