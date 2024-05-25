package io.middlepoint.morestuff.android.ui.search

import androidx.compose.runtime.Composable
import androidx.lifecycle.SavedStateHandle
import io.middlepoint.morestuff.android.app.presentation.viewmodel.MoleculeViewModel
import kotlinx.coroutines.flow.Flow


class SearchViewModel(
    private val savedState: SavedStateHandle,
) : MoleculeViewModel<SearchEvent, SearchState>() {

    override val initialState: SearchState = savedState["Search"] ?: SearchState()
    @Composable
    override fun models(events: Flow<SearchEvent>): SearchState {
        return searchModel(
            initialState = initialState,
            events = events,
        )
    }

    override fun onSaveState(model: SearchState) {
        savedState["Search"] = model
    }
}




