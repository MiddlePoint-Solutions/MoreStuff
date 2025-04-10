package io.middlepoint.morestuff.shared.data.middleware

import co.touchlab.kermit.Logger
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.status.SessionStatus
import io.middlepoint.morestuff.shared.data.mapper.DataMappers
import io.middlepoint.morestuff.shared.domain.redux.Middleware
import io.middlepoint.morestuff.shared.domain.redux.state.AppState
import io.middlepoint.morestuff.shared.domain.redux.action.UserAction
import io.middlepoint.morestuff.shared.domain.redux.store.Action
import io.middlepoint.morestuff.shared.domain.redux.store.Dispatch
import io.middlepoint.morestuff.shared.domain.redux.store.InitStoreAction
import io.middlepoint.morestuff.shared.domain.redux.store.Next
import io.middlepoint.morestuff.shared.domain.redux.store.NoOp
import io.middlepoint.morestuff.shared.ui.screen.settings.SettingsEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.Job

class AuthMiddleware(
  private val supabase: SupabaseClient,
  private val dataMappers: DataMappers
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
        initAuthEvents(scope, dispatch)
      }

      is UserAction.SignOut -> {
        scope.launch {  supabase.auth.signOut() }
      }

      else -> NoOp
    }
    return next(state, action, dispatch)
  }

  private fun initAuthEvents(scope: CoroutineScope, dispatch: Dispatch) {
    eventsJob = scope.launch {
      supabase.auth.sessionStatus.collect {
        when (it) {
          is SessionStatus.Authenticated -> {
            logger.d("Received new authenticated session: ${it.source}")
            logger.d { "user: ${it.session.user}" }

            it.session.user?.let { userInfo ->
              val user = dataMappers.userDataMapper(userInfo)
              dispatch(UserAction.Authenticated(user))
            }
          }

          SessionStatus.Initializing -> logger.d("Initializing")
          is SessionStatus.RefreshFailure -> logger.d("Refresh failure ${it.cause}")
          is SessionStatus.NotAuthenticated -> {
            dispatch(UserAction.NotAuthenticated(it.isSignOut))
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