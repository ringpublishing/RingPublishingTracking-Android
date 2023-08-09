/*
 *  Created by Grzegorz Małopolski on 10/27/21, 10:51 AM
 * Copyright © 2021 Ringier Axel Springer Tech. All rights reserved.
 *
 */

package com.ringpublishing.tracking.internal.service.builder

import com.ringpublishing.tracking.internal.device.AdvertisingInfo
import com.ringpublishing.tracking.internal.device.DeviceInfo
import com.ringpublishing.tracking.internal.log.Logger
import com.ringpublishing.tracking.internal.repository.UserRepository
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import org.junit.Assert
import org.junit.Before
import org.junit.Test

internal class UserBuilderTest
{

	@MockK
	lateinit var advertisingInfo: AdvertisingInfo

	@MockK
	lateinit var deviceInfo: DeviceInfo

	@MockK
	lateinit var userRepository: UserRepository

	@Before
	fun before()
	{
		MockKAnnotations.init(this, relaxUnitFun = true)
		Logger.debugLogEnabled(false)
	}

	@Test
	fun build_WhenAdvertisementId_ThenIsInUserAndDeviceIdIsNull()
	{
		every { advertisingInfo.readAdvertisingId() } returns "id"

		val userBuilder = UserBuilder(mockk(relaxed = true), advertisingInfo, deviceInfo, userRepository)

		val user = userBuilder.build()

		Assert.assertEquals("id", user.advertisementId)
		Assert.assertNull(user.deviceId)
	}

	@Test
	fun build_WhenNoAdvertisementId_ThenUserHaveDeviceId()
	{
		every { advertisingInfo.readAdvertisingId() } returns null
		every { deviceInfo.getDeviceId() } returns "deviceId"
		every { userRepository.readDeviceId() } returns null

		val userBuilder = UserBuilder(mockk(relaxed = true), advertisingInfo, deviceInfo, userRepository)

		val user = userBuilder.build()

		Assert.assertNull(user.advertisementId)
		Assert.assertEquals("deviceId", user.deviceId)
	}

	@Test
	fun build_WhenNoAdvertisementIdAndHaveDeviceIdInRepository_ThenUserHaveDeviceIdFromRepository()
	{
		every { advertisingInfo.readAdvertisingId() } returns null
		every { userRepository.readDeviceId() } returns "deviceId"

		val userBuilder = UserBuilder(mockk(relaxed = true), advertisingInfo, deviceInfo, userRepository)

		val user = userBuilder.build()

		Assert.assertNull(user.advertisementId)
		Assert.assertEquals("deviceId", user.deviceId)
	}
}
