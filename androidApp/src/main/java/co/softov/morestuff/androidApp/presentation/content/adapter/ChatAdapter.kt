package co.softov.morestuff.androidApp.presentation.content.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.AsyncPagedListDiffer
import androidx.paging.PagedList
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import co.softov.morestuff.android.R
import co.softov.morestuff.android.databinding.AppChatConfirmationItemBinding
import co.softov.morestuff.android.databinding.AppChatReminderItemBinding
import co.softov.morestuff.android.databinding.UserChatItemBinding
import co.softov.morestuff.androidApp.data.utils.toEpochMilliseconds
import co.softov.morestuff.androidApp.domain.enums.ContentType
import co.softov.morestuff.androidApp.domain.model.Message
import java.text.SimpleDateFormat
import java.util.*

class ChatAdapter(context: Context) : RecyclerView.Adapter<BaseViewHolder>() {

    private val layoutInflater: LayoutInflater = LayoutInflater.from(context)

    private val differ: AsyncPagedListDiffer<Message> =
        AsyncPagedListDiffer(this, messageDiffCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return when (ContentType.values()[viewType]) {

            ContentType.USER_NEW_TASK -> {
                UserViewHolder(
                    layoutInflater.inflate(R.layout.user_chat_item, parent, false)
                )
            }

            ContentType.CONFIRM_NEW_TASK -> AppTaskConfirmationViewHolder(
                layoutInflater.inflate(R.layout.app_chat_confirmation_item, parent, false)
            )
            ContentType.TASK_REMINDER -> AppReminderViewHolder(
                layoutInflater.inflate(R.layout.app_chat_reminder_item, parent, false)
            )
        }
    }

    override fun getItemCount(): Int {
        return differ.itemCount
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        differ.getItem(position)?.let { holder.bind(it) }
    }

    override fun getItemId(position: Int): Long {
        return differ.getItem(position)?.id ?: 0
    }

    override fun getItemViewType(position: Int): Int {
        return differ.getItem(position)?.contentType?.ordinal ?: 0
    }

    fun submitList(data: PagedList<Message>) {
        differ.submitList(data)
    }
}

abstract class BaseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    abstract fun bind(item: Message)

    protected fun getStartTimeText(time: String): String =
        SimpleDateFormat.getTimeInstance(SimpleDateFormat.SHORT)
            .format(Date(time.toEpochMilliseconds))
}

class UserViewHolder(view: View) : BaseViewHolder(view) {

    override fun bind(item: Message) {
        UserChatItemBinding.bind(itemView).apply {
            messageUserTaskTitle.text = item.content
            messageUserTaskTime.text = getStartTimeText(item.createTime)
        }
    }
}

class AppReminderViewHolder(view: View) : BaseViewHolder(view) {

    override fun bind(item: Message) {
        AppChatReminderItemBinding.bind(itemView).apply {
            messageAppTaskTitle.text = item.content
            messageAppTaskTime.text = getStartTimeText(item.createTime)
            layoutAppMessageReply.isVisible = item.replyType != null
            textAppMessageReply.text = item.replyContent ?: ""
        }
    }
}

class AppTaskConfirmationViewHolder(view: View) : BaseViewHolder(view) {

    override fun bind(item: Message) {
        AppChatConfirmationItemBinding.bind(itemView).apply {
            messageAppTaskTitle.text = item.content
            messageAppTaskTime.text = getStartTimeText(item.createTime)
        }
    }
}

private val messageDiffCallback =
    object : DiffUtil.ItemCallback<Message>() {

        override fun areItemsTheSame(
            oldItem: Message,
            newItem: Message
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: Message,
            newItem: Message
        ): Boolean {
            return oldItem.content == newItem.content && oldItem.replyType == newItem.replyType
        }
    }