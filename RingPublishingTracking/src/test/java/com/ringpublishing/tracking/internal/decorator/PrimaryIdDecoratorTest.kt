/*
 *  Created by Grzegorz Małopolski on 10/7/21, 12:31 PM
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

class PrimaryIdDecoratorTest
{

	@Before
	fun before()
	{
		MockKAnnotations.init(this, relaxUnitFun = true)
		Logger.debugLogEnabled(true)
	}

	@Test
	fun decorate_WhenPrimaryIdIsNotSet_ThenGenerateNewValue()
	{
		val primaryIdDecorator = PrimaryIdDecorator(ConfigurationManager())

		val event = Event("", "")
		primaryIdDecorator.decorate(event)

		val result = event.parameters[EventParam.PRIMARY_ID.paramName] as String?

		Assert.assertFalse(result.isNullOrEmpty())
	}

	@Test
	fun decorate_WhenPrimaryIdIsSet_ThenUseConfigurationValue()
	{
		val configurationDelegate = ConfigurationManager()
		configurationDelegate.primaryId = "100"

		val primaryIdDecorator = PrimaryIdDecorator(configurationDelegate)

		val event = Event("", "")
		primaryIdDecorator.decorate(event)

		val result = event.parameters[EventParam.PRIMARY_ID.paramName] as String?

		Assert.assertTrue(result != null)
		Assert.assertTrue(result!! == "100")
	}

	@Test
	fun decorate_WhenPrimaryIdIsReset_ThenGenerateNewValue()
	{
		val configurationDelegate = ConfigurationManager()
		configurationDelegate.primaryId = "100"

		val primaryIdDecorator = PrimaryIdDecorator(configurationDelegate)

		configurationDelegate.newPrimaryId()

		val event = Event("", "")
		primaryIdDecorator.decorate(event)

		val result = event.parameters[EventParam.PRIMARY_ID.paramName] as String?

		Assert.assertTrue(result != null)
		Assert.assertTrue(result!! != "100")
	}
}
