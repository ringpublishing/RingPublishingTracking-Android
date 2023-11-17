/*
 *  Created by Grzegorz Małopolski on 10/6/21, 11:28 AM
 * Copyright © 2021 Ringier Axel Springer Tech. All rights reserved.
 *
 */

package com.ringpublishing.tracking.internal.decorator

import android.util.Base64
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.ringpublishing.tracking.data.Event
import com.ringpublishing.tracking.internal.ConfigurationManager
import com.ringpublishing.tracking.internal.log.Logger
import com.ringpublishing.tracking.internal.repository.ApiRepository

internal class UserIdentifierDataDecorator(
    private val configurationManager: ConfigurationManager,
    private val apiRepository: ApiRepository,
    private val gson: Gson
) : BaseDecorator()
{

	override fun decorate(event: Event)
	{
        val userId = configurationManager.getUserData().userId
        val sso = createSso(userId)
        val artemisId = createArtemisId()

        if (artemisId != null)
        {
            encodeUserData(sso, artemisId)?.let {
                event.add(EventParam.USER_SSO_DATA, it)
            }
        }

        if (!userId.isNullOrEmpty())
        {
            event.add(EventParam.USER_SSO_IDENTIFIER, userId)
        }
	}

    private fun createSso(userId: String?): Sso?
    {
        val emailMd5 = configurationManager.getUserData().emailMd5
        val ssoName = configurationManager.getUserData().ssoName ?: ""

        return if (userId.isNullOrEmpty() || emailMd5.isNullOrEmpty()) null
        else Sso(Logged(userId, emailMd5), ssoName)
    }

    private fun createArtemisId(): ArtemisId? = apiRepository.readArtemisId()?.let { response ->
        with(response.user?.id) {
            ArtemisId(
                artemis = this?.real,
                external = External(
                    model = this?.model,
                    models = this?.models,
                )
            )
        }
    }

    private fun encodeUserData(sso: Sso?, artemisId: ArtemisId): String?
    {
        val jsonUser = gson.toJson(UserIdentifier(artemisId, sso))
        return runCatching {
            Base64.encodeToString(
                jsonUser.toByteArray(Charsets.UTF_8),
                Base64.NO_WRAP
            )
        }.getOrElse {
            Logger.warn("Parse user sso data UnsupportedEncodingException ${it.localizedMessage}")
            null
        }
    }

    private class UserIdentifier(val id: ArtemisId?, val sso: Sso?)

    private class ArtemisId(val artemis: String?, val external: External?)

    private class External(val model: String?, val models: JsonElement?)

    private class Logged(val id: String?, val md5: String?)

	private class Sso(val logged: Logged, val name: String)
}
