/*
 *  Created by Grzegorz Małopolski on 10/14/21, 11:47 AM
 * Copyright © 2021 Ringier Axel Springer Tech. All rights reserved.
 *
 */

package com.ringpublishing.tracking.internal.util

import com.ringpublishing.tracking.internal.ConfigurationManager
import com.ringpublishing.tracking.internal.constants.Constants

internal class PathBuilder(private val configurationManager: ConfigurationManager)
{

	private val applicationRootPath get() = configurationManager.ringPublishingTrackingConfiguration.applicationRootPath
	private val structurePath get() = configurationManager.getStructurePath()
	private val defaultStructurePath get() = configurationManager.ringPublishingTrackingConfiguration.applicationDefaultStructurePath

	fun buildCurrentContentUrl(): String
	{
		val url = configurationManager.currentPublicationUrl?.toString()
		return if (!url.isNullOrEmpty()) url.lowercase() else buildUrlFromPath(applicationRootPath, structurePath).lowercase()
	}

	fun isDefaultContentUrl(path: String?): Boolean
	{
		val defaultPath = buildUrlFromPath(applicationRootPath, defaultStructurePath).lowercase()
		return defaultPath == path?.lowercase()
	}

	private fun buildUrlFromPath(applicationRootPath: String, applicationStructurePath: List<String>): String
	{
		var path = applicationRootPath.lowercase()

		if (!path.startsWith("http"))
		{
			path = "https://$path"
		}

		if (path.endsWith("/"))
		{
			path = path.substringBeforeLast("/")
		}

		return applicationStructurePath.joinToString("/", "${path}${Constants.defaultRootPathSuffix}/")
	}
}
