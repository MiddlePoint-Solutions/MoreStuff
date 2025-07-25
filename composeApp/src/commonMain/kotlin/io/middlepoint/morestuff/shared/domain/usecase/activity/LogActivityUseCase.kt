package io.middlepoint.morestuff.shared.domain.usecase.activity

import io.middlepoint.morestuff.shared.domain.model.Activity
import io.middlepoint.morestuff.shared.domain.repository.ActivityRepository
import kotlinx.datetime.Clock

interface LogActivityUseCase {
  suspend operator fun invoke(sentence: String, data: String)
}

class LogActivityUseCaseImpl(
  private val activityRepository: ActivityRepository
) : LogActivityUseCase {

  override suspend fun invoke(sentence: String, data: String) {
    activityRepository.insert(
      Activity(
        sentence = sentence,
        data = data
      )
    )
  }
}