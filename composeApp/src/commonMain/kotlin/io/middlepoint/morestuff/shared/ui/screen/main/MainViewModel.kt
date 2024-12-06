package io.middlepoint.morestuff.shared.ui.screen.main

import androidx.compose.runtime.Composable
import io.middlepoint.morestuff.shared.ui.MoleculeViewModel
import kotlinx.coroutines.flow.SharedFlow

class MainViewModel : MoleculeViewModel<MainEvent, MainState>() {

    override val initialState: MainState = MainState()

    @Composable
    override fun models(events: SharedFlow<MainEvent>): MainState {
        return mainModel(initialState, events)
    }
}
