package co.softov.morestuff.androidApp.presentation.content.adapter.view_holder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import co.softov.morestuff.androidApp.data.utils.toEpochMilliseconds
import co.softov.morestuff.androidApp.domain.model.Message
import java.text.SimpleDateFormat
import java.util.*

abstract class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    abstract fun bind(item: Message)

    protected fun getStartTimeText(time: String): String =
        SimpleDateFormat.getTimeInstance(SimpleDateFormat.SHORT)
            .format(Date(time.toEpochMilliseconds))
}