package io.middlepoint.morestuff.android.domain.model

data class ReviewTasks(
    val tasks: List<TaskDomain>,
    val activeTasksCount: Int,
)
