package co.softov.morestuff.androidApp.domain.redux.middleware

import co.softov.morestuff.androidApp.domain.redux.Action
import co.softov.morestuff.androidApp.domain.redux.AppState
import co.softov.morestuff.androidApp.domain.redux.NoAction
import co.softov.morestuff.androidApp.domain.redux.middleware.NavigationAction.*
import com.github.terrakok.cicerone.Router
import com.github.terrakok.cicerone.Screen
import com.iiitech.operations.domain.redux.Dispatch
import com.iiitech.operations.domain.redux.Middleware
import com.iiitech.operations.domain.redux.Next
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
            else -> NoAction
        }
        return next(state, action, dispatch)
    }
}