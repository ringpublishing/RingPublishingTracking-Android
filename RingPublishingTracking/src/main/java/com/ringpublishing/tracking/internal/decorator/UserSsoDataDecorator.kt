/*
 *  Created by Grzegorz Małopolski on 10/6/21, 11:28 AM
 * Copyright © 2021 Ringier Axel Springer Tech. All rights reserved.
 *
 */

package com.ringpublishing.tracking.internal.decorator

import android.util.Base64
import com.google.gson.Gson
import com.ringpublishing.tracking.data.Event
import com.ringpublishing.tracking.internal.delegate.ConfigurationManager
import com.ringpublishing.tracking.internal.log.Logger
import java.io.UnsupportedEncodingException

internal class UserSsoDataDecorator(private val configurationDelegate: ConfigurationManager, private val gson: Gson) : BaseDecorator()
{
	private val userId get() = configurationDelegate.getUserData().userId
	private val ssoName get() = configurationDelegate.getUserData().ssoName ?: ""

	override fun decorate(event: Event)
	{
		if (userId.isNullOrEmpty()) return

		val encodedUserData = encodeUserSsoData(userId, ssoName)
		encodedUserData?.let { event.add(EventParam.USER_SSO_DATA, it) }
	}

	private fun encodeUserSsoData(userId: String?, ssoName: String): String?
	{
		val jsonUser = gson.toJson(User(Sso(Logged(userId)), ssoName))

		val data: ByteArray?

		try
		{
			data = jsonUser.toByteArray(Charsets.UTF_8)
		} catch (e: UnsupportedEncodingException)
		{
			Logger.warn("Parse user sso data UnsupportedEncodingException $e")
			return null
		}
		return Base64.encodeToString(data, Base64.DEFAULT)
	}

	private class Logged(val id: String?)

	private class Sso(val logged: Logged)

	private class User(val sso: Sso, val name: String)
}
