/*
 *  Created by Marcin Nowacki on 9/7/26, 3:00 PM
 * Copyright © 2026 Ringier Axel Springer Tech. All rights reserved.
 *
 */

package com.ringpublishing.tracking.internal.decorator

import com.ringpublishing.tracking.data.Event
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.random.Random

internal class SessionIdentifierDecorator : BaseDecorator()
{

	private var sessionIdentifier = generateSessionIdentifier()

	override fun decorate(event: Event)
	{
		event.add(EventParam.SESSION_ID, sessionIdentifier)
	}

	/**
	 * Generates a fresh session identifier. Unused for now - required by spec as a hook for a future session-break feature.
	 */
	fun startNewSession()
	{
		sessionIdentifier = generateSessionIdentifier()
	}

	private companion object
	{
		private const val RANDOM_PART_LENGTH = 10

		private fun generateSessionIdentifier(): String
		{
			val dateFormatter = SimpleDateFormat("yyyyMMddHHmmss", Locale.US)
			val randomPart = (0 until RANDOM_PART_LENGTH).joinToString("") { Random.nextInt(0, 10).toString() }
			return dateFormatter.format(Date()) + randomPart
		}
	}
}
