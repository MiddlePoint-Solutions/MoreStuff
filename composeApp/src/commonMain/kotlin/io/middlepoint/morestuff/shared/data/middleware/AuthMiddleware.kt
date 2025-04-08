package io.middlepoint.morestuff.shared.data.middleware

import co.touchlab.kermit.Logger
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.status.SessionStatus
import io.middlepoint.morestuff.shared.domain.redux.*
import io.middlepoint.morestuff.shared.domain.redux.middleware.Middleware
import io.middlepoint.morestuff.shared.domain.redux.store.Action
import io.middlepoint.morestuff.shared.domain.redux.store.Dispatch
import io.middlepoint.morestuff.shared.domain.redux.store.InitStoreAction
import io.middlepoint.morestuff.shared.domain.redux.store.Next
import io.middlepoint.morestuff.shared.domain.redux.store.NoOp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.Job

class AuthMiddleware(
  private val supabase: SupabaseClient,
) : Middleware<AppState> {

  private var eventsJob: Job? = null
  private val logger = Logger.withTag("AuthMiddleware")

  override fun invoke(
    state: AppState,
    action: Action,
    dispatch: Dispatch,
    next: Next<AppState>,
    scope: CoroutineScope
  ): Action {

    when (action) {
      is InitStoreAction -> {
        initAuthEvents(scope)
      }

      else -> NoOp
    }
    return next(state, action, dispatch)
  }

  private fun initAuthEvents(scope: CoroutineScope) {
    eventsJob = scope.launch {
      supabase.auth.sessionStatus.collect {
        when (it) {
          is SessionStatus.Authenticated -> {
            logger.d("Received new authenticated session: ${it.source}")
            logger.d { "user: ${it.session.user}" }
          }

          SessionStatus.Initializing -> logger.d("Initializing")
          is SessionStatus.RefreshFailure -> logger.d("Refresh failure ${it.cause}") //Either a network error or a internal server error
          is SessionStatus.NotAuthenticated -> {
            if (it.isSignOut) {
              logger.d("User signed out")
            } else {
              logger.d("User not signed in")
            }
          }
        }
      }
    }
  }
}