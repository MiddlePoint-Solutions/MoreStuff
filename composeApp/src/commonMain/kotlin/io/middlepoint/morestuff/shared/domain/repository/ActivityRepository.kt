package io.middlepoint.morestuff.shared.domain.repository

import io.middlepoint.morestuff.shared.domain.model.ActivityData

interface ActivityRepository {

    suspend fun insert(activity: String, data: ActivityData)
}
