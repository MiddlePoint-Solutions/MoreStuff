package co.softov.morestuff.android.ui.review

import co.softov.morestuff.android.app.presentation.viewmodel.BaseViewEvent
import co.softov.morestuff.android.domain.enums.PriorityActionType
import co.softov.morestuff.android.domain.model.ScopeDomain
import co.softov.morestuff.android.domain.model.defaultScope
import co.softov.morestuff.android.ui.model.ReviewItemUiModel
import co.softov.morestuff.android.ui.review.swipeable.SwipeDirection
import com.arkivanov.essenty.parcelable.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
sealed class ReviewRound : android.os.Parcelable {
    data class Review(val scopeId: Long) : ReviewRound()
    data object Final : ReviewRound()
}
@Parcelize
data class ReviewState(
    val round: ReviewRound = ReviewRound.Review(scopeId = defaultScope.id),
    val currentScope: ScopeDomain = defaultScope,
    val items: List<ReviewItemUiModel> = listOf(),
    val actions: List<Pair<ReviewItemUiModel, PriorityActionType>> = listOf(),
    val scopes: List<ScopeDomain> = listOf(),
    val reviewHintEnabled: Boolean = false
) : Parcelable

sealed class ReviewViewEvent : BaseViewEvent {
    data class Undo(val item: ReviewItemUiModel) : ReviewViewEvent()
    data class ItemSwipe(
        val item: ReviewItemUiModel,
        val direction: SwipeDirection
    ) : ReviewViewEvent()
    data class CompleteTask(
        val item: ReviewItemUiModel
    ) : ReviewViewEvent()
    data object ToggleReviewHint : ReviewViewEvent()
    data class LoadScope(val scopeId: Long) : ReviewViewEvent()
}

