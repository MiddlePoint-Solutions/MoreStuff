package co.softov.morestuff.android.ui.home

import androidx.compose.runtime.Composable
import androidx.lifecycle.SavedStateHandle
import co.softov.morestuff.android.app.presentation.viewmodel.MoleculeViewModel
import kotlinx.coroutines.flow.Flow

class HomePresenter(
    private val savedState: SavedStateHandle
) : MoleculeViewModel<HomeUiEvent, HomeScopeState>() {

    override val initialState: HomeScopeState = savedState["Scopes"] ?: HomeScopeState()

    @Composable
    override fun models(events: Flow<HomeUiEvent>): HomeScopeState {
        return homeModel(initialState, events)
    }

    override fun onSaveState(model: HomeScopeState) {
        savedState["Scopes"] = model
    }
}