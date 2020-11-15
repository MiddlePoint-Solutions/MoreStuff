package co.softov.morestuff.androidApp.domain.redux.middleware

import co.softov.morestuff.android.BuildConfig
import co.softov.morestuff.androidApp.domain.redux.Action
import co.softov.morestuff.androidApp.domain.redux.AppState
import com.iiitech.operations.domain.redux.Dispatch
import com.iiitech.operations.domain.redux.Middleware
import com.iiitech.operations.domain.redux.Next
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
            "${action::class.java.simpleName}(NO CHANGE)"
        } else {
            returnValue.log
        }
    }

}