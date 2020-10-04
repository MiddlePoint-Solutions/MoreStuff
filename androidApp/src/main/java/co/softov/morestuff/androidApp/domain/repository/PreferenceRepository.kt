package co.softov.morestuff.androidApp.domain.repository

interface PreferenceRepository {

    suspend fun getTodayReminderDelay() : Long

}