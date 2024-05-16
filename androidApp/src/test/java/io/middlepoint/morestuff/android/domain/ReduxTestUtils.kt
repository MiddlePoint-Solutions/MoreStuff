package io.middlepoint.morestuff.android.domain

import io.middlepoint.morestuff.android.domain.redux.store.Action
import io.middlepoint.morestuff.android.domain.redux.AppState
import io.middlepoint.morestuff.android.domain.redux.store.Dispatch
import io.middlepoint.morestuff.android.domain.redux.middleware.Middleware
import io.middlepoint.morestuff.android.domain.redux.store.Next
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