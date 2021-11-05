/*
 *  Created by Grzegorz Małopolski on 10/27/21, 10:39 AM
 * Copyright © 2021 Ringier Axel Springer Tech. All rights reserved.
 *
 */

package com.ringpublishing.tracking.internal.service.builder

import com.ringpublishing.tracking.internal.api.data.User
import com.ringpublishing.tracking.internal.api.response.IdentifyResponse
import com.ringpublishing.tracking.internal.log.Logger
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import org.junit.Assert
import org.junit.Before
import org.junit.Test

internal class IdentifyRequestBuilderTest
{

	@MockK
	lateinit var user: User

	@MockK
	lateinit var identifyResponse: IdentifyResponse

	@Before
	fun before()
	{
		MockKAnnotations.init(this, relaxUnitFun = true)
		Logger.debugLogEnabled(false)
	}

	@Test
	fun build_WhenAllParameters_ThenParametersAdded()
	{
		every { identifyResponse.getIdentifier() } returns "Identifier"
		val identifyRequestBuilder = IdentifyRequestBuilder(user, identifyResponse)

		val identifyRequest = identifyRequestBuilder.build()

		Assert.assertNotNull(identifyRequest.ids)
		Assert.assertNotNull(identifyRequest.user)

		Assert.assertEquals("Identifier", identifyRequest.ids["eaUUID"])
	}

	@Test
	fun build_WhenNoParameters_ThenNoUserAndEmptyList()
	{
		every { identifyResponse.getIdentifier() } returns "Identifier"
		val identifyRequestBuilder = IdentifyRequestBuilder()

		val identifyRequest = identifyRequestBuilder.build()

		Assert.assertNotNull(identifyRequest.ids)
		Assert.assertNull(identifyRequest.user)
		Assert.assertTrue(identifyRequest.ids.isEmpty())
	}
}
