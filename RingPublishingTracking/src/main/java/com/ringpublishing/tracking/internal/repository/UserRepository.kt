package com.ringpublishing.tracking.internal.repository

import com.ringpublishing.tracking.internal.device.AdvertisingInfo
import com.ringpublishing.tracking.internal.device.DeviceInfo
import com.ringpublishing.tracking.internal.service.builder.UserBuilder

internal class UserRepository(
    private val advertisingInfo: AdvertisingInfo,
    private val deviceInfo: DeviceInfo,
    private val dataRepository: DataRepository
)
{
    private enum class Key(val text: String) {
        USER_DEVICE_ID("userDeviceId")
    }

    fun saveDeviceId(deviceId: String) = dataRepository.saveString(Key.USER_DEVICE_ID.text, deviceId)

    fun readDeviceId() = dataRepository.readString(Key.USER_DEVICE_ID.text)

    fun buildUser() = UserBuilder(advertisingInfo, deviceInfo, this).build()
}
