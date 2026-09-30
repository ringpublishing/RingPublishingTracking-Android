package com.ringpublishing.tracking.internal.decorator

import android.util.Base64
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.ringpublishing.tracking.data.ContentMetadata
import com.ringpublishing.tracking.internal.log.Logger

/**
 * [objectId] is reported as 'object.id'. Pass the content identifier exactly as the same event already reports it
 * ('PU', or 'source_publication_uuid' for paid events), so the values never differ. A blank value omits 'object'.
 */
internal fun createMarkedAsPaidParam(gson: Gson, contentMetadata: ContentMetadata?, objectId: String?): String?
{
    contentMetadata ?: return null

    val markedAsPaidData = MarkedAsPaidData(
        contentObject = objectId?.takeIf { it.isNotBlank() }?.let { ContentObject(it) },
        publication = Publication(contentMetadata.paidContent),
        source = Source(contentMetadata.contentSpaceUuid, contentMetadata.sourceSystemName)
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

private class MarkedAsPaidData(
    @SerializedName("object") val contentObject: ContentObject?,
    val publication: Publication,
    val source: Source
)

private class ContentObject(val id: String)

private class Publication(val premium: Boolean)

private class Source(val id: String, val system: String)
