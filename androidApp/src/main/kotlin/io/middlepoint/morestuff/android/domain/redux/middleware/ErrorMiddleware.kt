package io.middlepoint.morestuff.android.domain.redux.middleware

import io.middlepoint.morestuff.android.domain.model.Failure
import io.middlepoint.morestuff.android.domain.redux.*
import io.middlepoint.morestuff.android.domain.redux.store.Action
import io.middlepoint.morestuff.android.domain.redux.store.Dispatch
import io.middlepoint.morestuff.android.domain.redux.store.Next
import io.middlepoint.morestuff.android.domain.redux.store.NoOp
import kotlinx.coroutines.CoroutineScope
import timber.log.Timber

data class ErrorAction(val failure: Failure) : Action.FeatureAction()

class ErrorMiddleware : Middleware<AppState> {

    override fun invoke(
        state: AppState,
        action: Action,
        dispatch: Dispatch,
        next: Next<AppState>,
        scope: CoroutineScope
    ): Action {
        when (action) {
            is ErrorAction -> {
                Timber.e("store-error ---> in ${action.log}")
                return action
            }
            else -> NoOp
        }
        return next(state, action, dispatch)
    }
}