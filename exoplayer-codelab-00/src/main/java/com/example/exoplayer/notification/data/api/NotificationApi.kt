package com.example.exoplayer.notification.data.api

import com.example.exoplayer.notification.data.model.NotificationListResponse
import io.reactivex.Single
import retrofit2.http.GET

interface NotificationApi {

    @GET("/inbox")
    fun getNotificationList(): Single<NotificationListResponse>

}
