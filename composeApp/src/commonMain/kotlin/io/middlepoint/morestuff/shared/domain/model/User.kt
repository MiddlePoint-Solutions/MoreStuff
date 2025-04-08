package io.middlepoint.morestuff.shared.domain.model

import kotlinx.datetime.Instant

data class User(
    val id: String,
    val fullName: String?,
    val email: String?,
    val createdAt: Instant?,
    val lastLogin: Instant?
)