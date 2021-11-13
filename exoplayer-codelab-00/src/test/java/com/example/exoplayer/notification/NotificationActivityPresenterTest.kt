package com.example.exoplayer.notification

import com.example.exoplayer.data_factory.newInbox
import com.example.exoplayer.data_factory.newNewNotification
import com.example.exoplayer.usecase.NotificationUseCase
import com.nhaarman.mockitokotlin2.inOrder
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import org.junit.Before
import org.junit.Test

class NotificationActivityPresenterTest {

    private lateinit var notificationUseCase: NotificationUseCase
    private lateinit var presenter: NotificationActivityPresenter
    private lateinit var view: NotificationActivityContract.View

    @Before
    fun setUp() {
        view = mock()
        notificationUseCase = mock()

        presenter = NotificationActivityPresenter(
            ioScheduler = Schedulers.trampoline(),
            mainScheduler = Schedulers.trampoline(),
            notificationUseCase = notificationUseCase
        )

        presenter.attachView(view)
    }

    @Test
    fun `when get notifications success, show loading then show notification view object`() {
        val notifications = listOf(newNewNotification())
        val inbox = newInbox(withNotifications = notifications)
        whenever(notificationUseCase.getNotifications()).thenReturn(Single.just(inbox))

        presenter.loadNotifications()

        val notificationViewObjects = notifications.map {
            NotificationViewObject.create(it, "")
        }
        inOrder(view) {
            verify(view).hideAllViews()
            verify(view).showLoading()
            verify(view).showNotifications(notificationViewObjects)
        }
    }

    @Test
    fun `when get notifications fail, show loading then show error view`() {
        whenever(notificationUseCase.getNotifications()).thenReturn(Single.error(Exception()))

        presenter.loadNotifications()

        inOrder(view) {
            verify(view).hideAllViews()
            verify(view).showLoading()
            verify(view).showError()
        }
    }

}