package io.middlepoint.morestuff.android.domain

import io.middlepoint.morestuff.shared.domain.redux.store.Action
import io.middlepoint.morestuff.shared.domain.redux.state.AppState
import io.middlepoint.morestuff.shared.domain.redux.store.Dispatch
import io.middlepoint.morestuff.shared.domain.redux.Middleware
import io.middlepoint.morestuff.shared.domain.redux.store.Next
import kotlinx.coroutines.coroutineScope
import org.junit.jupiter.api.Assertions

suspend fun Middleware<AppState>.testWith(
  state: AppState,
  action: Action,
): Action = coroutineScope { invoke(state, action,
  defaultDispatch(),
  createNext(), this) }

suspend fun Middleware<AppState>.testActionDispatch(
  state: AppState,
  action: Action,
  expected: Action
): Action =
    coroutineScope { invoke(state, action,
      expectedDispatch(expected),
      createNext(), this) }

private fun createNext(): Next<AppState> = { _, action, _ -> action }

private fun defaultDispatch(): Dispatch = {}
private fun expectedDispatch(expected: Action): Dispatch =
    { action ->
        Assertions.assertEquals(expected, action)
    }