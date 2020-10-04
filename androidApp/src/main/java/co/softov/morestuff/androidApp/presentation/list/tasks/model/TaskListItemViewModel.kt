package co.softov.morestuff.androidApp.presentation.list.tasks.model

data class TaskListItemViewModel(
    val id: Long,
    val createTime: String,
    val completeTime: String,
    val title: String
) {
    var expanded: Boolean = false
}