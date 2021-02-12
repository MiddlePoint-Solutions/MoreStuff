package co.softov.morestuff.android.app

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import co.softov.morestuff.android.domain.Debug

class Debugger(context: Context) : Debug {

    private val prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

    private val keyDebugReminders = "debug_reminder"
    private val keyTodayDebugTime = "debug_reminder_today_offset"
    private val keyKeepScreenOn = "debug_keep_screen_on"

    private var _debugReminders: Boolean = false
    private var _todayDebugTime: Int = 1 // Minute

    private val preferenceChangeListener =
        SharedPreferences.OnSharedPreferenceChangeListener { sharedPreferences, key ->
            when (key) {
                "debug_reminder" -> _debugReminders = sharedPreferences.getBoolean(key, false)
                "debug_reminder_today_offset" -> _todayDebugTime = sharedPreferences.getInt(key, 1)
            }
        }

    override val debugReminders: Boolean
        get() = _debugReminders

    override val todayDebugTime: Int
        get() = _todayDebugTime

    init {
        prefs.registerOnSharedPreferenceChangeListener(preferenceChangeListener)
        prefs.edit().putBoolean(keyKeepScreenOn, false).apply()
        _debugReminders = prefs.getBoolean(keyDebugReminders, false)
        _todayDebugTime = prefs.getInt(keyTodayDebugTime, 1)
    }
}