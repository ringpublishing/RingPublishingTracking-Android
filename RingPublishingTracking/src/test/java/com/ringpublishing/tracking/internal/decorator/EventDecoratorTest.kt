/*
 *  Created by Grzegorz Małopolski on 10/29/21, 10:29 AM
 * Copyright © 2021 Ringier Axel Springer Tech. All rights reserved.
 *
 */

package com.ringpublishing.tracking.internal.decorator

import android.app.Application
import android.content.SharedPreferences
import com.google.gson.Gson
import com.ringpublishing.tracking.data.Event
import com.ringpublishing.tracking.internal.ConfigurationManager
import com.ringpublishing.tracking.internal.data.UserData
import com.ringpublishing.tracking.internal.device.WindowSizeInfo
import com.ringpublishing.tracking.internal.log.Logger
import com.ringpublishing.tracking.internal.util.ScreenSizeInfo
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import org.junit.Assert
import org.junit.Before
import org.junit.Test

internal class EventDecoratorTest
{

	@MockK
	lateinit var configurationManager: ConfigurationManager

	@MockK
	lateinit var application: Application

	@MockK
	lateinit var windowSizeInfo: WindowSizeInfo

	@MockK
	lateinit var screenSizeInfo: ScreenSizeInfo

	@MockK
	lateinit var event: Event

	@MockK
	lateinit var sharedPreferences: SharedPreferences

	@MockK
	lateinit var userData: UserData

	@Before
	fun before()
	{
		MockKAnnotations.init(this, relaxUnitFun = true)
		Logger.debugLogEnabled(true)
	}

	@Test
	fun decorate()
	{
		every { application.packageName } returns "package"
		every { application.getSharedPreferences(any(), any()) } returns sharedPreferences
		every { configurationManager.primaryId } returns "primaryId"
		every { configurationManager.secondaryId } returns "secondaryId"
		every { configurationManager.getTenantId() } returns "tenantId"
		every { configurationManager.getSiteArea() } returns "area"
		every { configurationManager.getUserData() } returns userData
		every { configurationManager.currentContentUrl } returns "contentUrl"
		every { configurationManager.getFullStructurePath() } returns "structurePath"
		every { configurationManager.currentReferrer } returns "referrer"

		every { userData.userId } returns "userId"
		every { userData.emailMd5 } returns "emailMd5"
		every { userData.ssoName } returns "ssoName"

		every { screenSizeInfo.getScreenSizeDpString() } returns "1x1"
		every { windowSizeInfo.getWindowSizeDpString() } returns "1x1"

		every { sharedPreferences.getString(any(), any()) } returns "preference"

		every { event.name } returns "name"

		val parameters = mutableMapOf<String, Any>()

		every { event.parameters } returns parameters

		val eventDecorator = EventDecorator(configurationManager, Gson(), windowSizeInfo, screenSizeInfo)

		val eventDecorated = eventDecorator.decorate(event)

		Assert.assertEquals("name", eventDecorated.name)

		with(eventDecorated)
		{
			Assert.assertEquals("primaryId", parameters["IP"])
			Assert.assertEquals("secondaryId", parameters["IV"])
			Assert.assertEquals("tenantId", parameters["TID"])
			Assert.assertEquals("area", parameters["DA"])
			Assert.assertEquals("1x1x24", parameters["CS"])
			Assert.assertEquals("1x1", parameters["CW"])
			Assert.assertEquals("preference", parameters["_adpc"])
			Assert.assertEquals("contentUrl", parameters["DU"])
			Assert.assertEquals("structurePath", parameters["DV"])
			Assert.assertEquals("referrer", parameters["DR"])
		}
	}
}
