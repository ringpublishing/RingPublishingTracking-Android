/*
 *  Created by Grzegorz Małopolski on 10/29/21, 12:23 PM
 * Copyright © 2021 Ringier Axel Springer Tech. All rights reserved.
 *
 */

package com.ringpublishing.tracking.internal.device

import android.app.Application
import android.view.WindowManager
import com.ringpublishing.tracking.internal.data.WindowSize
import com.ringpublishing.tracking.internal.log.Logger
import com.ringpublishing.tracking.internal.util.ScreenSizeInfo
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import org.junit.Assert
import org.junit.Before
import org.junit.Test

internal class WindowSizeInfoTest
{

	@MockK
	lateinit var screenSizeInfo: ScreenSizeInfo

	@MockK
	lateinit var application: Application

	@MockK
	lateinit var windowManager: WindowManager

	@Before
	fun before()
	{
		MockKAnnotations.init(this, relaxUnitFun = true)
		Logger.debugLogEnabled(true)
	}

	@Test
	fun getWindowSizeDpString()
	{
		every { application.getSystemService(any()) } returns windowManager

		every { screenSizeInfo.getScreenSizePxFromMetrics() } returns WindowSize(1, 1)
		every { screenSizeInfo.getWindowSizeDpString(any()) } returns "1x1"
		val windowSizeInfo = WindowSizeInfo(screenSizeInfo, application)

		Assert.assertEquals("1x1", windowSizeInfo.getWindowSizeDpString())
	}
}
