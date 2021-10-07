/*
 *  Created by Grzegorz Małopolski on 10/7/21, 3:33 PM
 * Copyright © 2021 Ringier Axel Springer Tech. All rights reserved.
 *
 */

package com.ringpublishing.tracking.internal.decorator

import android.util.Base64
import com.google.gson.GsonBuilder
import com.ringpublishing.tracking.data.Event
import com.ringpublishing.tracking.internal.delegate.ConfigurationManager
import org.junit.Assert
import org.junit.Test

class UserSsoDataDecoratorTest
{

	@Test
	fun decorate_WhenUserUpdated_Then_CorrectParameterResult()
	{
		val configurationManager = ConfigurationManager()

		configurationManager.updateUserData("ssoValue", "userIdValue")

		val gson = GsonBuilder().create()
		val userSsoDataDecorator = UserSsoDataDecorator(configurationManager, gson)

		val event = Event("", "")
		userSsoDataDecorator.decorate(event)

		val result = event.parameters[EventParam.USER_SSO_DATA.paramName] as String?

		val decodedResult = String(Base64.decode(result, Base64.DEFAULT))

		Assert.assertEquals("{\"name\":\"ssoValue\",\"sso\":{\"logged\":{\"id\":\"userIdValue\"}}}", decodedResult)
	}
}
