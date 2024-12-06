package io.middlepoint.morestuff.shared.ui.screen.review

import androidx.compose.runtime.Composable
import io.middlepoint.morestuff.shared.ui.MoleculeViewModel
import kotlinx.coroutines.flow.SharedFlow


class ReviewViewModel(
) : MoleculeViewModel<ReviewViewEvent, ReviewState>() {

    override val initialState: ReviewState = ReviewState()
    @Composable
    override fun models(events: SharedFlow<ReviewViewEvent>): ReviewState {
        return reviewModel(
            initialState = initialState,
            events = events,
        )
    }
}
