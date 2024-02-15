package co.softov.morestuff.android.ui.home

import androidx.compose.runtime.Composable
import androidx.lifecycle.SavedStateHandle
import co.softov.morestuff.android.app.presentation.viewmodel.MoleculeViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asSharedFlow

class HomePresenter(
    private val savedState: SavedStateHandle,
    private val appPresenter: AppPresenter,
) : MoleculeViewModel<HomeEvent, HomeState>() {

    override val initialState: HomeState = savedState["Scopes"] ?: HomeState()

    val notifications = appPresenter.notifications.asSharedFlow()

    @Composable
    override fun models(events: Flow<HomeEvent>): HomeState {
        return homeModel(
            initialState = initialState,
            events = events,
            notifications = appPresenter.notifications
        )
    }

    override fun onSaveState(model: HomeState) {
        savedState["Scopes"] = model
    }
}

