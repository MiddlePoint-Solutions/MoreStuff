package co.softov.morestuff.android.domain.model

data class Task(
    val id: Long,
    val title: String,
    val createTime: String,
    val completeTime: String? = null
)
