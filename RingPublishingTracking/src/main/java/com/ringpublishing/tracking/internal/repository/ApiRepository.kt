package com.ringpublishing.tracking.internal.repository

import com.ringpublishing.tracking.data.ArtemisIdentifier
import com.ringpublishing.tracking.data.TrackingIdentifier
import com.ringpublishing.tracking.data.UserIdentifier
import com.ringpublishing.tracking.internal.api.response.ArtemisIdResponse
import com.ringpublishing.tracking.internal.api.response.IdentifyResponse
import com.ringpublishing.tracking.internal.util.isIdentifyExpire
import java.util.Date

internal class ApiRepository(private val repository: DataRepository)
{

    private enum class Key(val text: String)
    {
        IDENTIFY("identify"),
        IDENTIFY_DATE("identifyDateLong"),
        ARTEMIS("artemis"),
        ARTEMIS_DATE("artemisDateLong"),
    }

    fun saveIdentify(identifyResponse: IdentifyResponse?)
    {
        repository.saveObject(Key.IDENTIFY.text, identifyResponse)
        repository.saveLong(Key.IDENTIFY_DATE.text, Date().time)
    }

    fun saveArtemisId(artemisIdResponse: ArtemisIdResponse?)
    {
        repository.saveObject(Key.ARTEMIS.text, artemisIdResponse)
        repository.saveLong(Key.ARTEMIS_DATE.text, Date().time)
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

    fun readArtemisId(): ArtemisIdResponse?
    {

        val artemisIdResponse = repository.readObject<ArtemisIdResponse?>(Key.ARTEMIS.text, ArtemisIdResponse::class.java)
        val expirationDate = artemisIdResponse?.getValidDate(readArtemisIdDate())

        return if (!expirationDate.isIdentifyExpire())
        {
            artemisIdResponse
        } else {
            removeArtemisId()
            null
        }
    }

    fun readIdentifyRequestDate(): Date? = readDateForKey(Key.IDENTIFY_DATE)

    fun readArtemisIdDate(): Date? = readDateForKey(Key.ARTEMIS_DATE)

    private fun readDateForKey(key: Key): Date?
    {
        val dateValue = repository.readLong(key.text)

        return if (dateValue != null && dateValue > 0L) Date(dateValue)
        else null
    }

    fun removeIdentify()
    {
        repository.remove(Key.IDENTIFY.text)
        repository.remove(Key.IDENTIFY_DATE.text)
    }

    fun removeArtemisId()
    {
        repository.remove(Key.ARTEMIS.text)
        repository.remove(Key.ARTEMIS_DATE.text)
    }

    private fun readUserIdentifier(): UserIdentifier? = readIdentify()?.let { identify ->
        val expirationDate = identify.getValidDate(readIdentifyRequestDate())
        val identifier = identify.getIdentifier()

        if (identifier.isNullOrEmpty() || expirationDate.isIdentifyExpire()) null
        else UserIdentifier(identifier, expirationDate!!)
    }

    private fun readArtemisIdentifier(): ArtemisIdentifier? = readArtemisId()?.let { identify ->
        val expirationDate = identify.getValidDate(readIdentifyRequestDate())

        if (expirationDate.isIdentifyExpire()) null
        else ArtemisIdentifier(identify.toArtemisId(), expirationDate!!)
    }

    fun readTrackingIdentifier(): TrackingIdentifier?
    {
        val userIdentifier = readUserIdentifier()
        val artemisIdentifier = readArtemisIdentifier()

        return if (userIdentifier == null || artemisIdentifier == null) null
        else TrackingIdentifier(userIdentifier, artemisIdentifier)
    }
}
