package com.ringpublishing.tracking.internal.repository

import com.ringpublishing.tracking.data.TrackingIdentifier
import com.ringpublishing.tracking.internal.api.response.IdentifyResponse
import com.ringpublishing.tracking.internal.util.isIdentifyExpire
import java.util.Date

internal class ApiRepository(private val repository: DataRepository)
{

	private enum class Key(val text: String)
	{

		IDENTIFY("identify"),
		IDENTIFY_DATE("identifyDateLong")
	}

	fun saveIdentify(identifyResponse: IdentifyResponse?)
	{
		repository.saveObject(Key.IDENTIFY.text, identifyResponse)
		repository.saveLong(Key.IDENTIFY_DATE.text, Date().time)
	}

	fun readIdentify(): IdentifyResponse?
	{

		val identifyResponse = repository.readObject<IdentifyResponse?>(Key.IDENTIFY.text, IdentifyResponse::class.java)
		val expirationDate = identifyResponse?.getValidDate(readIdentifyRequestDate())

		return if (!expirationDate.isIdentifyExpire())
		{
			identifyResponse
		} else {
			removeIdentify()
			null
		}
	}

	fun readIdentifyRequestDate(): Date?
	{
		val dateValue = repository.readLong(Key.IDENTIFY_DATE.text)

		return if (dateValue != null && dateValue > 0L)
		{
			Date(dateValue)
		} else {
			null
		}
	}

	fun removeIdentify()
	{
		repository.remove(Key.IDENTIFY.text)
		repository.remove(Key.IDENTIFY_DATE.text)
	}

	fun readTrackingIdentifier(): TrackingIdentifier?
	{
		val identifyResponse = readIdentify()

		return identifyResponse?.let { identify ->
			val expirationDate = identify.getValidDate(readIdentifyRequestDate())
			val identifier = identify.getIdentifier()

			if (identifier.isNullOrEmpty() || expirationDate.isIdentifyExpire()) return null

			return TrackingIdentifier(identifier, expirationDate!!)
		}
	}
}
