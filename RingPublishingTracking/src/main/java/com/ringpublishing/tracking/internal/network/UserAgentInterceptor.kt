package com.ringpublishing.tracking.internal.network

import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

internal class UserAgentInterceptor(private val userAgentValue: String) : Interceptor
{

    private val keyUserAgentHeader = "User-Agent"

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response
    {
        val originalRequest: Request = chain.request()

        val requestWithUserAgent = originalRequest.newBuilder()
            .removeHeader(keyUserAgentHeader)
            .addHeader(keyUserAgentHeader, userAgentValue)
            .build()

        return chain.proceed(requestWithUserAgent)
    }
}
