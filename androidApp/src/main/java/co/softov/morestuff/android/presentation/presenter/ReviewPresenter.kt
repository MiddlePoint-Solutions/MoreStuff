package co.softov.morestuff.android.presentation.presenter

import co.softov.morestuff.android.app.presentation.viewmodel.BaseViewEvent
import co.softov.morestuff.android.app.presentation.viewmodel.BaseViewState
import co.softov.morestuff.android.domain.enums.PriorityActionType
import co.softov.morestuff.android.ui.model.ReviewItemUiModel

enum class ReviewRound {
    Review, Final
}

data class ReviewModel(
    val round: ReviewRound = ReviewRound.Review,
    val items: List<ReviewItemUiModel> = listOf(),
    val actions: List<Pair<ReviewItemUiModel, PriorityActionType>> = listOf()
) : BaseViewState

sealed class ReviewViewEvent : BaseViewEvent {
    data class SetupInitialRound(
        val round: ReviewRound,
        val items: List<ReviewItemUiModel>,
    ) : ReviewViewEvent()

    data class SetupRound(val round: ReviewRound) : ReviewViewEvent()

    data class ItemReview(val item: ReviewItemUiModel, val action: PriorityActionType) : ReviewViewEvent()

    data class Undo(val item: ReviewItemUiModel) : ReviewViewEvent()

}


