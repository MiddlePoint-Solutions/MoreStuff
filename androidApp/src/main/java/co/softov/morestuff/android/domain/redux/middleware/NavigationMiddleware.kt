package co.softov.morestuff.android.domain.redux.middleware

import co.softov.morestuff.android.domain.redux.Action
import co.softov.morestuff.android.domain.redux.AppState
import co.softov.morestuff.android.domain.redux.NoOp
import co.softov.morestuff.android.domain.redux.middleware.NavigationAction.*
import com.github.terrakok.cicerone.Router
import com.github.terrakok.cicerone.Screen
import co.softov.morestuff.android.domain.redux.Dispatch
import co.softov.morestuff.android.domain.redux.Middleware
import co.softov.morestuff.android.domain.redux.Next
import kotlinx.coroutines.CoroutineScope

sealed class NavigationAction : Action.FeatureAction() {

    data class NavigateTo(val screen: Screen) : NavigationAction() {
        override fun toString(): String {
            return "NavigationAction, NavigateTo: ${screen.screenKey}"
        }
    }

    data class NewRoot(val screen: Screen) : NavigationAction() {
        override fun toString(): String {
            return "NavigationAction, NewRoot: ${screen.screenKey}"
        }
    }

    object Back : NavigationAction()

}

class NavigationMiddleware(
    private val router: Router
) : Middleware<AppState> {

    override fun invoke(
        state: AppState,
        action: Action,
        dispatch: Dispatch,
        next: Next<AppState>,
        scope: CoroutineScope
    ): Action {
        when (action) {
            is NewRoot -> router.newRootScreen(action.screen)
            is NavigateTo -> router.navigateTo(action.screen)
            Back -> router.exit()
            else -> NoOp
        }
        return next(state, action, dispatch)
    }
}