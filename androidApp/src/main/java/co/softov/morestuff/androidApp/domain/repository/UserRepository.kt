package co.softov.morestuff.androidApp.domain.repository

import co.softov.morestuff.androidApp.domain.model.UserSettings

interface UserRepository {

    suspend fun setSnoozeLimit(limit: Int)
    suspend fun getUserSettings(): UserSettings

}