/*
 *  Created by Grzegorz Małopolski on 4/12/22, 10:23 AM
 * Copyright © 2021 Ringier Axel Springer Tech. All rights reserved.
 *
 */

package com.ringpublishing.tracking.internal.decorator

import android.util.Base64
import com.google.gson.GsonBuilder
import com.ringpublishing.tracking.data.Event
import org.junit.Assert
import org.junit.Test

internal class ClientDecoratorTest
{

	@Test
	fun decorate_When_Correct_Client_Then_Decoded_Result()
	{
		val gson = GsonBuilder().create()
		val clientDecorator = ClientDecorator(gson)

		val event = Event()
		clientDecorator.decorate(event)

		val result = event.parameters[EventParam.CLIENT_ID.text] as String?

		val decodedResult = String(Base64.decode(result, Base64.NO_WRAP))

		Assert.assertTrue(decodedResult.contains("{\"client\":{\"type\":\"native_app\"}}"))
	}

	@Test
	fun decorate_WhenUpdatedWithValidVariantExternalParameters_ThenClientIdContainsVariant()
	{
		val gson = GsonBuilder().create()
		val clientDecorator = ClientDecorator(gson)

		clientDecorator.updateVariantExternalParameters(mapOf("api_ver" to "1.0.1b"))

		val event = Event()
		clientDecorator.decorate(event)

		val result = event.parameters[EventParam.CLIENT_ID.text] as String?
		val decodedResult = String(Base64.decode(result, Base64.NO_WRAP))

		Assert.assertEquals(
			"{\"client\":{\"type\":\"native_app\"},\"variant\":{\"external\":{\"api_ver\":\"1.0.1b\"}}}",
			decodedResult
		)
	}

	@Test
	fun decorate_WhenUpdatedWithTooManyVariantExternalKeys_ThenParametersAreRejected()
	{
		val gson = GsonBuilder().create()
		val clientDecorator = ClientDecorator(gson)
		val tooManyKeys = (0 until 11).associate { "k$it" to "v" }

		clientDecorator.updateVariantExternalParameters(tooManyKeys)

		val event = Event()
		clientDecorator.decorate(event)

		val result = event.parameters[EventParam.CLIENT_ID.text] as String?
		val decodedResult = String(Base64.decode(result, Base64.NO_WRAP))

		Assert.assertEquals("{\"client\":{\"type\":\"native_app\"}}", decodedResult)
	}

	@Test
	fun decorate_WhenUpdatedWithTooLongVariantExternalKeyOrValue_ThenParametersAreRejected()
	{
		val gson = GsonBuilder().create()
		val clientDecorator = ClientDecorator(gson)

		clientDecorator.updateVariantExternalParameters(mapOf("a_key_too_long_here" to "v"))

		val eventWithLongKey = Event()
		clientDecorator.decorate(eventWithLongKey)
		var decodedResult = String(Base64.decode(eventWithLongKey.parameters[EventParam.CLIENT_ID.text] as String?, Base64.NO_WRAP))
		Assert.assertEquals("{\"client\":{\"type\":\"native_app\"}}", decodedResult)

		clientDecorator.updateVariantExternalParameters(mapOf("k" to "a_value_too_long_here"))

		val eventWithLongValue = Event()
		clientDecorator.decorate(eventWithLongValue)
		decodedResult = String(Base64.decode(eventWithLongValue.parameters[EventParam.CLIENT_ID.text] as String?, Base64.NO_WRAP))
		Assert.assertEquals("{\"client\":{\"type\":\"native_app\"}}", decodedResult)
	}
}
