package co.softov.morestuff.android.ui.model.map

import co.softov.morestuff.android.domain.model.ReviewTasks
import co.softov.morestuff.android.domain.service.TimeManager
import co.softov.morestuff.android.domain.model.TaskDomain
import co.softov.morestuff.android.domain.util.TimeFormatter
import co.softov.morestuff.android.ui.model.ReviewItemUiModel

class ReviewTasksMapper(
    private val timeFormatter: TimeFormatter,
    private val timeManager: TimeManager,
) {
    private fun internalMap(input: TaskDomain, position: Int, count: Int): ReviewItemUiModel {
        val inputDateTime = timeManager.nowLocalDateTimeString

        val createTime: String
        if (input.completeTime != null) {
            createTime = timeFormatter.formatToDateTime(input.completeTime) ?: ""
            timeFormatter.formatToDateTime(input.completeTime)
        } else {
            createTime = timeFormatter.formatToDateTime(inputDateTime) ?: ""
        }

        return ReviewItemUiModel(
            id = input.id,
            createTime = createTime,
            title = input.title,
            priorityScore = input.priorityScore,
            position = "$position/$count",
            isCompleted = input.isComplete,
            extraDetails = input.extraDetails
        )
    }

    fun map(input: ReviewTasks): List<ReviewItemUiModel> {
        return input.tasks.mapIndexed { index, task ->
            internalMap(task, index + 1, input.activeTasksCount)
        }
    }
}