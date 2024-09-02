package com.ringpublishing.tracking.internal.decorator

import android.util.Base64
import com.google.gson.Gson
import com.ringpublishing.tracking.data.ContentMetadata
import com.ringpublishing.tracking.internal.log.Logger

internal fun createMarkedAsPaidParam(gson: Gson, contentMetadata: ContentMetadata?): String?
{
    contentMetadata ?: return null

    val markedAsPaidData = MarkedAsPaidData(
        publication = Publication(contentMetadata.paidContent),
        source = Source(contentMetadata.contentId, contentMetadata.sourceSystemName)
    )

    return encodePaidContentData(
        markedAsPaidDataJson = gson.toJson(markedAsPaidData)
    )
}

private fun encodePaidContentData(markedAsPaidDataJson: String): String?
{
    return runCatching {
        Base64.encodeToString(
            markedAsPaidDataJson.toByteArray(Charsets.UTF_8),
            Base64.NO_WRAP
        )
    }.onFailure {
        Logger.warn("Parse paidContentDataJson UnsupportedEncodingException $it")
    }.getOrNull()
}

private class MarkedAsPaidData(val publication: Publication, val source: Source)

private class Publication(val premium: Boolean)

private class Source(val id: String, val system: String)
