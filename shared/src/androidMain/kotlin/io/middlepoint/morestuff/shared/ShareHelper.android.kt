package io.middlepoint.morestuff.shared

import android.content.Context
import android.content.Intent

class ShareHelperImpl(
    private val context: Context,
) : ShareHelper {

    override fun shareMessage(content: String) {
        val intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, content)
            type = "text/plain"
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        val chooserIntent = Intent.createChooser(intent, "Share Message").apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }

        context.startActivity(chooserIntent)
    }
}