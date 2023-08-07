package co.softov.morestuff.android.app

import co.softov.morestuff.android.data.Constants.KEY_DEBUG_MESSAGES
import co.softov.morestuff.android.domain.DevTools
import co.softov.morestuff.android.domain.service.Notifier
import com.russhwolf.settings.Settings

class DevToolsImpl(
    private val settings: Settings,
    private val notifier: Notifier,
) : DevTools {

    override var showDebugMessages: Boolean
        get() = settings.getBoolean(KEY_DEBUG_MESSAGES, false)
        set(value) {
            settings.putBoolean(KEY_DEBUG_MESSAGES, value)
        }

    override fun testReviewNotification() {
        notifier.showReviewNotification()
    }

}

