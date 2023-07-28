package co.softov.morestuff.android.app

import co.softov.morestuff.android.data.Constants.KEY_DEBUG_MESSAGE
import co.softov.morestuff.android.domain.DevTools
import co.softov.morestuff.android.domain.service.Notifier
import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.Settings
import com.russhwolf.settings.SettingsListener

class DevToolsImpl(
    private val observableSettings: ObservableSettings,
    private val settings: Settings,
    private val notifier: Notifier,
) : DevTools {

    private val keyDebugReminders = "debug_reminder"
    private val keyTodayDebugTime = "debug_reminder_today_offset"

    private var _debugReminders: Boolean = false
    private var _todayDebugTime: Int = 1 // Minute

    init {
        _debugReminders = observableSettings.getBoolean(keyDebugReminders, defaultValue = false)
        _todayDebugTime = observableSettings.getInt(keyTodayDebugTime, defaultValue = 1)
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

    override fun getDebugMessageSwitchState(): Boolean {
        return settings.getBoolean(KEY_DEBUG_MESSAGE, false)
    }

    override fun testReviewNotification() {
        notifier.showReviewNotification()
    }

}

