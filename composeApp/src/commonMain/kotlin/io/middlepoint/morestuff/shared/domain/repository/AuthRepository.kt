package io.middlepoint.morestuff.shared.domain.repository

import arrow.core.Either
import io.github.jan.supabase.auth.status.SessionStatus
import io.middlepoint.morestuff.shared.domain.model.Failure
import kotlinx.coroutines.flow.StateFlow

interface AuthRepository {

  val authEvents: StateFlow<SessionStatus>

  suspend fun signOut(): Either<Failure, Boolean>

}