package co.softov.morestuff.android.domain.model

data class ScopeDomain(
    val scopeId: Long,
    val uid: String,
    val name: String,
)

val ScopeAll = ScopeDomain(
    scopeId = 1,
    uid = "",
    name = "All"
)
