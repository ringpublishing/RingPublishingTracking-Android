/*
 *  Created by Grzegorz Małopolski on 10/7/21, 12:59 PM
 * Copyright © 2021 Ringier Axel Springer Tech. All rights reserved.
 *
 */

package com.ringpublishing.tracking.internal.decorator

import com.ringpublishing.tracking.data.Event
import com.ringpublishing.tracking.data.RingPublishingTrackingConfiguration
import com.ringpublishing.tracking.internal.delegate.ConfigurationManager
import com.ringpublishing.tracking.internal.log.Logger
import io.mockk.MockKAnnotations
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import java.net.URL

class SecondaryIdDecoratorTest
{

	@Before
	fun before()
	{
		MockKAnnotations.init(this, relaxUnitFun = true)
		Logger.debugLogEnabled(true)
	}

	@Test
	fun decorate_WhenSecondaryIdIsNotUpdated_ThenUseValueFromPrimaryId()
	{
		val configurationManager = ConfigurationManager()
		val ringPublishingTrackingConfiguration = RingPublishingTrackingConfiguration(
			"",
			"",
			URL("https://domain.com"),
			"rootPath",
		)
		configurationManager.initializeConfiguration(ringPublishingTrackingConfiguration)
		val secondaryIdDecorator = SecondaryIdDecorator(configurationManager)
		val event = Event()

		secondaryIdDecorator.decorate(event)

		val result = event.parameters[EventParam.SECONDARY_ID.text] as String?
		Assert.assertTrue(result != null)
	}

	@Test
	fun decorate_WhenPrimaryIdIsSetAndFullView_ThenResultSameLikePrimaryId()
	{
		val configurationManager = ConfigurationManager()
		val ringPublishingTrackingConfiguration = RingPublishingTrackingConfiguration(
			"",
			"",
			URL("https://domain.com"),
			"rootPath",
		)
		configurationManager.initializeConfiguration(ringPublishingTrackingConfiguration)
		val secondaryIdDecorator = SecondaryIdDecorator(configurationManager)
		val event = Event()

		configurationManager.updatePartiallyReloaded(false)
		secondaryIdDecorator.decorate(event)

		val result = event.parameters[EventParam.SECONDARY_ID.text] as String?

		Assert.assertTrue(result != null)
		Assert.assertTrue(result!! == configurationManager.primaryId)
	}

	@Test
	fun decorate_WhenPrimaryIdIsSetAndPartialView_ThenResultHaveNewValue()
	{
		val configurationManager = ConfigurationManager()
		val ringPublishingTrackingConfiguration = RingPublishingTrackingConfiguration(
			"",
			"",
			URL("https://domain.com"),
			"rootPath",
		)
		configurationManager.initializeConfiguration(ringPublishingTrackingConfiguration)
		val secondaryIdDecorator = SecondaryIdDecorator(configurationManager)
		val event = Event()

		configurationManager.updatePartiallyReloaded(true)
		secondaryIdDecorator.decorate(event)

		val result = event.parameters[EventParam.SECONDARY_ID.text] as String?

		Assert.assertTrue(result != null)
		Assert.assertTrue(result!! != configurationManager.primaryId)
		Assert.assertFalse(result.isEmpty())
	}
}
