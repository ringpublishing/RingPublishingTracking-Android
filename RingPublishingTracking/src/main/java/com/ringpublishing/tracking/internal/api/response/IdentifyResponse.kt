package com.ringpublishing.tracking.internal.api.response

import com.ringpublishing.tracking.internal.api.data.IdsMap
import com.ringpublishing.tracking.internal.api.data.Profile
import java.util.Calendar
import java.util.Date

internal data class IdentifyResponse(val ids: IdsMap?, val profile: Profile?, val postInterval: Long?)
{

	private fun getLifetime() = ids?.parameters?.get("eaUUID")?.asJsonObject?.get("lifetime")?.asLong ?: 0

	fun getValidDate(savedIdentifyDate: Date?): Date?
	{
		if (savedIdentifyDate == null) return null

		val calendar = Calendar.getInstance()
		calendar.timeInMillis = savedIdentifyDate.time + getLifetime()

		return calendar.time
	}

	fun getIdentifier() = ids?.parameters?.get("eaUUID")?.asJsonObject?.get("value")?.asString
}
