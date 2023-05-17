package co.softov.morestuff.android.ui.model

data class TaskListItemViewModel(
    val id: Long,
    val createTime: String,
    val completeTime: String,
    val title: String
)