package co.softov.morestuff.android.domain.redux.middleware

import co.softov.morestuff.android.domain.redux.AppState
import co.softov.morestuff.android.domain.redux.Dispatch
import co.softov.morestuff.android.domain.redux.Next
import co.softov.morestuff.android.domain.redux.store.Action
import co.softov.morestuff.android.domain.redux.store.NoOp
import co.softov.morestuff.android.domain.usecase.message.ClearActiveReminderMessagesUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

sealed class DevAction : Action.FeatureAction() {
    object ClearActiveReminderMessages : DevAction()
}

class DevMiddleware(
    private val clearActiveReminderMessagesUseCase: ClearActiveReminderMessagesUseCase
) : Middleware<AppState> {

    override fun invoke(
        state: AppState,
        action: Action,
        dispatch: Dispatch,
        next: Next<AppState>,
        scope: CoroutineScope
    ): Action {
        when (action) {
            is DevAction.ClearActiveReminderMessages -> scope.launch {
                clearActiveReminderMessagesUseCase()
            }
            else -> NoOp
        }

        return next(state, action, dispatch)
    }
}