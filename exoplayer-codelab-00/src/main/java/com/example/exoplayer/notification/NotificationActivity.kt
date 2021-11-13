package com.example.exoplayer.notification

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.exoplayer.R

class NotificationActivity : AppCompatActivity(), NotificationActivityContract.View {

    private lateinit var presenter: NotificationActivityContract.Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification)

        presenter.attachView(this)
    }

    override fun showLoading() {
    }

    override fun showError() {
    }

    override fun showNotifications(notifications: List<NotificationViewObject>) {
    }

    override fun hideAllViews() {
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.detachView()
    }
}