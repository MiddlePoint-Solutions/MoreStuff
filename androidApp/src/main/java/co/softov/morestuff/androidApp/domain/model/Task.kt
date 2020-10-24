package co.softov.morestuff.androidApp.domain.model

data class Task(
    val id: Long,
    val title: String,
    val createTime: String,
    val completeTime: String?
)
