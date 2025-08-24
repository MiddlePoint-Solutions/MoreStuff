package io.middlepoint.morestuff.shared.ui.screen.settings

import androidx.compose.runtime.Composable
import io.middlepoint.morestuff.shared.domain.redux.AppStore
import io.middlepoint.morestuff.shared.domain.usecase.settings.GetApiKeyUseCase
import io.middlepoint.morestuff.shared.ui.MoleculeViewModel
import kotlinx.coroutines.flow.SharedFlow

class SettingsViewModel(
    private val store: AppStore,
    private val getApiKeyUseCase: GetApiKeyUseCase
) : MoleculeViewModel<SettingsEvent, SettingsState>() {

    override val initialState: SettingsState = with(store.state.value.settings) {
        SettingsState(
            appTheme = appTheme,
            snoozeLimit = snoozeLimit,
            devSettings = devSettings,
            //reviewTime = reviewTime,
            inputVoiceLanguage = voiceInputLanguage,
            apiKey = getInitialApiKey()
        )
    }

    private fun getInitialApiKey(): String {
        return try {
            getApiKeyUseCase()
        } catch (e: IllegalStateException) {
            ""
        }
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


