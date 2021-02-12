package co.softov.morestuff.android.domain.repository

interface PreferenceRepository {

    suspend fun getTodayReminderDelay() : Long

}