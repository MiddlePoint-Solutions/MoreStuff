package co.softov.morestuff.androidApp.presentation.content.adapter.view_holder

import android.view.View
import co.softov.morestuff.android.databinding.ChatItemConfirmationBinding
import co.softov.morestuff.androidApp.domain.model.Message

class ChatItemConfirmationViewHolder(view: View) : BaseChatItemViewHolder(view) {

    override fun bind(item: Message) {
        ChatItemConfirmationBinding.bind(itemView).apply {
            chatTaskConfirmTitle.text = item.content
            chatItemTime.text = getStartTimeText(item.createTime)
        }
    }
}