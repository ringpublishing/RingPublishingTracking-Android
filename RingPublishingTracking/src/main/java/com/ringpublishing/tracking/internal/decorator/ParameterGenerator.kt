/*
 *  Created by Grzegorz Małopolski on 10/6/21, 1:26 PM
 * Copyright © 2021 Ringier Axel Springer Tech. All rights reserved.
 *
 */

package com.ringpublishing.tracking.internal.decorator

import com.ringpublishing.tracking.internal.constants.Constants
import java.util.Calendar
import java.util.Random

internal class ParameterGenerator
{

	fun generatePrimaryId(): String
	{
		with(Calendar.getInstance())
		{
			var iv = get(Calendar.YEAR) % 100L + 2000
			iv = iv * 100 + get(Calendar.MONTH) + 1
			iv = iv * 100 + get(Calendar.DATE)
			iv = iv * 100 + get(Calendar.HOUR)
			iv = iv * 100 + get(Calendar.MINUTE)
			iv = iv * 100 + get(Calendar.SECOND)
			iv = iv * 1000 + get(Calendar.MILLISECOND)
			iv += ("" + ((1 + Random().nextInt()) * 10000000)).substring(0, 8).toLong()
			return iv.toString()
		}
	}

	fun buildRootUrl(applicationRootPath: String, applicationStructurePath: List<String>): String
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
