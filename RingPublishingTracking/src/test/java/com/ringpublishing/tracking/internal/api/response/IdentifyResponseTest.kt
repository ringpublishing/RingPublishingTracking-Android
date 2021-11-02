/*
 *  Created by Grzegorz Małopolski on 10/27/21, 3:55 PM
 * Copyright © 2021 Ringier Axel Springer Tech. All rights reserved.
 *
 */

package com.ringpublishing.tracking.internal.api.response

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.ringpublishing.tracking.internal.api.data.IdsMap
import com.ringpublishing.tracking.internal.api.data.Profile
import com.ringpublishing.tracking.internal.log.Logger
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import java.util.Date

internal class IdentifyResponseTest
{

	@MockK
	lateinit var idsMap: IdsMap

	@MockK
	lateinit var profile: Profile

	@MockK
	lateinit var parameters: Map<String, JsonElement>

	@MockK
	lateinit var jsonElement: JsonElement

	@MockK
	lateinit var jsonObject: JsonObject

	@Before
	fun before()
	{
		MockKAnnotations.init(this, relaxUnitFun = true)
		Logger.debugLogEnabled(true)
	}

	@Test
	fun getValidDate_WhenIsNull_ThenReturnNull()
	{
		val identifyResponse = IdentifyResponse(idsMap, profile, 500)

		val result = identifyResponse.getValidDate(null)

		Assert.assertNull(result)
	}

	@Test
	fun getValidDate_WhenLifetime_ThenDateBeforeLifetime()
	{
		every { jsonObject.get("lifetime").asLong } returns 5000L
		every { jsonElement.asJsonObject } returns jsonObject
		every { parameters["eaUUID"] } returns jsonElement
		every { idsMap.parameters } returns parameters
		val lifetime = 5000L
		val identifyResponse = IdentifyResponse(idsMap, profile, lifetime)

		val resultDate = identifyResponse.getValidDate(Date())

		Assert.assertTrue(resultDate!!.before(Date(Date().time + lifetime + lifetime)))
	}

	@Test
	fun getIdentifier_WhenValueExist_ThenReturnCorrectValue()
	{
		every { jsonObject.get("value").asString } returns "value"
		every { jsonElement.asJsonObject } returns jsonObject
		every { parameters["eaUUID"] } returns jsonElement
		every { idsMap.parameters } returns parameters
		val lifetime = 500L
		val identifyResponse = IdentifyResponse(idsMap, profile, lifetime)

		val identifier = identifyResponse.getIdentifier()

		Assert.assertEquals("value", identifier)
	}
}
