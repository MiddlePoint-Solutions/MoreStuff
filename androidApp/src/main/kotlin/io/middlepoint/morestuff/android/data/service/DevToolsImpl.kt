package io.middlepoint.morestuff.android.data.service

import android.net.Uri
import com.russhwolf.settings.Settings
import io.middlepoint.morestuff.android.data.Constants.KEY_DEBUG_MESSAGES
import io.middlepoint.morestuff.shared.domain.DevTools
import io.middlepoint.morestuff.shared.domain.service.DataMigrationHelper
import io.middlepoint.morestuff.shared.domain.service.Notifier
import timber.log.Timber

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

    override fun testReviewNotification() {
        notifier.showReviewNotification()
    }

    override suspend fun exportData(uri: Uri) {
        dataMigration.exportDatabase(uri).also {
            Timber.d("exportData, finished: $it")
        }
    }

    override suspend fun importData(uri: Uri) {
        dataMigration.importDatabase(uri).also {
            Timber.d("importData, finished: $it")
        }
    }
}

