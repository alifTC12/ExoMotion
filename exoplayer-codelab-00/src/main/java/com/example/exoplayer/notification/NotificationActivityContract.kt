package com.example.exoplayer.notification

interface NotificationActivityContract {
    interface View {
        fun showLoading()
        fun showError()
        fun showNotifications(notifications: List<NotificationViewObject>)
        fun hideAllViews()
    }

    interface Presenter {
        fun attachView(view: View)
        fun detachView()
        fun loadNotifications()
    }
}