package io.middlepoint.morestuff.shared.data.repository

import io.middlepoint.morestuff.db.Activities
import io.middlepoint.morestuff.db.StuffDb
import io.middlepoint.morestuff.shared.data.utils.generate
import io.middlepoint.morestuff.shared.domain.model.ActivityData
import io.middlepoint.morestuff.shared.domain.model.Uuid
import io.middlepoint.morestuff.shared.domain.repository.ActivityRepository
import io.middlepoint.morestuff.shared.domain.service.TimeManager
import kotlinx.serialization.json.Json

class ActivityRepositoryImpl(
  private val database: StuffDb,
  private val timeManager: TimeManager,
) : ActivityRepository {

  override suspend fun insert(activity: String, data: ActivityData) {
    database.activitiesQueries.insert(
      Activities(
        id = Uuid.generate(),
        sentence = activity,
        created_at = timeManager.nowUtcInstant,
        data_ = Json.encodeToString(data)
      )
    )
  }
}
