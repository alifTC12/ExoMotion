package com.example.exoplayer.notification

import com.example.exoplayer.data.gateway.NotificationGatewayImpl
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapters.Rfc3339DateJsonAdapter
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.vidio.domain.entity.Inbox
import com.vidio.domain.entity.Notification
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.*

private class X

fun readResourceAsString(resourceName: String) = X::class.java.classLoader!!
    .getResourceAsStream(resourceName)
    .reader()
    .use { it.readText() }

val moshi: Moshi = Moshi.Builder()
    .add(Date::class.java, Rfc3339DateJsonAdapter())
    .addLast(KotlinJsonAdapterFactory())
    .build()

inline fun <reified T> createApiWithMoshi(baseUrl: String): T = Retrofit.Builder()
    .baseUrl(baseUrl)
    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .build()
    .create(T::class.java)

class NotificationGatewayImplTest {
    @get:Rule
    val server = MockWebServerTestRule()

    private lateinit var gateway: NotificationGatewayImpl

    @Before
    fun before() {
        gateway = NotificationGatewayImpl(createApiWithMoshi(server.baseUrl))
    }

    @Test
    fun `given request get notifications, should returns notifications list`() {
        val response = readResourceAsString("notification_list_response.json")
        server.alwaysRespondWithSuccess(body = response)

        val expectedResponse = Inbox(
            listOf(
                Notification(
                    id = 123,
                    title = "Ada yang seru di Vidio! 3",
                    body = "test",
                    url = "https://www.vidio.com/watch/1799882-liverpool-tahan-manchester-united-di-old-trafford",
                    timestamp = 1617763983650,
                    categoryId = "information",
                    imageUrl = "https://cdns.klimg.com/merdeka.com/i/w/tokoh/2013/06/24/6830/200x300/sctv.jpg",
                    thumbnailUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/2/21/Solid_black.svg/512px-Solid_black.svg.png",
                    type = Notification.Type.NORMAL,
                    seen = true
                )
            ),
            true
        )

        gateway.getNotifications().test().assertValue(expectedResponse)
    }

}