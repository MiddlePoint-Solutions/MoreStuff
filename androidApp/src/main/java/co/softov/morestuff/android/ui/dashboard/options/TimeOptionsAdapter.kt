package co.softov.morestuff.android.ui.dashboard.options

import android.text.Html
import android.text.Html.FROM_HTML_MODE_LEGACY
import android.text.Spanned
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import co.softov.morestuff.android.R
import co.softov.morestuff.android.app.presentation.extension.setOnDebouncedClickListener
import com.google.android.material.button.MaterialButton
import java.text.SimpleDateFormat
import java.util.*

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


        private fun OptionListItemViewModel.getOptionTitle(): Spanned {
            val res = itemView.resources

            // TODO: Consider using this for displaying both title and time in the same button
            return Html.fromHtml("${"Title"}<br/><small>${"00:00"}</small>", FROM_HTML_MODE_LEGACY)
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