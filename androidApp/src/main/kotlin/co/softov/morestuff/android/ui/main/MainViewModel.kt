package co.softov.morestuff.android.ui.main

import androidx.compose.runtime.Composable
import co.softov.morestuff.android.app.presentation.viewmodel.MoleculeViewModel
import kotlinx.coroutines.flow.Flow

class MainViewModel : MoleculeViewModel<MainEvent, MainState>() {

    override val initialState: MainState = MainState()

    @Composable
    override fun models(events: Flow<MainEvent>): MainState {
        return mainModel(initialState, events)
    }
}
