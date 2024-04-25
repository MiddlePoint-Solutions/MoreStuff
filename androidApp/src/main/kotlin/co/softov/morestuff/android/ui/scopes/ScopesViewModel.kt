package co.softov.morestuff.android.ui.scopes

import androidx.compose.runtime.Composable
import androidx.lifecycle.SavedStateHandle
import co.softov.morestuff.android.app.presentation.viewmodel.MoleculeViewModel
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
