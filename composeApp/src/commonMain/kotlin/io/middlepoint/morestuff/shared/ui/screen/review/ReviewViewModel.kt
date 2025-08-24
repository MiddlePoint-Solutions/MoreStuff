package io.middlepoint.morestuff.shared.ui.screen.review

import androidx.compose.runtime.Composable
import io.middlepoint.morestuff.shared.domain.model.Uuid
import io.middlepoint.morestuff.shared.ui.MoleculeViewModel
import io.middlepoint.morestuff.shared.ui.screen.review.ReviewRound.*
import kotlinx.coroutines.flow.SharedFlow


class ReviewViewModel(
  scopeId: Uuid
) : MoleculeViewModel<ReviewViewEvent, ReviewState>() {

  override val initialState: ReviewState = ReviewState(Review(scopeId))

  @Composable
  override fun models(events: SharedFlow<ReviewViewEvent>): ReviewState {
    return reviewModel(
      initialState = initialState,
      events = events,
    )
  }
}
