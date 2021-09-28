package com.ringpublishing.tracking.internal.di

import com.ringpublishing.tracking.internal.device.AdvertisingInfo
import com.ringpublishing.tracking.internal.device.DeviceInfo

private val deviceInfo: DeviceInfo by lazy { DeviceInfo(Component.provideResources()) }

private val advertisingInfo: AdvertisingInfo by lazy { AdvertisingInfo(Component.provideContext()) }

internal fun Component.provideDeviceInfo(): DeviceInfo = deviceInfo

internal fun Component.provideAdvertisingInfo(): AdvertisingInfo = advertisingInfo
