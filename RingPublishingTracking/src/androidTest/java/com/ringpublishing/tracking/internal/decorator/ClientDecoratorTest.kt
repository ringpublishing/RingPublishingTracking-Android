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
	fun decorate_When_Correct_Client_Then_Decoded_Result_Fail()
	{
		val gson = GsonBuilder().create()
		val clientDecorator = ClientDecorator(gson)

		val event = Event()
		clientDecorator.decorate(event)

		val result = event.parameters[EventParam.CLIENT_ID.text] as String?

		val decodedResult = String(Base64.decode(result, Base64.NO_WRAP))

		Assert.assertFalse(decodedResult.contains("{\"fail\":{\"type\":\"native_app\"}}"))
	}
}
