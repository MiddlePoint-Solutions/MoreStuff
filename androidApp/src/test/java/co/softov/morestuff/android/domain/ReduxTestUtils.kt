package co.softov.morestuff.android.domain

import co.softov.morestuff.android.domain.redux.Action
import co.softov.morestuff.android.domain.redux.AppState
import co.softov.morestuff.android.domain.redux.Dispatch
import co.softov.morestuff.android.domain.redux.middleware.Middleware
import co.softov.morestuff.android.domain.redux.Next
import kotlinx.coroutines.coroutineScope
import org.junit.Assert

suspend fun Middleware<AppState>.testWith(
    state: AppState,
    action: Action,
): Action = coroutineScope { invoke(state, action, defaultDispatch(), createNext(), this) }

suspend fun Middleware<AppState>.testActionDispatch(
    state: AppState,
    action: Action,
    expected: Action
): Action =
    coroutineScope { invoke(state, action, expectedDispatch(expected), createNext(), this) }

private fun createNext(): Next<AppState> = { _, action, _ -> action }

private fun defaultDispatch(): Dispatch = {}
private fun expectedDispatch(expected: Action): Dispatch =
    { action ->
        Assert.assertEquals(expected, action)
    }