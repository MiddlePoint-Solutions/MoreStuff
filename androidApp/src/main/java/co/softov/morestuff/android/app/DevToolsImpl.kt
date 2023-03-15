package co.softov.morestuff.android.app

import android.content.SharedPreferences
import co.softov.morestuff.android.domain.DevTools
import com.russhwolf.settings.Settings

class DevToolsImpl(settings: Settings) : DevTools {

    private val keyDebugReminders = "debug_reminder"
    private val keyTodayDebugTime = "debug_reminder_today_offset"
    private val keyKeepScreenOn = "debug_keep_screen_on"

    private var _debugReminders: Boolean = false
    private var _todayDebugTime: Int = 1 // Minute

    private val preferenceChangeListener =
        SharedPreferences.OnSharedPreferenceChangeListener { sharedPreferences, key ->
            when (key) {
                keyDebugReminders -> _debugReminders = settings.getBoolean(keyDebugReminders, false)
                keyTodayDebugTime -> _todayDebugTime = settings.getInt(keyTodayDebugTime, 1)
            }
        }

    override val debugReminders: Boolean
        get() = _debugReminders

    override val todayDebugTime: Int
        get() = _todayDebugTime

    init {
        settings.putBoolean(preferenceChangeListener.toString(), value = false)
        settings.putBoolean(keyKeepScreenOn, value = false)
        _debugReminders = settings.getBoolean(keyDebugReminders, false)
        _todayDebugTime = settings.getInt(keyTodayDebugTime, 1)
    }
}