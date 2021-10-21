/*
 *  Created by Grzegorz Małopolski on 10/7/21, 2:42 PM
 * Copyright © 2021 Ringier Axel Springer Tech. All rights reserved.
 *
 */

package com.ringpublishing.tracking.internal.decorator

import android.app.Application
import android.content.res.Resources
import android.util.DisplayMetrics
import android.view.WindowManager
import com.ringpublishing.tracking.data.Event
import com.ringpublishing.tracking.internal.device.WindowSizeInfo
import com.ringpublishing.tracking.internal.log.Logger
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockkStatic
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class WindowSizeDecoratorTest
{

	@MockK
	lateinit var application: Application

	@MockK
	lateinit var resources: Resources

	@MockK
	lateinit var metrics: DisplayMetrics

	@MockK
	lateinit var windowManager: WindowManager

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
		mockkStatic(Resources::class)
		every { Resources.getSystem().displayMetrics } returns metrics

		every { application.getSystemService(any()) } returns windowManager

		val windowSizeInfo = WindowSizeInfo(application)
		val decorator = WindowSizeDecorator(windowSizeInfo)

		val event = Event()
		decorator.decorate(event)

		val result = event.parameters[EventParam.WINDOW_SIZE.text] as String?

		Assert.assertEquals("10x10", result)
	}
}
