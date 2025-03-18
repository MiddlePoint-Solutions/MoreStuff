package io.middlepoint.morestuff.shared.domain.redux.middleware

import MoreStuff.composeApp.BuildConfig
import co.touchlab.kermit.Logger
import io.middlepoint.morestuff.shared.domain.redux.AppState
import io.middlepoint.morestuff.shared.domain.redux.store.Action
import io.middlepoint.morestuff.shared.domain.redux.store.Dispatch
import io.middlepoint.morestuff.shared.domain.redux.store.Next
import kotlinx.coroutines.CoroutineScope

class LoggerMiddleware(
    private val logger: Logger,
) : Middleware<AppState> {

    override fun invoke(
        state: AppState,
        action: Action,
        dispatch: Dispatch,
        next: Next<AppState>,
        scope: CoroutineScope
    ): Action {
        if (BuildConfig.DEBUG) {
            logger.d("store-middleware ---> in ${action.log}")
            val returnValue = next(state, action, dispatch)
            logger.d("store-middleware <--- out ${getOutMessage(action, returnValue)}")
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