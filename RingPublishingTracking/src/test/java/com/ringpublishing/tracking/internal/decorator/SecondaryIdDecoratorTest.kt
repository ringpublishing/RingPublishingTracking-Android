/*
 *  Created by Grzegorz Małopolski on 10/7/21, 12:59 PM
 * Copyright © 2021 Ringier Axel Springer Tech. All rights reserved.
 *
 */

package com.ringpublishing.tracking.internal.decorator

import com.ringpublishing.tracking.data.Event
import com.ringpublishing.tracking.internal.delegate.ConfigurationManager
import com.ringpublishing.tracking.internal.log.Logger
import io.mockk.MockKAnnotations
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class SecondaryIdDecoratorTest
{

	@Before
	fun before()
	{
		MockKAnnotations.init(this, relaxUnitFun = true)
		Logger.debugLogEnabled(true)
	}

	@Test
	fun decorate_WhenSecondaryIdIsNotSet_ThenUseValueFromPrimaryId()
	{
		val configurationManager = ConfigurationManager()
		configurationManager.primaryId = "100"
		val secondaryIdDecorator = SecondaryIdDecorator(configurationManager)

		val event = Event()
		secondaryIdDecorator.decorate(event)

		val result = event.parameters[EventParam.SECONDARY_ID.paramName] as String?

		Assert.assertTrue(result != null)
		Assert.assertTrue(result!! == "100")
	}

	@Test
	fun decorate_WhenPrimaryIdIsSetAndFullView_ThenResultSameLikePrimaryId()
	{
		val configurationManager = ConfigurationManager()
		configurationManager.primaryId = "100"
		configurationManager.secondaryId = null
		configurationManager.currentIsPartialView = false
		val secondaryIdDecorator = SecondaryIdDecorator(configurationManager)

		val event = Event()
		secondaryIdDecorator.decorate(event)

		val result = event.parameters[EventParam.SECONDARY_ID.paramName] as String?

		Assert.assertTrue(result != null)
		Assert.assertTrue(result!! == "100")
	}

	@Test
	fun decorate_WhenPrimaryIdIsSetAndPartialView_ThenResultHaveNewValue()
	{
		val configurationManager = ConfigurationManager()
		configurationManager.primaryId = "100"
		configurationManager.secondaryId = null
		configurationManager.currentIsPartialView = true
		val secondaryIdDecorator = SecondaryIdDecorator(configurationManager)

		val event = Event()
		secondaryIdDecorator.decorate(event)

		val result = event.parameters[EventParam.SECONDARY_ID.paramName] as String?

		Assert.assertTrue(result != null)
		Assert.assertTrue(result!! != "100")
		Assert.assertFalse(result.isEmpty())
	}
}
