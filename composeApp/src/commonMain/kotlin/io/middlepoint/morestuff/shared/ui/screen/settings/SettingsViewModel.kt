package io.middlepoint.morestuff.shared.ui.screen.settings

import androidx.compose.runtime.Composable
import io.middlepoint.morestuff.shared.ui.MoleculeViewModel
import io.middlepoint.morestuff.shared.domain.redux.AppStore
import kotlinx.coroutines.flow.SharedFlow

class SettingsViewModel(
    private val store: AppStore,
) : MoleculeViewModel<SettingsEvent, SettingsState>() {

    override val initialState: SettingsState = with(store.state.value.settings) {
        SettingsState(
            appTheme = appTheme,
            snoozeLimit = snoozeLimit,
            devSettings = devSettings,
            //reviewTime = reviewTime,
            inputVoiceLanguage = voiceInputLanguage,
            apiKey = apiKey
        )
    }

    @Composable
    override fun models(events: SharedFlow<SettingsEvent>): SettingsState {
        return settingsModel(
            initialState = initialState,
            events = events,
            store = store
        )
    }

}


