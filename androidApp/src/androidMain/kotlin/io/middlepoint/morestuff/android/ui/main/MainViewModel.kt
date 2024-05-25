package io.middlepoint.morestuff.android.ui.main

import androidx.compose.runtime.Composable
import io.middlepoint.morestuff.android.app.presentation.viewmodel.MoleculeViewModel
import kotlinx.coroutines.flow.Flow

class MainViewModel : MoleculeViewModel<MainEvent, MainState>() {

    override val initialState: MainState = MainState()

    @Composable
    override fun models(events: Flow<MainEvent>): MainState {
        return mainModel(initialState, events)
    }
}
