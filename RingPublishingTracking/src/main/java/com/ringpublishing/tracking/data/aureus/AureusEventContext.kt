package com.ringpublishing.tracking.data.aureus

/**
 * AureusEventContext
 *
 * @param clientUuid: Aureus client UUID. Corresponds to a purchase of Aureus service for a given website
 * @param variantUuid: Aureus variant UUID. Corresponds to the unique 'recipe' which was used to generate this particular recommendation
 * @param batchId: Query identifier executed to Aureus
 * @param recommendationId: Identifier of single recommendation. A batch contains one or more recommendations
 * @param segmentId: Segment identifier of given end user
 */
data class AureusEventContext(
    val clientUuid: String,
    val variantUuid: String,
    val batchId: String,
    val recommendationId: String,
    val segmentId: String
)
{
    // Identifier of a teaser displayed to the user
    var teaserId: String? = null
}
