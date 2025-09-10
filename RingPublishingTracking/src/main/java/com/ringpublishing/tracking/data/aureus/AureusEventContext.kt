package com.ringpublishing.tracking.data.aureus

/**
 * AureusEventContext
 *
 * @param variantUuid: Aureus variant UUID. Corresponds to the unique 'recipe' which was used to generate this particular recommendation
 * @param batchId: Query identifier executed to Aureus
 * @param recommendationId: Identifier of single recommendation. A batch contains one or more recommendations
 * @param segmentId: Segment identifier of given end user
 * @param impressionEventType: Type of event which is expected to be reported by Aureus
 */
data class AureusEventContext(
    val variantUuid: String,
    val batchId: String,
    val recommendationId: String,
    val segmentId: String,
    val impressionEventType: String
)
{
    // Identifier of a teaser displayed to the user
    var teaserId: String? = null
}
