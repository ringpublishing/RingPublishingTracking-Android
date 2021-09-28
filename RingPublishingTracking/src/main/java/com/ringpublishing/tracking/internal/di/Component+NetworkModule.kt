package com.ringpublishing.tracking.internal.di

import com.ringpublishing.tracking.internal.network.NetworkClient
import com.ringpublishing.tracking.internal.network.UserAgentInterceptorInfo

private val userAgentInterceptorInfo: UserAgentInterceptorInfo by lazy {
    UserAgentInterceptorInfo(Component.provideContext(), Component.provideDeviceInfo())
}

internal fun Component.provideUserAgentInterceptorInfo(): UserAgentInterceptorInfo = userAgentInterceptorInfo

private val networkClient: NetworkClient by lazy { NetworkClient(Component.provideUserAgentInterceptorInfo()) }

internal fun Component.provideNetworkClient(): NetworkClient = networkClient
