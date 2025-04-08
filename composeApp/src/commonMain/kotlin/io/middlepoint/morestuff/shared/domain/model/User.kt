package io.middlepoint.morestuff.shared.domain.model

data class User(
    val id: String,
    val fullName: String,
    val email: String,
    val createdAt: String,
    val lastLogin: String
)