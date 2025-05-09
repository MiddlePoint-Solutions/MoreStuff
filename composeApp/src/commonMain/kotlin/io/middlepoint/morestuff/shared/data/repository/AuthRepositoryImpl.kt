package io.middlepoint.morestuff.shared.data.repository

import arrow.core.Either
import arrow.core.raise.either
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.status.SessionStatus
import io.middlepoint.morestuff.shared.domain.model.AuthFailure
import io.middlepoint.morestuff.shared.domain.model.Failure
import io.middlepoint.morestuff.shared.domain.repository.AuthRepository
import kotlinx.coroutines.flow.StateFlow

class AuthRepositoryImpl(
  private val supabase: SupabaseClient,
): AuthRepository {

  override val authEvents: StateFlow<SessionStatus>
    get() = supabase.auth.sessionStatus

  override suspend fun signOut(): Either<Failure, Boolean> = either {
    try {
      supabase.auth.signOut()
    } catch (e: Exception) {
      raise(AuthFailure(e.message))
    }
    true
  }

}