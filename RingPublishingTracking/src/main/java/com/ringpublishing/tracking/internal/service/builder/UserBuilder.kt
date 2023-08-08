package com.ringpublishing.tracking.internal.service.builder

import android.content.Context
import androidx.preference.PreferenceManager
import com.ringpublishing.tracking.internal.api.data.User
import com.ringpublishing.tracking.internal.constants.Constants
import com.ringpublishing.tracking.internal.device.AdvertisingInfo
import com.ringpublishing.tracking.internal.device.DeviceInfo
import com.ringpublishing.tracking.internal.repository.UserRepository

internal class UserBuilder(
    context: Context,
    private val advertisingInfo: AdvertisingInfo,
    private val deviceInfo: DeviceInfo,
    private val userRepository: UserRepository
)
{
	private val sharedPreferences by lazy { PreferenceManager.getDefaultSharedPreferences(context) }

	fun build(): User
    {
	    val advertisingId = advertisingInfo.readAdvertisingId()
		val userConsent = sharedPreferences.getString(Constants.consentStringPreferenceName, null)
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

        return User(advertisingId, userConsent, deviceId)
    }
}
