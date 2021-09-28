package com.ringpublishing.tracking.internal.service.result

internal class ReportEventStatusMapper
{

    fun getStatus(responseCode: Int): ReportEventStatus
    {
        return when (responseCode)
        {
            in 200..203 -> ReportEventStatus.SUCCESS
            in 204..299 -> ReportEventStatus.ERROR_BAD_REQUEST
            in 400..499 -> ReportEventStatus.ERROR_BAD_REQUEST
            in 500..599 -> ReportEventStatus.ERROR_NETWORK
            else -> ReportEventStatus.ERROR_NETWORK
        }
    }
}
