/*
 *  Created by Grzegorz Małopolski on 10/7/21, 3:33 PM
 * Copyright © 2021 Ringier Axel Springer Tech. All rights reserved.
 *
 */

package com.ringpublishing.tracking.internal.decorator

import android.util.Base64
import androidx.test.platform.app.InstrumentationRegistry
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonElement
import com.ringpublishing.tracking.data.Event
import com.ringpublishing.tracking.internal.ConfigurationManager
import com.ringpublishing.tracking.internal.api.response.ArtemisIdResponse
import com.ringpublishing.tracking.internal.api.response.Cfg
import com.ringpublishing.tracking.internal.api.response.Id
import com.ringpublishing.tracking.internal.api.response.User
import com.ringpublishing.tracking.internal.repository.ApiRepository
import com.ringpublishing.tracking.internal.repository.PreferencesRepository
import org.junit.Assert
import org.junit.Test

internal class UserSsoDataDecoratorTest
{

	@Test
	fun decorate_WhenUserUpdated_Then_CorrectParameterResult()
	{
        val context = InstrumentationRegistry.getInstrumentation().context

        val gson = GsonBuilder().create()

        val configurationManager = ConfigurationManager()

        val dataRepository = PreferencesRepository(context, gson)

		val apiRepository = ApiRepository(dataRepository)

		configurationManager.updateUserData("ssoValue", "userIdValue", "1")
        apiRepository.saveArtemisId(prepareArtemisResponse())

		val userSsoDataDecorator = UserIdentifierDataDecorator(
            configurationManager = configurationManager,
            apiRepository = apiRepository,
            gson = gson
        )

		val event = Event()
		userSsoDataDecorator.decorate(event)

		val result = event.parameters[EventParam.USER_SSO_DATA.text] as String?
		val decodedResult = String(Base64.decode(result, Base64.NO_WRAP))

		Assert.assertTrue(decodedResult.contains("logged"))
		Assert.assertTrue(decodedResult.contains("userIdValue"))
		Assert.assertTrue(decodedResult.contains("md5"))
		Assert.assertTrue(decodedResult.contains("name"))
	}

    private fun prepareArtemisResponse() = ArtemisIdResponse(
        cfg = Cfg(
            version = 0.0,
            ttl = 3600L,
            cmds = emptyList()
        ),
        user = User(
            id = Id(
                real = "real",
                model = "model",
                models = Gson().fromJson("{\n\"ats_ri\": \"1234\"\n}", JsonElement::class.java)
            )
        )
    )
}
