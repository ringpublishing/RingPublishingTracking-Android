package com.ringpublishing.tracking.internal.service.queue

import com.google.gson.Gson
import com.google.gson.JsonParseException
import com.ringpublishing.tracking.internal.constants.Constants
import com.ringpublishing.tracking.internal.log.Logger
import com.ringpublishing.tracking.internal.repository.ApiRepository
import com.ringpublishing.tracking.internal.repository.UserRepository

internal class EventSizeCalculator(
    private val gson: Gson,
    private val apiRepository: ApiRepository,
    private val userRepository: UserRepository,
)
{
    private var maxIdentifySize = Constants.maxRequestBodySizeBuffer
    private var maxUserSize = Constants.maxRequestBodySizeBuffer
    private var totalOtherBodyObjectsSize = 0L

	fun calculateBodyElementsSize()
	{
		val identifySize = getSizeInBytes(apiRepository.readIdentify())
		if (maxIdentifySize < identifySize)
		{
			maxIdentifySize = identifySize
		}
		val userSize = getSizeInBytes(userRepository.buildUser())
		if (maxUserSize < userSize)
		{
			maxUserSize = userSize
		}
		totalOtherBodyObjectsSize = Constants.maxRequestBodySizeBuffer + maxIdentifySize + maxUserSize
	}

    fun available(eventsToSendSize: Long) = Constants.maxRequestBodySize - totalOtherBodyObjectsSize - eventsToSendSize

    fun isBiggerThanMaxRequestSize(currentSize: Long, newElementSize: Long): Boolean
    {
        val totalSize = totalOtherBodyObjectsSize + currentSize + newElementSize
        return totalSize >= Constants.maxRequestBodySize
    }

	fun getSizeInBytes(anyObject: Any?): Long
	{
		if (anyObject == null) return 0
		val json: String?

		try
		{
			json = gson.toJson(anyObject)
		} catch (e: JsonParseException)
		{
			Logger.warn("Event will be ignored. Cannot parse to Json event: $anyObject")
			return Long.MAX_VALUE
		}

		return (json.length * Character.SIZE / Byte.SIZE_BITS).toLong()
	}
}
