package co.softov.morestuff.android.app

import co.softov.morestuff.android.domain.DevTools
import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.SettingsListener

class DevToolsImpl(private val observableSettings: ObservableSettings) : DevTools {

    private val keyDebugReminders = "debug_reminder"
    private val keyTodayDebugTime = "debug_reminder_today_offset"
    private val keyKeepScreenOn = "debug_keep_screen_on"

    private var _debugReminders: Boolean = false
    private var _todayDebugTime: Int = 1 // Minute
    private var _keepScreenOn: Boolean = false
    private val debugRemindersListener: SettingsListener =
        observableSettings.addBooleanListener(keyDebugReminders, false) { value ->
            _debugReminders = value
        }
    private val todayDebugTimeListener: SettingsListener =
        observableSettings.addIntListener(keyTodayDebugTime, 1) { value ->
            _todayDebugTime = value
        }

    init {

        _debugReminders = observableSettings.getBoolean(keyDebugReminders, defaultValue = true)
        _todayDebugTime = observableSettings.getInt(keyTodayDebugTime, defaultValue = 1)
        _keepScreenOn = observableSettings.getBoolean(keyKeepScreenOn, defaultValue = false)
    }

    override var debugReminders: Boolean
        get() = _debugReminders
        set(value) {
            observableSettings.putBoolean(keyDebugReminders, value)
            _debugReminders = value
        }

    override var todayDebugTime: Int
        get() = _todayDebugTime
        set(value) {
            observableSettings.putInt(keyTodayDebugTime, value)
            _todayDebugTime = value
        }

    override var keepScreenOn: Boolean
        get() = _keepScreenOn
        set(value) {
            observableSettings.putBoolean(keyKeepScreenOn, value)
            _keepScreenOn = value
        }

    fun clearListeners() {
        debugRemindersListener.deactivate()
        todayDebugTimeListener.deactivate()
    }
}

