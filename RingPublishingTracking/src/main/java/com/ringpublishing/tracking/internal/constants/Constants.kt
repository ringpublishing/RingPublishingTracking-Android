package com.ringpublishing.tracking.internal.constants

import com.ringpublishing.tracking.BuildConfig
import java.net.URL

internal object Constants
{

	val apiUrl = URL(BuildConfig.API_URL)
    const val apiVersion = "v3"

    const val maxEventSize = 16384L // 16 KB
    const val maxRequestBodySize = 1048576L // 1 MB
    const val maxRequestBodySizeBuffer = 5120L // 5KB

	const val eventDefaultName = "Event"
    const val eventDefaultAnalyticsSystemName = "_GENERIC"

    const val defaultRootPathSuffix = "_app_android"

	const val applicationDefaultAdvertisementArea = "undefined"
	val applicationDefaultStructurePath = listOf("undefined")

	const val consentStringPreferenceName = "IABTCF_TCString"
	const val mobileDepth = 24
}
