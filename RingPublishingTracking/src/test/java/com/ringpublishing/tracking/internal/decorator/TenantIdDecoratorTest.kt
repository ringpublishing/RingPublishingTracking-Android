/*
 *  Created by Grzegorz Małopolski on 10/29/21, 12:03 PM
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

internal class TenantIdDecoratorTest
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
		every { configurationManager.getTenantId() } returns "tenantId"
		val tenantIdDecorator = TenantIdDecorator(configurationManager)
		val event = Event()

		tenantIdDecorator.decorate(event)

		Assert.assertEquals("tenantId", event.parameters["TID"])
	}
}
