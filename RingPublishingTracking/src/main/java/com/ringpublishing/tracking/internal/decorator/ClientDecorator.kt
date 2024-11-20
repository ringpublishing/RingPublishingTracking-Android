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
import com.ringpublishing.tracking.internal.data.Client
import com.ringpublishing.tracking.internal.data.ClientPlatform
import com.ringpublishing.tracking.internal.data.ClientType
import com.ringpublishing.tracking.internal.log.Logger
import java.io.UnsupportedEncodingException

internal class ClientDecorator(private val gson: Gson, configurationManager: ConfigurationManager) : BaseDecorator()
{
	private val client = Client(ClientType(ClientPlatform.native_app), configurationManager.environment)

	override fun decorate(event: Event)
	{
		val clientId = encodeClientId(client)
		clientId.let { event.add(EventParam.CLIENT_ID, it) }
	}

	private fun encodeClientId(client: Client): String?
	{
		val jsonClient = gson.toJson(client)

		val data: ByteArray?

		try
		{
			data = jsonClient.toByteArray(Charsets.UTF_8)
		} catch (e: UnsupportedEncodingException)
		{
			Logger.warn("Parse jsonClient UnsupportedEncodingException $e")
			return null
		}
		return Base64.encodeToString(data, Base64.NO_WRAP)
	}
}
