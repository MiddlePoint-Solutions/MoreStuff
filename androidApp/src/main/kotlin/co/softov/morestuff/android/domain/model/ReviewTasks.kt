package co.softov.morestuff.android.domain.model

data class ReviewTasks(
    val tasks: List<TaskDomain>,
    val activeTasksCount: Int,
)
