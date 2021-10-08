/*
 *  Created by Grzegorz Małopolski on 10/7/21, 2:22 PM
 * Copyright © 2021 Ringier Axel Springer Tech. All rights reserved.
 *
 */

package com.ringpublishing.tracking.internal.decorator

import android.content.Context
import android.content.res.Resources
import android.util.DisplayMetrics
import com.ringpublishing.tracking.data.Event
import com.ringpublishing.tracking.internal.log.Logger
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class DeviceScreenDecoratorTest
{

	@MockK
	lateinit var context: Context

	@MockK
	lateinit var resources: Resources

	@MockK
	lateinit var metrics: DisplayMetrics

	@Before
	fun before()
	{
		MockKAnnotations.init(this, relaxUnitFun = true)
		Logger.debugLogEnabled(true)
	}

	@Test
	fun decorate_WhenNormalDensity_ThenSizeSame()
	{
		metrics.widthPixels = 10
		metrics.heightPixels = 10
		metrics.density = 1F
		every { resources.displayMetrics } returns metrics
		every { context.resources } returns resources

		val deviceScreenDecorator = DeviceScreenDecorator(context)

		val event = Event()
		deviceScreenDecorator.decorate(event)

		val result = event.parameters[EventParam.SCREEN_SIZE.paramName] as String?

		Assert.assertEquals("10x10x24", result)
	}

	@Test
	fun decorate_WhenDoubleDensity_ThenSizeDecreased()
	{
		metrics.widthPixels = 10
		metrics.heightPixels = 10
		metrics.density = 2F
		every { resources.displayMetrics } returns metrics
		every { context.resources } returns resources

		val deviceScreenDecorator = DeviceScreenDecorator(context)

		val event = Event()
		deviceScreenDecorator.decorate(event)

		val result = event.parameters[EventParam.SCREEN_SIZE.paramName] as String?

		Assert.assertEquals("5x5x24", result)
	}
}
