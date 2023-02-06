package co.softov.morestuff.android.domain.repository

import co.softov.morestuff.android.domain.model.AppSettings

interface UserRepository {

    suspend fun setSnoozeLimit(limit: Int)
    suspend fun getUserSettings(default: AppSettings): AppSettings

}