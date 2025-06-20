package io.middlepoint.morestuff.shared.domain.redux.middleware

import io.middlepoint.morestuff.shared.domain.DevTools
import io.middlepoint.morestuff.shared.domain.enums.AppSetting
import io.middlepoint.morestuff.shared.domain.redux.AppState
import io.middlepoint.morestuff.shared.domain.redux.state.SettingAction
import io.middlepoint.morestuff.shared.domain.redux.state.SettingAction.EnableDevSettings
import io.middlepoint.morestuff.shared.domain.redux.state.SettingAction.InitSettings
import io.middlepoint.morestuff.shared.domain.redux.state.SettingAction.OnBoardingComplete
import io.middlepoint.morestuff.shared.domain.redux.state.SettingAction.SetAppTheme
import io.middlepoint.morestuff.shared.domain.redux.state.SettingAction.SetSnoozeLimit
import io.middlepoint.morestuff.shared.domain.redux.store.Action
import io.middlepoint.morestuff.shared.domain.redux.store.Dispatch
import io.middlepoint.morestuff.shared.domain.redux.store.InitStoreAction
import io.middlepoint.morestuff.shared.domain.redux.store.Next
import io.middlepoint.morestuff.shared.domain.redux.store.NoOp
import io.middlepoint.morestuff.shared.domain.usecase.settings.GetAppSettingsUseCase
import io.middlepoint.morestuff.shared.domain.usecase.settings.SaveUserSettingUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class SettingsMiddleware(
    private val getAppSettingsUseCase: GetAppSettingsUseCase,
    private val saveUserSettingUseCase: SaveUserSettingUseCase,
    private val devTools: DevTools
    ) : Middleware<AppState> {

    override fun invoke(
        state: AppState,
        action: Action,
        dispatch: Dispatch,
        next: Next<AppState>,
        scope: CoroutineScope,
    ): Action {
        when (action) {
            is InitStoreAction -> scope.launch {
                devTools.exportJsonData(false)
                dispatch(InitSettings(getAppSettingsUseCase()))
            }

            is EnableDevSettings -> scope.launch {
                saveUserSettingUseCase(AppSetting.DevSettings, action.enable)
            }

            is SetSnoozeLimit -> scope.launch {
                saveUserSettingUseCase(AppSetting.SnoozeLimit, action.amount)
            }

            is SetAppTheme -> scope.launch {
                saveUserSettingUseCase(AppSetting.Theme, action.theme.name)
            }

            is OnBoardingComplete -> scope.launch {
                saveUserSettingUseCase(AppSetting.FirstTime, false)
                dispatch(TaskAction.CreateHintTask)
            }

            /*is SettingAction.SetReviewTimeAction -> scope.launch {
                saveUserSettingUseCase(AppSetting.ReviewTime, Pair(action.hour, action.minute))
            }*/

            is SettingAction.EnableReviewHint -> scope.launch {
                saveUserSettingUseCase(AppSetting.ShowHintArrowPriority, action.enable)
            }

            is SettingAction.SetVoiceLanguage -> scope.launch {
                saveUserSettingUseCase(AppSetting.VoiceInputLanguage, action.language.name)
            }

            else -> NoOp
        }

        return next(state, action, dispatch)
    }
}