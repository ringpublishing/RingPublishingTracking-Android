/*
 *  Created by Grzegorz Małopolski on 10/29/21, 12:07 PM
 * Copyright © 2021 Ringier Axel Springer Tech. All rights reserved.
 *
 */

package com.ringpublishing.tracking.internal.decorator

import com.ringpublishing.tracking.data.Event
import com.ringpublishing.tracking.internal.device.WindowSizeInfo
import com.ringpublishing.tracking.internal.log.Logger
import com.ringpublishing.tracking.internal.util.ScreenSizeInfo
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import org.junit.Assert
import org.junit.Before
import org.junit.Test

internal class WindowSizeDecoratorTest
{

	@MockK
	lateinit var windowSizeInfo: WindowSizeInfo

	@MockK
	lateinit var screenSizeInfo: ScreenSizeInfo

	@Before
	fun before()
	{
		MockKAnnotations.init(this, relaxUnitFun = true)
		Logger.debugLogEnabled(true)
	}

	@Test
	fun decorate()
	{
		every { windowSizeInfo.getWindowSizeDpString() } returns "1x1"
		val windowSizeDecorator = WindowSizeDecorator(windowSizeInfo, screenSizeInfo)

		val event = Event()
		windowSizeDecorator.decorate(event)

		Assert.assertEquals("1x1", event.parameters["CW"])
	}
}
