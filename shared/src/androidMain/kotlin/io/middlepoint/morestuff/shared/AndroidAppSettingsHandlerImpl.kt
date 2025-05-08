
package io.middlepoint.morestuff.shared

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import co.touchlab.kermit.Logger


class AndroidAppSettingsHandlerImpl(
    private val context: Context,
    private val logger: Logger
) : AppSettingsHandler {
    
    override fun openAppSettings() {
        try {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                val uri = Uri.fromParts("package", context.packageName, null)
                data = uri
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
            logger.d("App settings opened successfully")
        } catch (e: Exception) {
            logger.e("Error opening app settings", e)
        }
    }
}