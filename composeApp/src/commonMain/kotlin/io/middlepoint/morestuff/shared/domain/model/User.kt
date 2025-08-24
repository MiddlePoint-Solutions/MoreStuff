package io.middlepoint.morestuff.shared.domain.model

import kotlinx.datetime.Instant

data class User(
    val id: String,
    val fullName: String?,
    val email: String?,
    val createdAt: Instant?,
    val lastLogin: Instant?
) {

    override fun equals(other: Any?): Boolean {
        return super.equals(other)
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + (fullName?.hashCode() ?: 0)
        result = 31 * result + (email?.hashCode() ?: 0)
        result = 31 * result + (createdAt?.hashCode() ?: 0)
        result = 31 * result + (lastLogin?.hashCode() ?: 0)
        return result
    }

}