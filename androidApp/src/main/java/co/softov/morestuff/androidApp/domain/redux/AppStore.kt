package co.softov.morestuff.androidApp.domain.redux

import co.softov.morestuff.androidApp.domain.redux.middleware.LoggerMiddleware
import co.softov.morestuff.androidApp.domain.redux.middleware.NavigationMiddleware
import co.softov.morestuff.androidApp.domain.redux.state.reduceSignInState
import com.iiitech.operations.domain.redux.SimpleStore

class AppStore(
    logger: LoggerMiddleware,
    navigator: NavigationMiddleware
) : SimpleStore<AppState>(
    AppState(),
    listOf(AppState::reduceSignInState),
    listOf(logger, navigator)
)
