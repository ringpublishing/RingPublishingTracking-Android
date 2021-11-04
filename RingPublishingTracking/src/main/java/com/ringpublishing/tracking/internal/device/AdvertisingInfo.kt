package com.ringpublishing.tracking.internal.device

import android.content.Context
import com.google.android.gms.ads.identifier.AdvertisingIdClient
import com.ringpublishing.tracking.internal.log.Logger

internal class AdvertisingInfo(private val context: Context)
{

	private var advertisingId: String? = null

	private var readCalled = false

	private fun readAdvertisementIdFromGoogleClient(): String?
	{
		Logger.debug("AdvertisingInfo: readAdvertisementIdFromGoogleClient Current thread: ${Thread.currentThread().name}")
		try
		{
			return AdvertisingIdClient.getAdvertisingIdInfo(context).id
		} catch (e: Exception)
		{
			Logger.info("AdvertisingInfo: Cannot get advertisingId ${e.localizedMessage}")
		}
		return null
	}

	fun readAdvertisingId(): String?
	{
		if (!readCalled)
		{
			advertisingId = readAdvertisementIdFromGoogleClient()
			Logger.debug("AdvertisingInfo: readAdvertisementIdFromGoogleClient() with result: $advertisingId")
			readCalled = true
		}
		Logger.debug("AdvertisingInfo: readAdvertisingId: $advertisingId")
		return advertisingId
	}
}
