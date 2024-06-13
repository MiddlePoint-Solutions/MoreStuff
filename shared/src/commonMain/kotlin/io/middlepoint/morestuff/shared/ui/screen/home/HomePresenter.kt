package io.middlepoint.morestuff.shared.ui.screen.home

import androidx.compose.runtime.Composable
import io.middlepoint.morestuff.shared.ui.MoleculeViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asSharedFlow

class HomePresenter(
  private val appPresenter: AppPresenter,
) : MoleculeViewModel<HomeEvent, HomeState>() {

    override val initialState: HomeState = HomeState()

    val notifications = appPresenter.notifications.asSharedFlow()

    @Composable
    override fun models(events: Flow<HomeEvent>): HomeState {
        return homeModel(
            initialState = initialState,
            events = events,
            notifications = appPresenter.notifications
        )
    }

}

