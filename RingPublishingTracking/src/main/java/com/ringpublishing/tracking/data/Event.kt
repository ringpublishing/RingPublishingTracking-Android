/*
 *  Created by Grzegorz Małopolski on 9/21/21, 4:33 PM
 * Copyright © 2021 Ringier Axel Springer Tech. All rights reserved.
 *
 */
package com.ringpublishing.tracking.data

import com.ringpublishing.tracking.internal.constants.Constants

data class Event(
    val analyticsSystemName: String = Constants.eventDefaultAnalyticsSystemName,
    val name: String = Constants.eventDefaultName,
    val parameters: MutableMap<String, Any> = mutableMapOf(),
)
{
	override fun toString(): String
	{
		val builder = StringBuilder()
		builder.append("Event {analyticsSystemName=$analyticsSystemName")
		builder.append(", name=$name")

		if (parameters.isNotEmpty()) builder.append(", parameters=")

		for (key in parameters.keys)
		{
			val parameterValue = mapCollectionsToString(parameters[key])
			builder.append("$key=$parameterValue ")
		}

		builder.append("}")
		return builder.toString()
	}

	private fun mapCollectionsToString(parameter: Any?): Any = when (parameter)
	{
		is Array<*> ->
		{
			parameter.contentToString()
		}
		is List<*> ->
		{
			parameter.toTypedArray().contentToString()
		}
		is Map<*, *> ->
		{
			parameter.entries.toTypedArray().contentToString()
		}
		else -> parameter.toString()
	}
}
