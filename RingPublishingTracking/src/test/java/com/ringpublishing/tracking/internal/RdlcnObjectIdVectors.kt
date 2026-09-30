package com.ringpublishing.tracking.internal

/**
 * Content ids and the RDLCN 'object.id' each of them must produce, null when 'object' is omitted.
 * The iOS SDK tests use the same vectors, so both platforms report the same value for the same content.
 */
internal val rdlcnObjectIdVectors: List<Pair<String, String?>> = listOf(
    "E0BE23E3-A100-4D4F-A347-0635DE46BFC4" to "e0be23e3-a100-4d4f-a347-0635de46bfc4",
    "  e0be23e3-a100-4d4f-a347-0635de46bfc4  " to "e0be23e3-a100-4d4f-a347-0635de46bfc4",
    "e0be23e3-a100-4d4f-a347-0635de46bfc4" to "e0be23e3-a100-4d4f-a347-0635de46bfc4",
    "12345" to null,
    "my-unique-content-id-1234" to null,
    "e0be23e3a1004d4fa3470635de46bfc4" to null,
    "" to null,
    "   " to null,
    "\u0085e0be23e3-a100-4d4f-a347-0635de46bfc4\u0085" to null,
    "e0be23e3-a100-4d4f-a347-0635de46bfc4\u0085" to null
)
