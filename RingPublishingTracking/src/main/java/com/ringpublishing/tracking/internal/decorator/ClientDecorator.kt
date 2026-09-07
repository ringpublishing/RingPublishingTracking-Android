/*
 *  Created by Grzegorz Małopolski on 10/6/21, 11:28 AM
 * Copyright © 2021 Ringier Axel Springer Tech. All rights reserved.
 *
 */

package com.ringpublishing.tracking.internal.decorator

import android.util.Base64
import com.google.gson.Gson
import com.ringpublishing.tracking.data.Event
import com.ringpublishing.tracking.internal.data.Client
import com.ringpublishing.tracking.internal.data.ClientPlatform
import com.ringpublishing.tracking.internal.data.ClientType
import com.ringpublishing.tracking.internal.data.ClientVariant
import com.ringpublishing.tracking.internal.log.Logger
import java.io.UnsupportedEncodingException

internal class ClientDecorator(private val gson: Gson) : BaseDecorator()
{

	private var variantExternalParameters: Map<String, String>? = null

	override fun decorate(event: Event)
	{
		val variant = variantExternalParameters?.let { ClientVariant(it) }
		val client = Client(ClientType(ClientPlatform.native_app), variant)

		val clientId = encodeClientId(client)
		clientId.let { event.add(EventParam.CLIENT_ID, it) }
	}

	/**
	 * Sets variant.external keys reported inside RDLC; rejected (unchanged) over 10 keys or 10 chars each.
	 */
	fun updateVariantExternalParameters(parameters: Map<String, String>)
	{
		if (!isValidVariantExternalParameters(parameters))
		{
			Logger.error(
				"Rejected variant.external parameters: exceeds limits (max $MAX_VARIANT_EXTERNAL_PARAMETERS_COUNT keys, " +
					"max $MAX_VARIANT_EXTERNAL_PARAMETER_LENGTH characters per key/value)"
			)
			return
		}

		variantExternalParameters = parameters
	}

	private fun isValidVariantExternalParameters(parameters: Map<String, String>): Boolean
	{
		if (parameters.size > MAX_VARIANT_EXTERNAL_PARAMETERS_COUNT) return false

		return parameters.all { (key, value) ->
			key.length <= MAX_VARIANT_EXTERNAL_PARAMETER_LENGTH && value.length <= MAX_VARIANT_EXTERNAL_PARAMETER_LENGTH
		}
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

	private companion object
	{
		const val MAX_VARIANT_EXTERNAL_PARAMETERS_COUNT = 10
		const val MAX_VARIANT_EXTERNAL_PARAMETER_LENGTH = 10
	}
}
