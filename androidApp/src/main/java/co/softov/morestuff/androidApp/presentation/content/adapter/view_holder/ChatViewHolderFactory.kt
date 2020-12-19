package co.softov.morestuff.androidApp.presentation.content.adapter.view_holder

import android.view.ViewGroup
import co.softov.morestuff.android.R
import co.softov.morestuff.androidApp.app.presentation.extension.inflateView
import co.softov.morestuff.androidApp.domain.enums.ContentType
import co.softov.morestuff.androidApp.presentation.content.ChatResponseActions
import com.google.android.material.textview.MaterialTextView

class ChatViewHolderFactory(
    private val actions: ChatResponseActions
) {

    fun create(type: ContentType, parent: ViewGroup): ChatViewHolder {
        return when (type) {
            ContentType.USER_NEW_TASK -> UserViewHolder(
                parent.inflateView(R.layout.user_chat_item)
            )
            ContentType.CONFIRM_NEW_TASK -> AppTaskConfirmationViewHolder(
                parent.inflateView(R.layout.app_chat_confirmation_item)
            )
            ContentType.TASK_REMINDER -> AppReminderViewHolder(
                parent.inflateView(R.layout.app_chat_reminder_item), actions
            )
            else -> {
                MaterialTextView(parent.context).run {
                    text = context.getString(R.string.chat_item_invalid)
                    InvalidViewHolder(this)
                }
            }
        }
    }

}