package co.softov.morestuff.android.domain.redux.middleware

import co.softov.morestuff.android.domain.redux.AppState
import co.softov.morestuff.android.domain.redux.state.SettingAction
import co.softov.morestuff.android.domain.redux.store.Action
import co.softov.morestuff.android.domain.redux.store.Dispatch
import co.softov.morestuff.android.domain.redux.store.Next
import co.softov.morestuff.android.domain.redux.store.NoOp
import co.softov.morestuff.android.domain.usecase.settings.SelectLanguageUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class InputVoiceLanguageMiddleware(
    private val selectLanguageUseCase: SelectLanguageUseCase
) : Middleware<AppState> {

    override fun invoke(
        state: AppState,
        action: Action,
        dispatch: Dispatch,
        next: Next<AppState>,
        scope: CoroutineScope,
    ): Action {
        when (action) {
            is SettingAction.SetLanguage -> scope.launch {
                selectLanguageUseCase(action.language.name)
            }
            else -> NoOp
        }

        return next(state, action, dispatch)
    }
}