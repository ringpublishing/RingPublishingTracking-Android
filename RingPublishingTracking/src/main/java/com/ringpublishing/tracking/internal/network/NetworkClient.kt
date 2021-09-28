package com.ringpublishing.tracking.internal.network

import okhttp3.OkHttpClient

internal class NetworkClient(private val userAgentInterceptorInfo: UserAgentInterceptorInfo)
{

    private val client: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .apply { addNetworkInterceptor(UserAgentInterceptor(userAgentInterceptorInfo.getHeader())) }
            .build()
    }

    fun get() = client
}
