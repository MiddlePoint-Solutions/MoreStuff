package co.softov.morestuff.android.presentation.content.adapter.view_holder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import co.softov.morestuff.android.data.utils.toEpochMilliseconds
import co.softov.morestuff.android.domain.model.Message
import java.text.SimpleDateFormat
import java.util.*

abstract class BaseChatItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    abstract fun bind(item: Message)

    protected fun getStartTimeText(time: String): String =
        SimpleDateFormat.getTimeInstance(SimpleDateFormat.SHORT)
            .format(Date(time.toEpochMilliseconds))
}