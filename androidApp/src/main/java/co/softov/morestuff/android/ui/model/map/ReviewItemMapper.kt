package co.softov.morestuff.android.ui.model.map

import co.softov.morestuff.android.domain.service.TimeManager
import co.softov.morestuff.android.domain.model.TaskDomain
import co.softov.morestuff.android.domain.usecase.time.TimeFormatter
import co.softov.morestuff.android.ui.model.ReviewItemUiModel

class ReviewItemMapper(
    private val timeFormatter: TimeFormatter,
    private val timeManager: TimeManager,
) {
    fun map(input: TaskDomain): ReviewItemUiModel {
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
            priorityScore = input.priorityScore
        )
    }

    fun map(input: List<TaskDomain>): List<ReviewItemUiModel> {
        return input.map { map(it) }
    }
}