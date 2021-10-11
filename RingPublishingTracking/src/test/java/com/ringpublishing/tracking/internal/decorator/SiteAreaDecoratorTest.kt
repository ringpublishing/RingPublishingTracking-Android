/*
 *  Created by Grzegorz Małopolski on 10/7/21, 2:48 PM
 * Copyright © 2021 Ringier Axel Springer Tech. All rights reserved.
 *
 */

package com.ringpublishing.tracking.internal.decorator

import com.ringpublishing.tracking.data.Event
import com.ringpublishing.tracking.data.RingPublishingTrackingConfiguration
import com.ringpublishing.tracking.internal.delegate.ConfigurationManager
import org.junit.Assert
import org.junit.Test
import java.net.URL

class SiteAreaDecoratorTest
{

	@Test
	fun decorate_WhenSetArea_ThenIsInResult()
	{
		val configurationManager = ConfigurationManager()
		configurationManager.updateAdvertisementArea("area")
		val decorator = SiteAreaDecorator(configurationManager)

		val event = Event()
		decorator.decorate(event)

		val result = event.parameters[EventParam.SITE_AREA.paramName] as String?

		Assert.assertSame("area", result)
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

		val result = event.parameters[EventParam.SITE_AREA.paramName] as String?

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

		val result = event.parameters[EventParam.SITE_AREA.paramName] as String?

		Assert.assertSame("area", result)
	}
}
