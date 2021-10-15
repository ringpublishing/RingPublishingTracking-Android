package com.ringpublishing.tracking.internal.di

import com.ringpublishing.tracking.internal.device.AdvertisingInfo
import com.ringpublishing.tracking.internal.device.DeviceInfo
import com.ringpublishing.tracking.internal.device.WindowSizeInfo

private val deviceInfo: DeviceInfo by lazy { DeviceInfo(Component.provideContext()) }

private val advertisingInfo: AdvertisingInfo by lazy { AdvertisingInfo(Component.provideContext()) }

internal fun Component.provideDeviceInfo(): DeviceInfo = deviceInfo

internal fun Component.provideAdvertisingInfo(): AdvertisingInfo = advertisingInfo

private val windowSizeInfo: WindowSizeInfo by lazy {WindowSizeInfo(Component.provideApplication())}

internal fun Component.provideWindowSizeInfo(): WindowSizeInfo = windowSizeInfo
