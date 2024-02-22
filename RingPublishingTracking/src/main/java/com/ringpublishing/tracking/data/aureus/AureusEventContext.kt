package com.ringpublishing.tracking.data.aureus

data class AureusEventContext(

    // Aureus client UUID
    // Corresponds to a purchase of Aureus service for a given website
    val clientUuid: String?,

    // Aureus variant UUID
    // Corresponds to the unique 'recipe' which was used to generate this particular recommendation
    val variantUuid: String?,

    // Query identifier executed to Aureus
    val batchId: String?,

    // Identifier of single recommendation
    // A batch contains one or more recommendations
    val recommendationId: String?,

    // Segment identifier of given end user
    val segmentId: String?
)
{
    // Identifier of a teaser displayed to the user
    var teaserId: String? = null
}

