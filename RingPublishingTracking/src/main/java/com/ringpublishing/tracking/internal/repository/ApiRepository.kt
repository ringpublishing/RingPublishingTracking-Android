package com.ringpublishing.tracking.internal.repository

import com.ringpublishing.tracking.data.TrackingIdentifier
import com.ringpublishing.tracking.internal.api.response.IdentifyResponse
import java.util.Date

internal class ApiRepository(private val repository: DataRepository)
{

    private enum class Key(val text: String) {
        IDENTIFY("identify"),
        IDENTIFY_DATE("identifyDate")
    }

    fun saveIdentify(identifyResponse: IdentifyResponse?)
    {
        repository.saveObject(Key.IDENTIFY.text, identifyResponse)
        repository.saveObject(Key.IDENTIFY_DATE.text, Date())
    }

    fun readIdentify() = repository.readObject<IdentifyResponse?>(Key.IDENTIFY.text, IdentifyResponse::class.java)

    fun readIdentifyRequestDate() = repository.readObject<Date?>(Key.IDENTIFY_DATE.text, Date::class.java)

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

			if (identifier.isNullOrEmpty() || expirationDate == null) return null

			return TrackingIdentifier(identifier, expirationDate)
		}
	}
}
