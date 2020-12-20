package co.softov.morestuff.androidApp.presentation.content.adapter.view_holder

import android.view.View
import co.softov.morestuff.android.databinding.ChatTaskConfirmationItemBinding
import co.softov.morestuff.androidApp.domain.model.Message

class TaskConfirmationViewHolder(view: View) : ChatViewHolder(view) {

    override fun bind(item: Message) {
        ChatTaskConfirmationItemBinding.bind(itemView).apply {
            chatTaskConfirmTitle.text = item.content
            chatItemTime.text = getStartTimeText(item.createTime)

        }
    }
}