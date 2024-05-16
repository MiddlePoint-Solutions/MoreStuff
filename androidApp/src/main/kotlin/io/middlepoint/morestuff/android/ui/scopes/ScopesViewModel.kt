package io.middlepoint.morestuff.android.ui.scopes

import androidx.compose.runtime.Composable
import androidx.lifecycle.SavedStateHandle
import io.middlepoint.morestuff.android.app.presentation.viewmodel.MoleculeViewModel
import kotlinx.coroutines.flow.Flow


class ScopesViewModel(
    private val savedState: SavedStateHandle,
) : MoleculeViewModel<ScopesUiEvent, ScopesState>() {

    override val initialState: ScopesState = savedState["Scopes"] ?: ScopesState()

    @Composable
    override fun models(events: Flow<ScopesUiEvent>): ScopesState {
        return scopesModel(
            initialState = initialState,
            events = events,
        )
    }

    override fun onSaveState(model: ScopesState) {
        savedState["Scopes"] = model
    }
}
