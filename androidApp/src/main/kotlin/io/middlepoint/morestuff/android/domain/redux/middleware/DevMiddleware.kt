package io.middlepoint.morestuff.android.domain.redux.middleware

import io.middlepoint.morestuff.android.domain.redux.AppState
import io.middlepoint.morestuff.android.domain.redux.store.Dispatch
import io.middlepoint.morestuff.android.domain.redux.store.Next
import io.middlepoint.morestuff.android.domain.redux.store.Action
import io.middlepoint.morestuff.android.domain.redux.store.NoOp
import io.middlepoint.morestuff.android.domain.usecase.message.ClearActiveReminderMessagesUseCase
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