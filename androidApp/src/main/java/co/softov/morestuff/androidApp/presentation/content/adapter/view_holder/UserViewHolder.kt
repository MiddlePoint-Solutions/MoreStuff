package co.softov.morestuff.androidApp.presentation.content.adapter.view_holder

import android.view.View
import co.softov.morestuff.android.databinding.UserChatItemBinding
import co.softov.morestuff.androidApp.domain.model.Message

class UserViewHolder(view: View) : ChatViewHolder(view) {

    override fun bind(item: Message) {
        UserChatItemBinding.bind(itemView).apply {
            messageUserTaskTitle.text = item.content
            messageUserTaskTime.text = getStartTimeText(item.createTime)
        }
    }
}