package io.middlepoint.morestuff.shared.ui.screen.settings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import io.middlepoint.morestuff.shared.domain.enums.AppTheme
import io.middlepoint.morestuff.shared.domain.enums.Language
import io.middlepoint.morestuff.shared.domain.redux.AppStore
import io.middlepoint.morestuff.shared.domain.redux.state.SettingAction
import io.middlepoint.morestuff.shared.domain.usecase.settings.OpenAppSettingsUseCase
import kotlinx.coroutines.flow.Flow
import org.koin.compose.koinInject

@Composable
fun settingsModel(
  initialState: SettingsState,
  events: Flow<SettingsEvent>,
  openAppSettingsUseCase: OpenAppSettingsUseCase = koinInject(),
  store: AppStore
): SettingsState {
    var state by remember { mutableStateOf(initialState) }

    LaunchedEffect(Unit) {
        events.collect { event ->
            state = when (event) {
                is SettingsEvent.ChangeSnoozeLimit -> {
                    store.dispatch(SettingAction.SetSnoozeLimit(event.limit))
                    state.copy(snoozeLimit = event.limit)
                }
                is SettingsEvent.SelectAppTheme -> {
                    store.dispatch(SettingAction.SetAppTheme(AppTheme[event.index]))
                    state.copy(appTheme = AppTheme[event.index])
                }
                is SettingsEvent.EnableDevSettings -> {
                    store.dispatch(SettingAction.EnableDevSettings(event.enable))
                    state.copy(devSettings = event.enable)
                }
                /*is SettingsEvent.SetReviewTime -> {
                    store.dispatch(SettingAction.SetReviewTimeAction(event.hour, event.minute))
                    state.copy(reviewTime = Pair(event.hour, event.minute))
                }*/
                is SettingsEvent.SelectLanguage -> {
                    store.dispatch(SettingAction.SetVoiceLanguage(Language[event.index]))
                    state.copy(inputVoiceLanguage = Language[event.index])
                }
                is SettingsEvent.SetApiKey -> {
                    store.dispatch(SettingAction.SetApiKey(event.apiKey))
                    state.copy(apiKey = event.apiKey)
                }

                SettingsEvent.OpenAppSettings -> {
                    openAppSettingsUseCase()
                    state
                }
            }
        }
    }

    return state
}
