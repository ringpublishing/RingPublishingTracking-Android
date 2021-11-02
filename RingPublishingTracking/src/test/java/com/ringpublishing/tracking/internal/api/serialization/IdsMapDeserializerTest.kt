/*
 *  Created by Grzegorz Małopolski on 10/28/21, 10:30 AM
 * Copyright © 2021 Ringier Axel Springer Tech. All rights reserved.
 *
 */

package com.ringpublishing.tracking.internal.api.serialization

import com.google.gson.Gson
import com.google.gson.JsonElement
import com.ringpublishing.tracking.internal.log.Logger
import io.mockk.MockKAnnotations
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class IdsMapDeserializerTest
{

	@Before
	fun before()
	{
		MockKAnnotations.init(this, relaxUnitFun = true)
		Logger.debugLogEnabled(true)
	}

	@Test
	fun deserialize_WhenObjectHaveThreeParams_ThenThreeParamsInMap()
	{
		val jsonElement = Gson().fromJson("{\"param1\":\"value1\", \"param2\":30, \"param3\":1}", JsonElement::class.java)

		val deserializer = IdsMapDeserializer()

		val idsMap = deserializer.deserialize(jsonElement, null, null)

		val params = idsMap.parameters
		Assert.assertNotNull(params)
		Assert.assertEquals(3, params.size)
	}
}
