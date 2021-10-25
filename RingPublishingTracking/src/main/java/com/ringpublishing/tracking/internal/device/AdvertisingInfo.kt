package com.ringpublishing.tracking.internal.device

import android.content.Context
import com.google.android.gms.ads.identifier.AdvertisingIdClient
import com.ringpublishing.tracking.internal.log.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

internal class AdvertisingInfo(private val context: Context)
{

    var advertisingId: String? = null

    /**
     * Call once on application start
     */
    fun obtainAdvertisingId()
    {
	    CoroutineScope(SupervisorJob() + Dispatchers.IO).launch(Dispatchers.IO) {
		    readAdvertisementInfo()
	    }
    }

	private suspend fun readAdvertisementInfo()
	{
		try {
			advertisingId = AdvertisingIdClient.getAdvertisingIdInfo(context).id
		} catch (e: Exception)
		{
			Logger.info("Cannot get advertisingId ${e.localizedMessage}")
		}
	}
}
