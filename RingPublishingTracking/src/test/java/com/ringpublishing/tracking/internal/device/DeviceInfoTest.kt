/*
 *  Created by Grzegorz Małopolski on 10/29/21, 12:14 PM
 * Copyright © 2021 Ringier Axel Springer Tech. All rights reserved.
 *
 */

package com.ringpublishing.tracking.internal.device

import android.content.Context
import android.content.res.Configuration
import com.ringpublishing.tracking.internal.log.Logger
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class DeviceInfoTest
{

	@MockK
	lateinit var context: Context

	@MockK
	lateinit var configuration: Configuration

	@Before
	fun before()
	{
		MockKAnnotations.init(this, relaxUnitFun = true)
		Logger.debugLogEnabled(true)
	}

	@Test
	fun isTablet_WhenDefaultValues_ThenFalse()
	{
		every { context.resources.configuration } returns configuration
		val deviceInfo = DeviceInfo(context)

		Assert.assertFalse(deviceInfo.isTablet())
	}

	@Test
	fun getDeviceId_WhenCalled_ThenReturnNotEmptyId()
	{
		every { context.resources.configuration } returns configuration
		val deviceInfo = DeviceInfo(context)

		Assert.assertTrue(deviceInfo.getDeviceId().isNotEmpty())
	}
}
