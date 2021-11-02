/*
 *  Created by Grzegorz Małopolski on 10/29/21, 11:58 AM
 * Copyright © 2021 Ringier Axel Springer Tech. All rights reserved.
 *
 */

package com.ringpublishing.tracking.internal.decorator

import com.ringpublishing.tracking.data.Event
import com.ringpublishing.tracking.internal.ConfigurationManager
import com.ringpublishing.tracking.internal.log.Logger
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import org.junit.Assert
import org.junit.Before
import org.junit.Test

internal class PrimaryIdDecoratorTest
{

	@MockK
	lateinit var configurationManager: ConfigurationManager

	@Before
	fun before()
	{
		MockKAnnotations.init(this, relaxUnitFun = true)
		Logger.debugLogEnabled(true)
	}

	@Test
	fun decorate_WhenEventDecorated_ThenParametersHavePrimaryId()
	{
		every { configurationManager.primaryId } returns "primaryId"
		val primaryIdDecorator = PrimaryIdDecorator(configurationManager)

		val event = Event()
		primaryIdDecorator.decorate(event)

		Assert.assertEquals("primaryId", event.parameters["IP"])
	}
}
