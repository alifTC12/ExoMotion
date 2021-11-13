package com.example.exoplayer.notification

import com.example.exoplayer.usecase.NotificationUseCase
import io.reactivex.Scheduler
import io.reactivex.disposables.CompositeDisposable

class NotificationActivityPresenter(
    private val ioScheduler: Scheduler,
    private val mainScheduler: Scheduler,
    private val notificationUseCase: NotificationUseCase
) : NotificationActivityContract.Presenter {

    private val disposable = CompositeDisposable()
    private var view: NotificationActivityContract.View? = null

    override fun attachView(view: NotificationActivityContract.View) {
        this.view = view
    }

    override fun detachView() {
        this.view = null
        disposable.dispose()
    }

    override fun loadNotifications() {
        notificationUseCase
            .getNotifications()
            .subscribeOn(ioScheduler)
            .doOnSubscribe {
                view?.run {
                    hideAllViews()
                    showLoading()
                }
            }
            .subscribeOn(mainScheduler)
            .map { inbox -> inbox.notification.map { NotificationViewObject.create(it, "") } }
            .subscribe({ view?.showNotifications(it) }, { view?.showError() })
            .let { disposable.add(it) }
    }
}