package io.middlepoint.morestuff.shared.domain.usecase.activity

import io.middlepoint.morestuff.shared.domain.model.ActivityData
import io.middlepoint.morestuff.shared.domain.repository.ActivityRepository

interface LogActivityUseCase {
  suspend operator fun invoke(sentence: String, data: ActivityData)
}

class LogActivityUseCaseImpl(
  private val activityRepository: ActivityRepository
) : LogActivityUseCase {

  override suspend fun invoke(sentence: String, data: ActivityData) {
    activityRepository.insert(sentence, data)
  }
}