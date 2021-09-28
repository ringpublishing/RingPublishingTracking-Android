package com.ringpublishing.tracking.internal.service.result

internal data class ReportEventResult(val status: ReportEventStatus, val postInterval: Long? = null)
{
    fun isSuccess() = status == ReportEventStatus.SUCCESS
}
