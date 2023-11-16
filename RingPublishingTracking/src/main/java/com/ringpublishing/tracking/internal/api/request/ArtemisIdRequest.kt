package com.ringpublishing.tracking.internal.api.request

internal data class ArtemisIdRequest(
    val user: User,
    val tid: String
)

internal data class User(
    val id: UserId,
    val sso: UserSso
)

internal data class UserId(
    val local: String,
    val global: String
)

internal data class UserSso(
    val name: String?,
    val id: String?
)
