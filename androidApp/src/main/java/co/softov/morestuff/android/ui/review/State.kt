package co.softov.morestuff.android.ui.review

import co.softov.morestuff.android.app.presentation.viewmodel.BaseViewEvent
import co.softov.morestuff.android.app.presentation.viewmodel.BaseViewState
import co.softov.morestuff.android.domain.enums.PriorityActionType
import co.softov.morestuff.android.domain.model.ScopeDomain
import co.softov.morestuff.android.domain.model.scopeAll
import co.softov.morestuff.android.ui.model.ReviewItemUiModel

sealed class ReviewRound {
    data class Review(val scopeId: Long) : ReviewRound()
    data object Final : ReviewRound()

}

data class ReviewModel(
    val round: ReviewRound = ReviewRound.Review(scopeId = scopeAll.id),
    val currentScope: ScopeDomain = scopeAll,
    val items: List<ReviewItemUiModel> = listOf(),
    val actions: List<Pair<ReviewItemUiModel, PriorityActionType>> = listOf()
) : BaseViewState

sealed class ReviewViewEvent : BaseViewEvent {
    data class SetupReviewRound(
        val currentScope: ScopeDomain,
        val items: List<ReviewItemUiModel>,
    ) : ReviewViewEvent()

    data object FinalRound : ReviewViewEvent()

    data class ItemReview(val item: ReviewItemUiModel, val action: PriorityActionType) :
        ReviewViewEvent()

    data class Undo(val item: ReviewItemUiModel) : ReviewViewEvent()

}


