package io.middlepoint.morestuff.shared.ui.screen.search

import androidx.compose.runtime.Composable
import io.middlepoint.morestuff.shared.ui.MoleculeViewModel
import kotlinx.coroutines.flow.Flow


class SearchViewModel: MoleculeViewModel<SearchEvent, SearchState>() {

    override val initialState: SearchState = SearchState()
    @Composable
    override fun models(events: Flow<SearchEvent>): SearchState {
        return searchModel(
            initialState = initialState,
            events = events,
        )
    }

}




