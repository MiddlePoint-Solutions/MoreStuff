package co.softov.morestuff.android.presentation.content.adapter.view_holder

import android.view.View
import co.softov.morestuff.android.databinding.ChatItemUserBinding
import co.softov.morestuff.android.domain.model.Message

class ChatItemUserViewHolder(view: View) : BaseChatItemViewHolder(view) {

    override fun bind(item: Message) {
        ChatItemUserBinding.bind(itemView).apply {
            messageUserTaskTitle.text = item.content
            messageUserTaskTime.text = getStartTimeText(item.createTime)
        }
    }
}