package com.ringpublishing.tracking.internal.service.builder

import com.ringpublishing.tracking.internal.api.data.User
import com.ringpublishing.tracking.internal.device.AdvertisingInfo
import com.ringpublishing.tracking.internal.device.DeviceInfo
import com.ringpublishing.tracking.internal.repository.UserRepository

internal class UserBuilder(
    private val advertisingInfo: AdvertisingInfo,
    private val deviceInfo: DeviceInfo,
    private val userRepository: UserRepository
)
{

    fun build(): User
    {
        val advertisingId = advertisingInfo.readAdvertisingId()
        var deviceId: String? = null

        if (advertisingId.isNullOrEmpty())
        {
            deviceId = userRepository.readDeviceId()

            if (deviceId.isNullOrEmpty())
            {
                deviceId = deviceInfo.getDeviceId()
                userRepository.saveDeviceId(deviceId)
            }
        }

        return User(advertisingId, deviceId)
    }
}
