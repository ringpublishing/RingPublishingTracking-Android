/*
 *  Created by Grzegorz Małopolski on 10/7/21, 2:48 PM
 * Copyright © 2021 Ringier Axel Springer Tech. All rights reserved.
 *
 */

package com.ringpublishing.tracking.internal.decorator

import com.ringpublishing.tracking.data.Event
import com.ringpublishing.tracking.data.RingPublishingTrackingConfiguration
import com.ringpublishing.tracking.internal.ConfigurationManager
import com.ringpublishing.tracking.internal.log.Logger
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import java.net.URL

class SiteAreaDecoratorTest
{
    @MockK
    lateinit var ringPublishingConfiguration: RingPublishingTrackingConfiguration

    @Before
    fun before()
    {
        MockKAnnotations.init(this, relaxUnitFun = true)
        Logger.debugLogEnabled(true)
    }

    @Test
	fun decorate_WhenSetArea_ThenIsInResult()
	{
        every { ringPublishingConfiguration.applicationDefaultAdvertisementArea } returns ""
        every { ringPublishingConfiguration.applicationDefaultStructurePath } returns listOf()
        every { ringPublishingConfiguration.applicationRootPath } returns ""
        every { ringPublishingConfiguration.advertisementSite } returns "test-site"

        val configurationManager = ConfigurationManager()
        configurationManager.initializeConfiguration(ringPublishingConfiguration)

		configurationManager.updateAdvertisementArea("area")
		val decorator = SiteAreaDecorator(configurationManager)

		val event = Event()
		decorator.decorate(event)

		val result = event.parameters[EventParam.SITE_AREA.text] as String?

		Assert.assertEquals("test-site/area", result)
	}

	@Test
	fun decorate_WhenDefaultArea_ThenIsInResult()
	{
		val configurationManager = ConfigurationManager()
		val ringPublishingTrackingConfiguration = RingPublishingTrackingConfiguration(
			"",
			"",
			URL("https://domain.com"),
			"",
			emptyList<String>(),
			"defaultArea"
		)
		configurationManager.initializeConfiguration(ringPublishingTrackingConfiguration)
		val decorator = SiteAreaDecorator(configurationManager)

		val event = Event()
		decorator.decorate(event)

		val result = event.parameters[EventParam.SITE_AREA.text] as String?

		Assert.assertSame("defaultArea", result)
	}

	@Test
	fun decorate_WhenDefaultAreaAndAreaSet_ThenLocalArea()
	{
		val configurationManager = ConfigurationManager()
		val ringPublishingTrackingConfiguration = RingPublishingTrackingConfiguration(
			"",
			"",
			URL("https://domain.com"),
			"",
			emptyList<String>(),
			"defaultArea"
		)
		configurationManager.initializeConfiguration(ringPublishingTrackingConfiguration)
		configurationManager.updateAdvertisementArea("area")

		val decorator = SiteAreaDecorator(configurationManager)

		val event = Event()
		decorator.decorate(event)

		val result = event.parameters[EventParam.SITE_AREA.text] as String?

		Assert.assertSame("area", result)
	}
}
