package com.example.exoplayer.data.api

import com.example.exoplayer.data.model.NotificationListResponse
import io.reactivex.Single
import retrofit2.http.GET

interface NotificationApi {

    @GET("/inbox")
    fun getNotificationList(): Single<NotificationListResponse>

}
