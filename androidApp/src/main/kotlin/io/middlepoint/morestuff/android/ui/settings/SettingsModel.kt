package io.middlepoint.morestuff.android.ui.settings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import io.middlepoint.morestuff.android.domain.enums.AppTheme
import io.middlepoint.morestuff.android.domain.enums.Language
import io.middlepoint.morestuff.android.domain.redux.AppStore
import io.middlepoint.morestuff.android.domain.redux.state.SettingAction
import kotlinx.coroutines.flow.Flow

@Composable
fun settingsModel(
    initialState: SettingsState,
    events: Flow<SettingsEvent>,
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
                SettingsEvent.EnableDevSettings -> {
                    store.dispatch(SettingAction.EnableDevSettings(true))
                    state.copy(devSettings = true)
                }
                is SettingsEvent.SetReviewTime -> {
                    store.dispatch(SettingAction.SetReviewTimeAction(event.hour, event.minute))
                    state.copy(reviewTime = Pair(event.hour, event.minute))
                }
                is SettingsEvent.SelectLanguage -> {
                    store.dispatch(SettingAction.SetVoiceLanguage(Language[event.index]))
                    state.copy(inputVoiceLanguage = Language[event.index])
                }
            }
        }
    }

    return state
}
