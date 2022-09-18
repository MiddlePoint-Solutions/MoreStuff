package co.softov.morestuff.android.domain.redux.middleware

import co.softov.morestuff.android.domain.model.Failure
import co.softov.morestuff.android.domain.redux.*
import co.softov.morestuff.android.domain.redux.store.Action
import co.softov.morestuff.android.domain.redux.store.NoOp
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