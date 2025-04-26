package io.middlepoint.morestuff.shared.data.middleware

import co.touchlab.kermit.Logger
import io.middlepoint.morestuff.shared.data.sync.DataSyncManager
import io.middlepoint.morestuff.shared.domain.enums.SyncTrigger.*
import io.middlepoint.morestuff.shared.domain.redux.Middleware
import io.middlepoint.morestuff.shared.domain.redux.action.SyncAction.*
import io.middlepoint.morestuff.shared.domain.redux.action.UserAction
import io.middlepoint.morestuff.shared.domain.redux.state.AppState
import io.middlepoint.morestuff.shared.domain.redux.store.Action
import io.middlepoint.morestuff.shared.domain.redux.store.Dispatch
import io.middlepoint.morestuff.shared.domain.redux.store.Next
import io.middlepoint.morestuff.shared.domain.redux.store.NoOp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class SyncMiddleware(
  private val manager: DataSyncManager
) : Middleware<AppState> {

  private val logger = Logger.withTag("SyncMiddleware")

  private var syncJob: Job? = null

  override fun invoke(
    state: AppState,
    action: Action,
    dispatch: Dispatch,
    next: Next<AppState>,
    scope: CoroutineScope
  ): Action {
    when (action) {

      // TODO: implement interval sync mechanisem
      is SyncIntervalAction -> {
        dispatch(SyncTriggerAction(Interval))
      }

      is UserAction.Authenticated -> {
        dispatch(SyncTriggerAction(Auth))
      }

      is SyncTriggerAction -> {
        // TODO: check trigger and decide if sync should be performed
        // TODO: we should check when the last sync time made so we don't sync to often
        if (syncJob == null || syncJob?.isActive == false) {
          performSync(dispatch, scope)
        }
      }

      else -> NoOp
    }

    return next(state, action, dispatch)
  }

  private fun performSync(dispatch: Dispatch, scope: CoroutineScope) {
    syncJob = scope.launch {
      manager.sync().collect { status ->
        dispatch(UpdateSyncStatusAction(status))
      }
    }
  }

}
