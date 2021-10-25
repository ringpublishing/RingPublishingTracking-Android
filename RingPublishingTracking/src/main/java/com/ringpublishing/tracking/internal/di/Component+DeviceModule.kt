package com.ringpublishing.tracking.internal.di

import com.ringpublishing.tracking.internal.device.AdvertisingInfo
import com.ringpublishing.tracking.internal.device.DeviceInfo
import com.ringpublishing.tracking.internal.device.WindowSizeInfo
import com.ringpublishing.tracking.internal.util.ScreenSizeInfo

private val deviceInfo: DeviceInfo by lazy { DeviceInfo(Component.provideContext()) }

private val advertisingInfo: AdvertisingInfo by lazy { AdvertisingInfo(Component.provideContext()) }

internal fun Component.provideDeviceInfo(): DeviceInfo = deviceInfo

internal fun Component.provideAdvertisingInfo(): AdvertisingInfo = advertisingInfo

private val screenSizeInfo: ScreenSizeInfo by lazy {ScreenSizeInfo(Component.provideApplication())}

internal fun Component.provideScreenSizeInfo(): ScreenSizeInfo = screenSizeInfo

private val windowSizeInfo: WindowSizeInfo by lazy {WindowSizeInfo(Component.provideScreenSizeInfo(), Component.provideApplication())}

internal fun Component.provideWindowSizeInfo(): WindowSizeInfo = windowSizeInfo
