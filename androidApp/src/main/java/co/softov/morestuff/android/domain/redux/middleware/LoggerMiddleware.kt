package co.softov.morestuff.android.domain.redux.middleware

import co.softov.morestuff.android.BuildConfig
import co.softov.morestuff.android.domain.redux.Action
import co.softov.morestuff.android.domain.redux.AppState
import co.softov.morestuff.android.domain.redux.Dispatch
import co.softov.morestuff.android.domain.redux.Middleware
import co.softov.morestuff.android.domain.redux.Next
import kotlinx.coroutines.CoroutineScope
import timber.log.Timber

class LoggerMiddleware : Middleware<AppState> {

    override fun invoke(
        state: AppState,
        action: Action,
        dispatch: Dispatch,
        next: Next<AppState>,
        scope: CoroutineScope
    ): Action {
        if (BuildConfig.DEBUG) {
            Timber.d("store-middleware ---> in ${action.log}")
            val returnValue = next(state, action, dispatch)
            Timber.d("store-middleware <--- out ${getOutMessage(action, returnValue)}")
            return returnValue
        }

        return next(state, action, dispatch)
    }

    private fun getOutMessage(action: Action, returnValue: Action): String {
        return if (action == returnValue) {
            "(No Change)"
        } else {
            returnValue.log
        }
    }

}