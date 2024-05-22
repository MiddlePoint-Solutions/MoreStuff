package io.middlepoint.morestuff.shared.domain

import io.middlepoint.morestuff.shared.domain.redux.store.Action
import io.middlepoint.morestuff.shared.domain.redux.AppState
import io.middlepoint.morestuff.shared.domain.redux.store.Dispatch
import io.middlepoint.morestuff.shared.domain.redux.middleware.Middleware
import io.middlepoint.morestuff.shared.domain.redux.store.Next
import kotlinx.coroutines.coroutineScope
import org.junit.Assert

suspend fun Middleware<AppState>.testWith(
    state: AppState,
    action: Action,
): Action = coroutineScope { invoke(state, action,
  io.middlepoint.morestuff.shared.domain.defaultDispatch(),
  io.middlepoint.morestuff.shared.domain.createNext(), this) }

suspend fun Middleware<AppState>.testActionDispatch(
    state: AppState,
    action: Action,
    expected: Action
): Action =
    coroutineScope { invoke(state, action,
      io.middlepoint.morestuff.shared.domain.expectedDispatch(expected),
      io.middlepoint.morestuff.shared.domain.createNext(), this) }

private fun createNext(): Next<AppState> = { _, action, _ -> action }

private fun defaultDispatch(): Dispatch = {}
private fun expectedDispatch(expected: Action): Dispatch =
    { action ->
        Assert.assertEquals(expected, action)
    }