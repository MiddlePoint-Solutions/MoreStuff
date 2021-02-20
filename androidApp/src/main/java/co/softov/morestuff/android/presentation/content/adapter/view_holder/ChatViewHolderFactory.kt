package co.softov.morestuff.android.presentation.content.adapter.view_holder

import android.view.ViewGroup
import co.softov.morestuff.android.R
import co.softov.morestuff.android.app.presentation.extension.inflateView
import co.softov.morestuff.android.domain.enums.ContentType
import co.softov.morestuff.android.domain.enums.ContentType.*
import co.softov.morestuff.android.presentation.content.ChatActions
import com.google.android.material.textview.MaterialTextView

class ChatViewHolderFactory(
    private val actions: ChatActions
) {

    fun create(type: ContentType, parent: ViewGroup): BaseChatItemViewHolder {
        return when (type) {
            USER_NEW_TASK -> ChatItemUserViewHolder(
                parent.inflateView(R.layout.chat_item_user)
            )
            CONFIRM_NEW_TASK -> ChatItemConfirmationViewHolder(
                parent.inflateView(R.layout.chat_item_confirmation)
            )
            TASK_REMINDER -> ChatItemReminderViewHolder(
                parent.inflateView(R.layout.chat_item_reminder), actions
            )
            else -> MaterialTextView(parent.context).run {
                text = context.getString(R.string.chat_item_invalid)
                ChatItemInvalidViewHolder(this)
            }
        }
    }

}