package com.ringpublishing.tracking.data.aureus

/**
 * AureusTeaser
 *
 * @param teaserId: Aureus teaser identifier
 * @param offerId: Aureus offer identifier / offers batch id identifier
 * @param contentId: Content identifier
 */
data class AureusTeaser(
    val teaserId: String?,
    val offerId: String?,
    val contentId: String
)
