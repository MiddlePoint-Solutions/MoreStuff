package io.middlepoint.morestuff.shared.domain.model

data class ReviewTasks(
    val tasks: List<TaskDomain>,
    val activeTasksCount: Int,
)
