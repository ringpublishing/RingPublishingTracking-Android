package com.ringpublishing.tracking.internal.service.queue

import com.ringpublishing.tracking.internal.constants.Constants
import com.ringpublishing.tracking.internal.repository.ApiRepository
import com.ringpublishing.tracking.internal.repository.UserRepository
import com.google.gson.Gson

internal class EventSizeCalculator(
    private val gson: Gson,
    private val apiRepository: ApiRepository,
    private val userRepository: UserRepository
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

    fun isLowerThanMaxRequestSize(currentSize: Long, newElementSize: Long): Boolean
    {
        val totalSize = totalOtherBodyObjectsSize + currentSize + newElementSize
        return totalSize < Constants.maxRequestBodySize
    }

    fun getSizeInBytes(event: Any?): Long
    {
        if (event == null) return 0
        return (gson.toJson(event).length * Character.SIZE / Byte.SIZE_BITS).toLong()
    }
}
