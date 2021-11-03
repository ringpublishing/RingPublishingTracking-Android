/*
 *  Created by Grzegorz Małopolski on 10/29/21, 12:01 PM
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

internal class ReferrerDecoratorTest
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
	fun decorate()
	{
		every { configurationManager.currentReferrer } returns "referrer"
		val referrerDecorator = ReferrerDecorator(configurationManager)

		val event = Event()

		referrerDecorator.decorate(event)

		Assert.assertEquals("referrer", event.parameters["DR"])
	}
}
