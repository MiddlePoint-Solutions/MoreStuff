package io.middlepoint.morestuff.shared.data.repository

import arrow.core.Either
import arrow.core.raise.either
import co.touchlab.kermit.Logger
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.status.SessionStatus
import io.middlepoint.morestuff.shared.domain.model.AuthFailure
import io.middlepoint.morestuff.shared.domain.model.Failure
import io.middlepoint.morestuff.shared.domain.repository.AuthRepository
import io.middlepoint.morestuff.shared.domain.service.DataSyncManager
import kotlinx.coroutines.flow.StateFlow

class AuthRepositoryImpl(
  private val supabase: SupabaseClient,
  private val syncManager: DataSyncManager,
) : AuthRepository {

  private val logger = Logger.withTag("AuthRepository")

  override val authEvents: StateFlow<SessionStatus>
    get() = supabase.auth.sessionStatus

  override suspend fun signOut(): Either<Failure, Boolean> = either {
    try {
      syncManager.push()
      supabase.auth.signOut()
      syncManager.resetData()
    } catch (e: Exception) {
      logger.e("SignOut Exception", e)
      raise(AuthFailure(e.message))
    }
    true
  }

}