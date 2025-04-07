package io.middlepoint.morestuff.shared.data.service

import co.touchlab.kermit.Logger
import com.russhwolf.settings.Settings
import io.middlepoint.morestuff.android.data.Constants.KEY_DEBUG_MESSAGES
import io.middlepoint.morestuff.android.data.Constants.KEY_DEV_SETTINGS
import io.middlepoint.morestuff.shared.domain.DevTools
import io.middlepoint.morestuff.shared.DataMigrationHelper
import io.middlepoint.morestuff.shared.domain.service.Notifier

class DevToolsImpl(
    private val settings: Settings,
    private val notifier: Notifier,
    private val dataMigration: DataMigrationHelper,
) : DevTools {

    override var showDebugMessages: Boolean
        get() = settings.getBoolean(KEY_DEBUG_MESSAGES, false)
        set(value) {
            settings.putBoolean(KEY_DEBUG_MESSAGES, value)
        }
    override var showDevSettings: Boolean
        get() = settings.getBoolean(KEY_DEV_SETTINGS, false)
        set(value) {
            settings.putBoolean(KEY_DEV_SETTINGS, value)
        }

    override fun testReviewNotification() {
        notifier.showReviewNotification()
    }

    override suspend fun exportData(uri: String) {
        dataMigration.exportDatabase(uri).also {
            Logger.d("exportData, finished: $it")
        }
    }

    override suspend fun importData(uri: String) {
        dataMigration.importDatabase(uri).also {
            Logger.d("importData, finished: $it")
        }
    }
}

