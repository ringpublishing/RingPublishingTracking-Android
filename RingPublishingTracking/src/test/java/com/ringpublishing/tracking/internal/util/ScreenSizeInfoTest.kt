/*
 *  Created by Grzegorz Małopolski on 10/26/21, 10:06 AM
 * Copyright © 2021 Ringier Axel Springer Tech. All rights reserved.
 *
 */

package com.ringpublishing.tracking.internal.util

import android.content.Context
import android.hardware.display.DisplayManager
import android.util.DisplayMetrics
import android.view.Display
import com.ringpublishing.tracking.internal.data.WindowSize
import com.ringpublishing.tracking.internal.log.Logger
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class ScreenSizeInfoTest
{
	@MockK
	lateinit var context: Context

	@MockK
	lateinit var displayManager: DisplayManager

	@MockK
	lateinit var display: Display

	@Before
	fun before()
	{
		MockKAnnotations.init(this, relaxUnitFun = true)
		Logger.debugLogEnabled(true)
	}

	@Test
	fun getScreenSizePxFromMetrics_WhenDIsplayMetricsCorrect_ThenResultSizesCorrect()
	{
		every { displayManager.getDisplay(any()) } returns display
		every { context.getSystemService(Context.DISPLAY_SERVICE) } returns displayManager

		val metrics = DisplayMetrics()
		metrics.widthPixels = 5
		metrics.heightPixels = 5

		val screenSizeInfo = ScreenSizeInfo(context, metrics)

		val windowSize = screenSizeInfo.getScreenSizePxFromMetrics()

		Assert.assertEquals(5, windowSize.width)
		Assert.assertEquals(5, windowSize.height)
	}

	@Test
	fun getScreenSizeDpString_WhenDensityBigger_ThenResultDpSmallerThanPx()
	{
		every { displayManager.getDisplay(any()) } returns display
		every { context.getSystemService(Context.DISPLAY_SERVICE) } returns displayManager

		val metrics = DisplayMetrics()
		metrics.widthPixels = 6
		metrics.heightPixels = 6
		metrics.density = 2.0f

		val screenSizeInfo = ScreenSizeInfo(context, metrics)

		val dpString = screenSizeInfo.getScreenSizeDpString()
		Assert.assertEquals("3x3", dpString)
	}

	@Test
	fun getWindowSizeDpString_WhenDensityBigger_ThenSizeCalculated()
	{
		every { displayManager.getDisplay(any()) } returns display
		every { context.getSystemService(Context.DISPLAY_SERVICE) } returns displayManager

		val metrics = DisplayMetrics()
		metrics.density = 2.0f

		val screenSizeInfo = ScreenSizeInfo(context, metrics)

		val windowSize = WindowSize(6, 6)

		val windowSizeDp = screenSizeInfo.getWindowSizeDpString(windowSize)

		Assert.assertEquals("3x3", windowSizeDp)
	}

	@Test
	fun getSizeDp()
	{
		every { displayManager.getDisplay(any()) } returns display
		every { context.getSystemService(Context.DISPLAY_SERVICE) } returns displayManager

		val metrics = DisplayMetrics()
		metrics.density = 2.0f

		val screenSizeInfo = ScreenSizeInfo(context, metrics)

		val windowSizeDp = screenSizeInfo.getSizeDp(6)

		Assert.assertEquals(3, windowSizeDp)
	}
}
