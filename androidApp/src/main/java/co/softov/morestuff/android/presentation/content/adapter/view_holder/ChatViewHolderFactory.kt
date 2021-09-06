package co.softov.morestuff.android.presentation.content.adapter.view_holder

import android.view.ViewGroup
import co.softov.morestuff.android.domain.enums.ContentType
import co.softov.morestuff.android.domain.enums.ContentType.*
import co.softov.morestuff.android.presentation.content.ChatActions

class ChatViewHolderFactory(
    private val actions: ChatActions
) {

    fun create(type: ContentType, parent: ViewGroup): BaseChatItemViewHolder {
        return when (type) {
            USER_NEW_TASK -> ChatItemUserViewHolder(parent.context)
            CONFIRM_NEW_TASK,
            TASK_REMINDER -> ChatItemReminderViewHolder(parent.context, actions)
        }
    }

}