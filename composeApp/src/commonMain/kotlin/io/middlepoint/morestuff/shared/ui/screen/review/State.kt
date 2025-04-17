package io.middlepoint.morestuff.shared.ui.screen.review

import io.middlepoint.morestuff.shared.ui.model.ReviewItemUiModel
import io.middlepoint.morestuff.shared.ui.components.swipeable.SwipeDirection
import io.middlepoint.morestuff.shared.domain.enums.ReviewActionType
import io.middlepoint.morestuff.shared.domain.model.Uuid
import io.middlepoint.morestuff.shared.domain.model.core.Scope
import io.middlepoint.morestuff.shared.domain.model.core.defaultScope

sealed class ReviewRound {
  data class Review(val scopeId: Uuid) : ReviewRound()
  data object Final : ReviewRound()
}

data class ReviewState(
  val round: ReviewRound,
  val currentScope: Scope = defaultScope,
  val items: List<ReviewItemUiModel> = listOf(),
  val itemsForReview: Int = 0,
  val actions: List<Pair<ReviewItemUiModel, ReviewActionType>> = listOf(),
  val scopes: List<Scope> = listOf(),
  val reviewHintEnabled: Boolean = false
)

sealed class ReviewViewEvent {
  data class Undo(val item: ReviewItemUiModel) : ReviewViewEvent()

  data class ItemSwipe(
    val item: ReviewItemUiModel,
    val direction: SwipeDirection
  ) : ReviewViewEvent()

  data class CompleteTask(val item: ReviewItemUiModel) : ReviewViewEvent()
  data class DeleteTask(val item: ReviewItemUiModel) : ReviewViewEvent()
  data class LoadScope(val scopeId: Uuid) : ReviewViewEvent()
  data object ToggleReviewHint : ReviewViewEvent()
}

