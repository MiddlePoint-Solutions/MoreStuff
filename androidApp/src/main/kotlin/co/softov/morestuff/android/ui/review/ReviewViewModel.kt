package co.softov.morestuff.android.ui.review

import androidx.compose.runtime.Composable
import co.softov.morestuff.android.app.presentation.viewmodel.MoleculeViewModel
import kotlinx.coroutines.flow.Flow


class ReviewViewModel(
) : MoleculeViewModel<ReviewViewEvent, ReviewState>() {

    override val initialState: ReviewState = ReviewState()
    @Composable
    override fun models(events: Flow<ReviewViewEvent>): ReviewState {
        return reviewModel(
            initialState = initialState,
            events = events,
        )
    }
}
