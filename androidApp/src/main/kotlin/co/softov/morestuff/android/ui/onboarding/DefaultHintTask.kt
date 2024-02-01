package co.softov.morestuff.android.ui.onboarding

import android.content.Context
import androidx.compose.runtime.Stable
import co.softov.morestuff.android.R
import co.softov.morestuff.android.ui.model.ReviewItemUiModel

@Stable
fun buildReviewHintTasks(context: Context): List<ReviewItemUiModel> =
    buildList {
        val res = context.resources
        val ids = listOf(
            R.string.onboarding_review_task1,
            R.string.onboarding_review_task2,
            R.string.onboarding_review_task3,
            R.string.onboarding_review_task4,
            R.string.onboarding_review_task5,
            R.string.onboarding_review_task6,
            R.string.onboarding_review_task7,
            R.string.onboarding_review_task8,
            R.string.onboarding_review_task9,
            R.string.onboarding_review_task10,
        )

        repeat(ids.size) {
            add(
                ReviewItemUiModel(
                    id = it.toLong(),
                    createTime = "",
                    position = "${it + 1}/${ids.size}",
                    title = res.getString(ids[it]),
                    priorityScore = 5,
                    isCompleted = false,
                    extraDetails = false
                )
            )
        }
    }