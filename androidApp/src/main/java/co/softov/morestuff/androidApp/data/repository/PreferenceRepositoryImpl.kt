package co.softov.morestuff.androidApp.data.repository

import android.content.SharedPreferences
import co.softov.morestuff.androidApp.domain.repository.PreferenceRepository
import java.util.concurrent.TimeUnit

class PreferenceRepositoryImpl(
    private val sharedPreferences: SharedPreferences
) : PreferenceRepository {

    override suspend fun getTodayReminderDelay(): Long {
        val minutes = sharedPreferences.getInt("reminder_today_delay", 60).toLong()
        return TimeUnit.MINUTES.toMillis(minutes)
    }
}