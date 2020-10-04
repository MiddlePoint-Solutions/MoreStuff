package co.softov.morestuff.androidApp.presentation.dashboard.options

import android.text.Html
import android.text.Spanned
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import co.softov.morestuff.android.R
import co.softov.morestuff.androidApp.app.presentation.extension.setOnDebouncedClickListener
import co.softov.morestuff.androidApp.presentation.dashboard.options.TimeOption.AFTER_NOON
import co.softov.morestuff.androidApp.presentation.dashboard.options.TimeOption.CUSTOM
import co.softov.morestuff.androidApp.presentation.dashboard.options.TimeOption.EVENING
import co.softov.morestuff.androidApp.presentation.dashboard.options.TimeOption.MORNING
import co.softov.morestuff.androidApp.presentation.dashboard.options.TimeOption.NIGHT
import co.softov.morestuff.androidApp.presentation.dashboard.options.TimeOption.NOON
import co.softov.morestuff.androidApp.presentation.dashboard.options.TimeOption.ONE_HOUR
import co.softov.morestuff.androidApp.presentation.dashboard.options.TimeOption.SOME_DAY
import java.text.SimpleDateFormat
import java.util.Date

class
TimeOptionsAdapter(
    private val itemClickAction: (Long) -> Unit
) : ListAdapter<OptionListItemViewModel, TimeOptionsAdapter.ViewHolder>(TaskDiffCallback()) {

    private var currentOptionId: Long = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.option_button, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun getItemId(position: Int): Long {
        return getItem(position).id
    }

    fun setCurrentOption(optionId: Long) {
        currentOptionId = optionId
        notifyDataSetChanged()
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        fun bind(item: OptionListItemViewModel) {
            (itemView.findViewById(R.id.buttonTimeOption) as MaterialButton).apply {
                text = item.getOptionTitle()
                isSelected = item.id == currentOptionId

                setOnDebouncedClickListener {
                    itemClickAction(item.id)
                }
            }
        }

        @Suppress("DEPRECATION")
        private fun OptionListItemViewModel.getOptionTitle(): Spanned {
            val res = itemView.resources
            val (title: String, value: String) = when (option) {
                ONE_HOUR -> res.getString(R.string.time_option_1_hour) to formatTime()
                MORNING -> res.getString(R.string.time_option_morning) to formatTime()
                NOON -> res.getString(R.string.time_option_noon) to formatTime()
                AFTER_NOON -> res.getString(R.string.time_option_after_noon) to formatTime()
                EVENING -> res.getString(R.string.time_option_evening) to formatTime()
                NIGHT -> res.getString(R.string.time_option_night) to formatTime()
                CUSTOM -> {
                    val timeString = if (time != 0L) formatTime() else "Set"
                    res.getString(R.string.time_option_custom) to timeString
                }
                SOME_DAY -> res.getString(R.string.time_option_later) to "Later"
            }

            return Html.fromHtml("${title}<br/><small>${value}</small>")
        }

        private fun OptionListItemViewModel.formatTime() =
            SimpleDateFormat.getTimeInstance(SimpleDateFormat.SHORT).format(Date(time))
    }
}

class TaskDiffCallback : DiffUtil.ItemCallback<OptionListItemViewModel>() {

    override fun areItemsTheSame(
        oldItem: OptionListItemViewModel,
        newItem: OptionListItemViewModel
    ): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(
        oldItem: OptionListItemViewModel,
        newItem: OptionListItemViewModel
    ): Boolean {
        return oldItem.time == newItem.time
    }
}