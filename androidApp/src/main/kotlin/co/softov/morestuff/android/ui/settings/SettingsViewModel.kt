package co.softov.morestuff.android.ui.settings

import androidx.compose.runtime.Composable
import co.softov.morestuff.android.app.presentation.viewmodel.MoleculeViewModel
import co.softov.morestuff.android.domain.redux.AppStore
import kotlinx.coroutines.flow.Flow

class SettingsViewModel(
    private val store: AppStore,
) : MoleculeViewModel<SettingsEvent, SettingsState>() {

    override val initialState: SettingsState = with(store.state.value.settings) {
        SettingsState(
            appTheme = appTheme,
            snoozeLimit = snoozeLimit,
            devSettings = devSettings,
            reviewTime = reviewTime,
            inputVoiceLanguage = voiceInputLanguage
        )
    }

    @Composable
    override fun models(events: Flow<SettingsEvent>): SettingsState {
        return settingsModel(
            initialState = initialState,
            events = events,
            store = store
        )
    }

}


