package co.softov.morestuff.android.domain.redux.middleware

import co.softov.morestuff.android.domain.redux.AppState
import co.softov.morestuff.android.domain.redux.Dispatch
import co.softov.morestuff.android.domain.redux.Next
import co.softov.morestuff.android.domain.redux.state.SettingAction
import co.softov.morestuff.android.domain.redux.store.Action
import co.softov.morestuff.android.domain.redux.store.InitAction
import co.softov.morestuff.android.domain.redux.store.NoOp
import co.softov.morestuff.android.domain.repository.UserRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

sealed class DevAction : Action.FeatureAction() {
    // TODO: Add clear all stale messages action + add settings option
    object ClearStaleMessages: DevAction()
}

class DevMiddleware(

) : Middleware<AppState> {

    override fun invoke(
        state: AppState,
        action: Action,
        dispatch: Dispatch,
        next: Next<AppState>,
        scope: CoroutineScope
    ): Action {
        when (action) {
            is DevAction.ClearStaleMessages -> {

            }
            else -> NoOp
        }

        return next(state, action, dispatch)
    }
}