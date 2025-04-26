package io.middlepoint.morestuff.shared.data.middleware

import co.touchlab.kermit.Logger
import io.middlepoint.morestuff.shared.data.sync.DataSyncManager
import io.middlepoint.morestuff.shared.domain.redux.Middleware
import io.middlepoint.morestuff.shared.domain.redux.action.UserAction
import io.middlepoint.morestuff.shared.domain.redux.state.AppState
import io.middlepoint.morestuff.shared.domain.redux.store.Action
import io.middlepoint.morestuff.shared.domain.redux.store.Dispatch
import io.middlepoint.morestuff.shared.domain.redux.store.Next
import io.middlepoint.morestuff.shared.domain.redux.store.NoOp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class SyncMiddleware(
  private val manager: DataSyncManager
) : Middleware<AppState> {

  private val logger = Logger.withTag("SyncMiddleware")

  override fun invoke(
    state: AppState,
    action: Action,
    dispatch: Dispatch,
    next: Next<AppState>,
    scope: CoroutineScope
  ): Action {
    when (action) {
      is UserAction.Authenticated -> scope.launch {
        manager.sync().collect { logger.d { "$it" } }
      }

      else -> NoOp
    }

    return next(state, action, dispatch)
  }
}