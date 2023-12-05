package co.softov.morestuff.android.domain.model

data class ScopeDomain(
    val id: Long,
    val uid: String,
    val name: String,
    val order: Int,
)

val scopeAll = ScopeDomain(
    id = 1,
    uid = "",
    name = "All",
    order = 0,
)
