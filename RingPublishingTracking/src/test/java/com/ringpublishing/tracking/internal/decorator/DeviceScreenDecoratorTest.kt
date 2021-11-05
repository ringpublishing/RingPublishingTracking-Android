/*
 *  Created by Grzegorz Małopolski on 10/29/21, 11:51 AM
 * Copyright © 2021 Ringier Axel Springer Tech. All rights reserved.
 *
 */

package com.ringpublishing.tracking.internal.decorator

import com.ringpublishing.tracking.data.Event
import com.ringpublishing.tracking.internal.log.Logger
import com.ringpublishing.tracking.internal.util.ScreenSizeInfo
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import org.junit.Assert
import org.junit.Before
import org.junit.Test

internal class DeviceScreenDecoratorTest
{

	@MockK
	lateinit var screenSizeInfo: ScreenSizeInfo

	@Before
	fun before()
	{
		MockKAnnotations.init(this, relaxUnitFun = true)
		Logger.debugLogEnabled(true)
	}

	@Test
	fun decorate_WhenDecorate_ThenParametersHaveParameterValue()
	{
		every { screenSizeInfo.getScreenSizeDpString() } returns "1x1"
		val deviceScreenDecorator = DeviceScreenDecorator(screenSizeInfo)

		val event = Event()
		deviceScreenDecorator.decorate(event)

		Assert.assertEquals("1x1x24", event.parameters["CS"])
	}
}
