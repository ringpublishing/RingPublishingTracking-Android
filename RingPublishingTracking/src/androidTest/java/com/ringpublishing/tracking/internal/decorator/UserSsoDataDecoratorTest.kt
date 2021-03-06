/*
 *  Created by Grzegorz Małopolski on 10/7/21, 3:33 PM
 * Copyright © 2021 Ringier Axel Springer Tech. All rights reserved.
 *
 */

package com.ringpublishing.tracking.internal.decorator

import android.util.Base64
import com.google.gson.GsonBuilder
import com.ringpublishing.tracking.data.Event
import com.ringpublishing.tracking.internal.ConfigurationManager
import org.junit.Assert
import org.junit.Test

internal class UserSsoDataDecoratorTest
{

	@Test
	fun decorate_WhenUserUpdated_Then_CorrectParameterResult()
	{
		val configurationManager = ConfigurationManager()

		configurationManager.updateUserData("ssoValue", "userIdValue", "1")

		val gson = GsonBuilder().create()
		val userSsoDataDecorator = UserSsoDataDecorator(configurationManager, gson)

		val event = Event()
		userSsoDataDecorator.decorate(event)

		val result = event.parameters[EventParam.USER_SSO_DATA.text] as String?

		val decodedResult = String(Base64.decode(result, Base64.NO_WRAP))

		Assert.assertTrue(decodedResult.contains("logged"))
		Assert.assertTrue(decodedResult.contains("userIdValue"))
		Assert.assertTrue(decodedResult.contains("md5"))
		Assert.assertTrue(decodedResult.contains("name"))
	}
}
