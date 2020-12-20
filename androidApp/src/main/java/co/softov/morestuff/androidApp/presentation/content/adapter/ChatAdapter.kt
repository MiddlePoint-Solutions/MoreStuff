package co.softov.morestuff.androidApp.presentation.content.adapter

import android.view.ViewGroup
import androidx.paging.AsyncPagedListDiffer
import androidx.paging.PagedList
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import co.softov.morestuff.androidApp.domain.enums.ContentType
import co.softov.morestuff.androidApp.domain.model.Message
import co.softov.morestuff.androidApp.presentation.content.adapter.view_holder.ChatViewHolder
import co.softov.morestuff.androidApp.presentation.content.adapter.view_holder.ChatViewHolderFactory

class ChatAdapter(
    private val factory: ChatViewHolderFactory
) : RecyclerView.Adapter<ChatViewHolder>() {

    private val differ: AsyncPagedListDiffer<Message> =
        AsyncPagedListDiffer(this, messageDiffCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        return factory.create(ContentType.withValue(viewType), parent)
    }

    override fun getItemCount(): Int {
        return differ.itemCount
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        differ.getItem(position)?.let { holder.bind(it) }
    }

    override fun getItemId(position: Int): Long {
        return differ.getItem(position)?.id ?: 0
    }

    override fun getItemViewType(position: Int): Int {
        return differ.getItem(position)?.contentType?.value ?: 0
    }

    fun submitList(data: PagedList<Message>) {
        differ.submitList(data)
    }
}

private val messageDiffCallback = object : DiffUtil.ItemCallback<Message>() {

    override fun areItemsTheSame(oldItem: Message, newItem: Message): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Message, newItem: Message): Boolean {
        return oldItem.content == newItem.content && oldItem.replyType == newItem.replyType
    }
}