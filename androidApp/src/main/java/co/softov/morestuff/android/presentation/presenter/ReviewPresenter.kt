package co.softov.morestuff.android.presentation.presenter

import co.softov.morestuff.android.app.presentation.viewmodel.BaseViewEvent
import co.softov.morestuff.android.app.presentation.viewmodel.BaseViewState
import co.softov.morestuff.android.domain.enums.ReviewActionType
import co.softov.morestuff.android.ui.model.ReviewItemUiModel

enum class ReviewRound {
    Priority, Final
}

data class ReviewModel(
    val round: ReviewRound = ReviewRound.Priority,
    val items: List<ReviewItemUiModel> = listOf(),
    val actions: List<Pair<ReviewItemUiModel, ReviewActionType>> = listOf()
) : BaseViewState

sealed class ReviewViewEvent : BaseViewEvent {
    data class SetupInitialRound(
        val round: ReviewRound,
        val items: List<ReviewItemUiModel>,
    ) : ReviewViewEvent()

    data class SetupRound(val round: ReviewRound) : ReviewViewEvent()

    data class ItemReview(val item: ReviewItemUiModel, val action: ReviewActionType) : ReviewViewEvent()

    data class Undo(val item: ReviewItemUiModel) : ReviewViewEvent()
}


