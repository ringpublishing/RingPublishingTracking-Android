package com.ringpublishing.tracking.internal.device

import android.content.Context
import androidx.ads.identifier.AdvertisingIdClient
import androidx.ads.identifier.AdvertisingIdInfo
import com.ringpublishing.tracking.internal.log.Logger
import com.google.common.util.concurrent.FutureCallback
import com.google.common.util.concurrent.Futures
import java.util.concurrent.Executors

internal class AdvertisingInfo(private val context: Context)
{

    var advertisingId: String? = null

    /**
     * Call once on application start
     */
    fun obtainAdvertisingId()
    {
        if (!AdvertisingIdClient.isAdvertisingIdProviderAvailable(context)) return

        val advertisingIdInfoListenableFuture = AdvertisingIdClient.getAdvertisingIdInfo(context)

        Futures.addCallback(
            advertisingIdInfoListenableFuture,
            object : FutureCallback<AdvertisingIdInfo>
            {
                override fun onSuccess(adInfo: AdvertisingIdInfo?)
                {
                    advertisingId = adInfo?.id
                }

                override fun onFailure(throwable: Throwable)
                {
                    Logger.warn("Failed to connect to Advertising ID provider.${throwable.localizedMessage}")
                }
            },
            Executors.newSingleThreadExecutor()
        )
    }
}
