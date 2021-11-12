package com.example.exoplayer.notification

import okhttp3.Headers
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import okhttp3.mockwebserver.SocketPolicy.NO_RESPONSE
import okio.Buffer
import org.json.JSONArray
import org.json.JSONObject
import org.junit.Assert.*
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement
import java.net.URLDecoder
import java.nio.charset.Charset
import java.util.concurrent.TimeUnit.SECONDS
import java.util.concurrent.TimeoutException

class MockWebServerTestRule : TestRule {

    val server: MockWebServer
        get() = statement.server

    val requestCount: Int
        get() = server.requestCount

    val baseUrl: String
        get() = server.url("/").toString()

    lateinit var statement: MockWebServerStatement

    fun handleRequest(block: (request: RecordedRequest, response: MockResponse) -> Unit) {

        server.setDispatcher(object : Dispatcher() {
            override fun dispatch(request: RecordedRequest): MockResponse {
                val response = MockResponse()
                block(request, response)
                return response
            }
        })
    }

    fun respondWithSuccess(
        responseCode: Int = 200,
        body: String? = null,
        headers: Headers? = null
    ): MockWebServerTestRule {
        val response = MockResponse().setResponseCode(responseCode)
        headers?.let { response.setHeaders(it) }
        body?.let { response.setBody(it) }
        return respondWith(response)
    }

    fun alwaysRespondWithSuccess(
        responseCode: Int = 200,
        body: String? = null
    ): MockWebServerTestRule {
        val response = MockResponse().setResponseCode(responseCode)
        body?.let { response.setBody(it) }
        return alwaysRespondWith(response)
    }

    fun respondWithError(
        responseCode: Int,
        body: String? = null,
        headers: Headers? = null
    ): MockWebServerTestRule {
        val response = MockResponse().setResponseCode(responseCode)
        body?.let { response.setBody(it) }
        headers?.let { response.setHeaders(headers) }
        return respondWith(response)
    }

    fun alwaysRespondWithError(responseCode: Int, body: String? = null): MockWebServerTestRule {
        val response = MockResponse().setResponseCode(responseCode)
        body?.let { response.setBody(it) }
        return alwaysRespondWith(response)
    }

    fun respondWith(block: MockResponse.() -> Unit): MockWebServerTestRule {
        val response = MockResponse()
        block(response)
        respondWith(response)
        return this
    }

    fun respondWith(mockResponse: MockResponse): MockWebServerTestRule {
        server.enqueue(mockResponse)
        return this
    }

    fun alwaysRespondWith(mockResponse: MockResponse): MockWebServerTestRule {
        server.setDispatcher(object : Dispatcher() {
            override fun dispatch(request: RecordedRequest?): MockResponse {
                return mockResponse
            }
        })
        return this
    }

    fun sendNoResponse(): MockWebServerTestRule {
        val response = MockResponse()
        response.socketPolicy = NO_RESPONSE
        return respondWith(response)
    }

    fun alwaysSendNoResponse(): MockWebServerTestRule {
        val response = MockResponse()
        response.socketPolicy = NO_RESPONSE
        return alwaysRespondWith(response)
    }

    fun takeRequest(timeoutInSeconds: Long = 1): RecordedRequest {
        return server.takeRequest(timeoutInSeconds, SECONDS)
            ?: throw TimeoutException("No requests received by mock server within $timeoutInSeconds second(s)")
    }

    fun takeRequests(n: Int): List<RecordedRequest> {
        return (0 until n)
            .map { takeRequest() }
            .toList()
    }

    override fun apply(base: Statement, description: Description?): Statement {
        statement = MockWebServerStatement(base)
        return statement
    }

    class MockWebServerStatement(private val base: Statement) : Statement() {

        val server = MockWebServer()

        override fun evaluate() {
            server.start()
            try {
                base.evaluate()
            } finally {
                server.shutdown()
            }
        }
    }

    fun respondWithDigestChallenge(
        realm: String = "bbm-client",
        nonce: String = "random nonce",
        algorithm: String = "SHA-256",
        qop: String? = "auth",
        isStale: Boolean? = null
    ): MockWebServerTestRule {
        respondWith {
            setResponseCode(401)

            addHeader(
                "WWW-Authenticate",
                """Digest
            | realm="$realm",
            | algorithm=$algorithm,
            | nonce="$nonce"
            | ${if (qop != null) ", qop=$qop" else ""}
            | ${if (isStale != null) ", stale=$isStale" else ""}""".trimMargin().replace("\n", "")
            )
        }
        return this
    }
}

//region Request verification classes
fun RecordedRequest.verify(block: RequestVerifier.() -> Unit) {

    block(RequestVerifier(this))
}

class RequestVerifier(val request: RecordedRequest) {

    fun headers(block: HeaderVerifier.() -> Unit) {
        block(HeaderVerifier(request.headers))
    }

    fun bodyFormUrl(block: BodyFormUrlVerifier.() -> Unit) {
        block(BodyFormUrlVerifier(request.body))
    }

    fun bodyJson(block: BodyJsonVerifier.() -> Unit) {
        block(BodyJsonVerifier(request.body.readUtf8()))
    }


    fun path(): String {
        return request.path
    }

    fun assertMethod(method: String) {
        assertEquals(method, request.method)
    }

    fun assertPath(path: String) {
        assertEquals(path, request.requestUrl.encodedPath())
    }

    fun assertHeader(name: String, value: String) {
        assertEquals(value, request.headers.get(name))
    }

    fun assertQuery(key: String, value: String) {
        assertEquals(value, request.requestUrl.queryParameter(key))
    }

    fun assertNoQuery(key: String) {
        assertNull(request.requestUrl.queryParameter(key))
    }

    fun assertEncodedQuery(value: String) {
        assertEquals(value, request.requestUrl.encodedQuery())
    }

    fun assertQuery(key: String, value: Long) {
        assertEquals(value.toString(), request.requestUrl.queryParameter(key))
    }

    fun assertQuery(key: String, value: Int) {
        assertEquals(value.toString(), request.requestUrl.queryParameter(key))
    }
}

class HeaderVerifier(val headers: Headers) {

    fun assertHeader(name: String, value: String?) {
        assertEquals(value, headers[name])
    }

    fun assertNoHeader(name: String) {
        assertNull(headers[name])
    }

    fun assertHeaderPresent(name: String) {
        assertNotNull(headers[name])
    }

    fun authorization(block: AuthorizationVerifier.() -> Unit) {
        assertNotNull(this.headers["Authorization"])
        block(AuthorizationVerifier(this.headers["Authorization"]!!))
    }
}

class BodyFormUrlVerifier(val body: Buffer) {

    private val params: Map<String, String>

    init {
        params = body.readString(Charset.forName("UTF-8")).split("&")
            .map {
                val keyValue = it.split("=")
                // @Suppress("DEPRECATION")
                keyValue[0].trim() to URLDecoder.decode(keyValue[1].trim(), "UTF-8")!!
            }.toMap()
    }

    operator fun get(name: String): String? = params[name]

    fun assertParam(name: String, value: String) {
        assertEquals("Form URL parameter with name: $name has different value", value, params[name])
    }

    fun assertNoParam(name: String) {
        assertNull("Form URL parameter with name: $name is not null", params[name])
    }

    fun assertParam(name: String, value: Long) {
        assertEquals(
            "Form URL parameter with name: $name has different value",
            value.toString(),
            params[name]
        )
    }

    fun assertParam(name: String, value: Int) {
        assertParam(name, value.toLong())
    }
}

class BodyJsonVerifier(val body: String) {

    private val jsonObject = JSONObject(body)

    fun <T> assertValue(key: String, expectedValue: T) {
        assertEquals(expectedValue, jsonObject.get(key))
    }

    fun <T> assertObjectInArray(withKeyArray: String, withKeyObject: String, expectedValue: T) {
        assertEquals(expectedValue, (jsonObject.get(withKeyArray) as JSONArray).getJSONObject(0).get(withKeyObject))
    }
}

@Suppress("unused", "MemberVisibilityCanBePrivate")
class AuthorizationVerifier(digest: String) {

    val map: Map<String, String>

    init {
        map = digest.substring(digest.indexOf(" ")).split(",")
            .map {
                val keyValue = it.trim().split("=", limit = 2)
                keyValue[0].trim() to keyValue[1].trim()
            }
            .toMap()
    }

    fun assertUserName(userName: String) {
        assertValue("username", "\"$userName\"")
    }

    fun assertNonce(nonce: String) {
        assertValue("nonce", "\"$nonce\"")
    }

    fun assertRealm(realm: String) {
        assertValue("realm", "\"$realm\"")
    }

    fun assertAlgorithm(algorithm: String) {
        assertValue("algorithm", algorithm)
    }

    fun assertUri(uri: String) {
        assertValue("uri", "\"$uri\"")
    }

    fun assertQop(qop: String) {
        assertValue("qop", qop)
    }

    fun assertNoQop() {
        assertNoValue("qop")
    }

    fun assertNc(nc: String) {
        assertValue("nc", nc)
    }

    fun assertNc(nc: Int) {
        assertValue("nc", nc)
    }

    fun assertNoNc() {
        return assertNoValue("nc")
    }

    fun assertCnonce(cnonce: String) {
        assertValue("cnonce", "\"$cnonce\"")
    }

    fun assertCnonceNotNull() {
        assertNotNullValue("cnonce")
    }

    fun assertNoCnonce() {
        assertNoValue("cnonce")
    }

    fun assertResponse(response: String) {
        assertValue("response", "\"$response\"")
    }

    fun assertResponseNotNull() {
        assertNotNullValue("response")
    }

    fun assertOpaque(opaque: String) {
        assertValue("opaque", "\"$opaque\"")
    }

    fun assertNoValue(key: String) {
        assertNull(map[key])
    }

    fun assertValue(key: String, value: String) {
        assertEquals("Value for $key is not as expected", value, map[key])
    }

    fun assertValue(key: String, value: Int) {
        assertEquals("Value for $key is not as expected", value, map[key]?.toInt())
    }

    fun assertNotNullValue(key: String) {
        assertNotNull(map[key])
    }
}
//endregion
