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
	private val defaultStructurePath get() = configurationManager.ringPublishingTrackingConfiguration.applicationDefaultStructurePath

	fun buildCurrentContentUrl(): String
	{
		with(configurationManager)
		{
			if (currentPublicationUrl != null)
			{
				return (currentPublicationUrl.toString() + contentPageViewSource.utmMedium()).lowercase()
			}

			return buildUrlFromPath(applicationRootPath, getStructurePath(), contentPageViewSource.utmMedium()).lowercase()
		}
	}

	fun isDefaultContentUrl(path: String?): Boolean
	{
		val defaultPath = buildUrlFromPath(applicationRootPath, defaultStructurePath).lowercase()
		return defaultPath == path?.lowercase()
	}

	private fun buildUrlFromPath(applicationRootPath: String, applicationStructurePath: List<String>, query: String? = null): String
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
		path = applicationStructurePath.joinToString("/", "${path}${Constants.defaultRootPathSuffix}/")
		query?.let { path = "$path$query" }
		return path
	}
}
