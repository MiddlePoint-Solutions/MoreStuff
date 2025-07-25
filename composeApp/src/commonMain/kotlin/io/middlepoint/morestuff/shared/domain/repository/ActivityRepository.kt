package io.middlepoint.morestuff.shared.domain.repository

import io.middlepoint.morestuff.shared.domain.model.Activity

interface ActivityRepository {

    suspend fun insert(activity: Activity)
}
