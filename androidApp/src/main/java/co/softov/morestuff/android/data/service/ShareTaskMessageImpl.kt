package co.softov.morestuff.android.data.service

import android.content.Context
import android.content.Intent
import co.softov.morestuff.android.domain.model.Message
import co.softov.morestuff.android.domain.service.ShareTaskMessage

class ShareTaskMessageImpl(
    private val context: Context,
) : ShareTaskMessage {

        override fun shareMessage(message: Message) {
            val intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, message.content)
                type = "text/plain"
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            val chooserIntent = Intent.createChooser(intent, "Share Message").apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }

            context.startActivity(chooserIntent)
        }
}
