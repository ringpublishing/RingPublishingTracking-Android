/*
 *  Created by Grzegorz Małopolski on 10/6/21, 11:28 AM
 * Copyright © 2021 Ringier Axel Springer Tech. All rights reserved.
 *
 */

package com.ringpublishing.tracking.internal.decorator

import android.util.Base64
import com.google.gson.Gson
import com.ringpublishing.tracking.data.Event
import com.ringpublishing.tracking.internal.ConfigurationManager
import com.ringpublishing.tracking.internal.log.Logger
import java.io.UnsupportedEncodingException

internal class UserSsoDataDecorator(private val configurationManager: ConfigurationManager, private val gson: Gson) : BaseDecorator()
{

	override fun decorate(event: Event)
	{
		val userId = configurationManager.getUserData().userId
		val emailMd5 = configurationManager.getUserData().emailMd5
		val ssoName = configurationManager.getUserData().ssoName ?: ""

		if (userId.isNullOrEmpty() || emailMd5.isNullOrEmpty()) return

		val encodedUserData = encodeUserData(userId, emailMd5, ssoName)
		encodedUserData?.let { event.add(EventParam.USER_SSO_DATA, it) }
	}

	private fun encodeUserData(userId: String, emailMd5: String, ssoName: String): String?
	{
		val jsonUser = gson.toJson(Sso(Logged(userId, emailMd5), ssoName))

		val data: ByteArray?

		try
		{
			data = jsonUser.toByteArray(Charsets.UTF_8)
		} catch (e: UnsupportedEncodingException)
		{
			Logger.warn("Parse user sso data UnsupportedEncodingException $e")
			return null
		}
		return Base64.encodeToString(data, Base64.NO_WRAP)
	}

	private class Logged(val id: String?, val md5: String?)

	private class Sso(val logged: Logged, val name: String)
}
