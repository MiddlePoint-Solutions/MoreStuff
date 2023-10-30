package co.softov.morestuff.android.ui.onboarding

import co.softov.morestuff.android.R
import co.softov.morestuff.android.ui.model.ReviewItemUiModel

data object DefaultHintTask{
    val tasks = listOf(
        ReviewItemUiModel(
            id = 1,
            createTime = "10:30 AM",
            title = R.string.right_priority.toString(),
            priorityScore = 5,
            isCompleted = false,
        ),
        ReviewItemUiModel(
            id = 2,
            createTime = "11:00 AM",
            title = R.string.left_priority.toString(),
            priorityScore = 3,
            isCompleted = false
        ),
        ReviewItemUiModel(
            id = 3,
            createTime = "11:30 AM",
            title = R.string.up_priority.toString(),
            priorityScore = 7,
            isCompleted = false
        ),
        ReviewItemUiModel(
            id = 4,
            createTime = "12:30 AM",
            title = R.string.down_priority.toString(),
            priorityScore = -1,
            isCompleted = false
        )
    )
}