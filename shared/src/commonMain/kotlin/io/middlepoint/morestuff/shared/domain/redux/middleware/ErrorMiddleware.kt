package io.middlepoint.morestuff.shared.domain.redux.middleware

import co.touchlab.kermit.Logger
import io.middlepoint.morestuff.shared.domain.model.Failure
import io.middlepoint.morestuff.shared.domain.redux.*
import io.middlepoint.morestuff.shared.domain.redux.store.Action
import io.middlepoint.morestuff.shared.domain.redux.store.Dispatch
import io.middlepoint.morestuff.shared.domain.redux.store.Next
import io.middlepoint.morestuff.shared.domain.redux.store.NoOp
import kotlinx.coroutines.CoroutineScope

data class ErrorAction(val failure: Failure) : Action.FeatureAction()

class ErrorMiddleware(
    private val logger: Logger,
): Middleware<AppState> {

    override fun invoke(
        state: AppState,
        action: Action,
        dispatch: Dispatch,
        next: Next<AppState>,
        scope: CoroutineScope
    ): Action {
        when (action) {
            is ErrorAction -> {
                logger.e("store-error ---> in ${action.log}")
                return action
            }
            else -> NoOp
        }
        return next(state, action, dispatch)
    }
}