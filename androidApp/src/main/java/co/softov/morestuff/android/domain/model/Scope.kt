package co.softov.morestuff.android.domain.model

data class Scope(
    val id: Long,
    val title: String,
    val createTime: String,
    val completeTime: String? = null
)
