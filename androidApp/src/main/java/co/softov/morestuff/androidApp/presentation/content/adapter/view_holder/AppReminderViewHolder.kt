package co.softov.morestuff.androidApp.presentation.content.adapter.view_holder

import android.view.View
import androidx.core.view.isVisible
import co.softov.morestuff.android.databinding.AppChatReminderItemBinding
import co.softov.morestuff.androidApp.app.presentation.extension.setOnDebouncedClickListener
import co.softov.morestuff.androidApp.domain.enums.ReplyType
import co.softov.morestuff.androidApp.domain.model.Message
import co.softov.morestuff.androidApp.presentation.content.ChatResponseActions

class AppReminderViewHolder(
    view: View,
    private val actions: ChatResponseActions
) : ChatViewHolder(view) {

    override fun bind(item: Message) {
        AppChatReminderItemBinding.bind(itemView).apply {
            messageAppTaskTitle.text = item.content
            messageAppTaskTime.text = getStartTimeText(item.createTime)
            setReply(item)
        }
    }

    private fun AppChatReminderItemBinding.setReply(item: Message) {

        val showReplyActions = item.replyType == null
        layoutAppMessageActions.isVisible = showReplyActions

        textAppMessageReply.isVisible = showReplyActions.not()
        textAppMessageReply.text = item.replyContent ?: ""

        layoutAppMessageActionsSnooze.setOnDebouncedClickListener {
            actions.scheduleResponse(item.scheduleId, ReplyType.SNOOZE)
        }

        layoutAppMessageActionsTomorrow.setOnDebouncedClickListener {
            actions.scheduleResponse(item.scheduleId, ReplyType.TOMORROW)
        }

        layoutAppMessageActionsDone.setOnDebouncedClickListener {
            actions.scheduleResponse(item.scheduleId, ReplyType.DONE)
        }
    }
}