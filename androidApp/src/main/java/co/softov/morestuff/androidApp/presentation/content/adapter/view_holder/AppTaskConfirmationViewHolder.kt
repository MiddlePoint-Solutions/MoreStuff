package co.softov.morestuff.androidApp.presentation.content.adapter.view_holder

import android.view.View
import co.softov.morestuff.android.databinding.AppChatConfirmationItemBinding
import co.softov.morestuff.androidApp.domain.model.Message

class AppTaskConfirmationViewHolder(view: View) : ChatViewHolder(view) {

    override fun bind(item: Message) {
        AppChatConfirmationItemBinding.bind(itemView).apply {
            messageAppTaskTitle.text = item.content
            messageAppTaskTime.text = getStartTimeText(item.createTime)
        }
    }
}