package com.ringpublishing.tracking.internal.api.data

internal data class Ids(val eaUUID: String?)
{
    constructor(idsMap: IdsMap) : this(idsMap.parameters["eaUUID"]?.asJsonObject?.get("value")?.asString)
}
