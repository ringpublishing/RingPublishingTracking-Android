package com.ringpublishing.tracking.internal.decorator

import android.util.Base64
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.ringpublishing.tracking.data.ContentMetadata
import com.ringpublishing.tracking.internal.log.Logger
import java.util.concurrent.ConcurrentHashMap

// Use matches(), not find(): '$' also matches before a trailing line terminator such as U+0085.
private val uuidRegex = Regex("^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$")

// Explicit set: Kotlin trim() and iOS whitespacesAndNewlines disagree on characters such as U+0085.
private val asciiWhitespace = setOf(' ', '\t', '\n', '\r', '\u000B', '\u000C')

// Raw content ids already warned about, so each one is logged once while the SDK (a process-wide object) lives.
private val contentIdsWarnedAsNotUuid: MutableSet<String> = ConcurrentHashMap.newKeySet()

internal fun createMarkedAsPaidParam(gson: Gson, contentMetadata: ContentMetadata?): String?
{
    contentMetadata ?: return null

    val markedAsPaidData = MarkedAsPaidData(
        contentObject = createContentObject(contentMetadata.contentId),
        publication = Publication(contentMetadata.paidContent),
        source = Source(contentMetadata.contentSpaceUuid, contentMetadata.sourceSystemName)
    )

    return encodePaidContentData(
        markedAsPaidDataJson = gson.toJson(markedAsPaidData)
    )
}

/**
 * 'object.id' is the content id with ASCII whitespace trimmed, lowercased, and reported only when it is a UUID. The iOS
 * SDK applies the same rule, so every event and both platforms report one value per content, whatever the 'PU' is.
 */
private fun createContentObject(contentId: String): ContentObject?
{
    val objectId = contentId.trim { it in asciiWhitespace }.lowercase()

    if (!uuidRegex.matches(objectId))
    {
        if (contentIdsWarnedAsNotUuid.add(contentId))
        {
            Logger.warn("RDLCN: content id '$contentId' is not a UUID, 'object' is omitted")
        }
        return null
    }

    return ContentObject(objectId)
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
