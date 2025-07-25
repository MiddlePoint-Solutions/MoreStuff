package io.middlepoint.morestuff.shared.data.repository

import io.middlepoint.morestuff.db.Activities
import io.middlepoint.morestuff.db.StuffDb
import io.middlepoint.morestuff.shared.domain.model.Activity
import io.middlepoint.morestuff.shared.domain.repository.ActivityRepository

class ActivityRepositoryImpl(
  private val database: StuffDb
) : ActivityRepository {

  override suspend fun insert(activity: Activity) {
    database.activitiesQueries.insert(
      Activities(
        id = activity.id,
        sentence = activity.sentence,
        created_at = activity.createdAt,
        data_ = activity.data
      )
    )
  }
}
