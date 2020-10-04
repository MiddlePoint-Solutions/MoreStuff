package co.softov.morestuff.androidApp.domain.model

data class Task(
    val id: Long,
    val createTime: Long,
    val completeTime: Long,
    val title: String
)
