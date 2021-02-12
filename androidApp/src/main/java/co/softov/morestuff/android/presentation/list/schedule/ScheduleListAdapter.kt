package co.softov.morestuff.android.presentation.list.schedule

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import co.softov.morestuff.android.R
import co.softov.morestuff.android.databinding.ScheduleListItemExpandableBinding
import co.softov.morestuff.android.app.presentation.extension.setOnDebouncedClickListener
import co.softov.morestuff.android.presentation.list.schedule.model.ScheduleListItemViewModel

class
ScheduleListAdapter :
    ListAdapter<ScheduleListItemViewModel, ScheduleListAdapter.ViewHolder>(ScheduleDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ViewHolder(
            layoutInflater.inflate(R.layout.schedule_list_item_expandable, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun getItemId(position: Int): Long {
        return getItem(position).taskId
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        fun bind(item: ScheduleListItemViewModel) {
            ScheduleListItemExpandableBinding.bind(itemView).apply {
                itemView.isLongClickable = true

                taskScheduleTitleText.text = item.taskTitle
                textScheduleItemScheduleDate.text =
                    itemView.resources.getString(
                        R.string.text_scheduled_time_placeholder,
                        item.scheduleTime
                    )

                layoutItemExpandedInfo.apply {
                    isVisible = item.expanded
                }

                itemView.setOnDebouncedClickListener {
                    item.expanded = item.expanded.not()
                    notifyItemChanged(adapterPosition)
                }

            }
        }
    }

    fun showMenu(anchor: View) {
        val popup = PopupMenu(anchor.context, anchor)
        popup.apply {
            menu.add(0, R.id.reschedule_tasks_1_hour, 0, "1 - Hour")
            menu.add(0, R.id.reschedule_tasks_tomorrow, 0, "Tomorrow")
            menu.add(0, R.id.reschedule_tasks_later, 0, "Later")
            menu.add(0, R.id.reschedule_tasks_complete, 0, "Complete")
        }

        popup.show()
        popup.setOnMenuItemClickListener {

            true
        }
    }
}

class ScheduleDiffCallback : DiffUtil.ItemCallback<ScheduleListItemViewModel>() {

    override fun areItemsTheSame(
        oldItem: ScheduleListItemViewModel,
        newItem: ScheduleListItemViewModel
    ): Boolean {
        return oldItem.taskId == newItem.taskId || oldItem.taskTitle == newItem.taskTitle
    }

    override fun areContentsTheSame(
        oldItem: ScheduleListItemViewModel,
        newItem: ScheduleListItemViewModel
    ): Boolean {
        return oldItem.taskTitle == newItem.taskTitle && oldItem.scheduleTime == newItem.scheduleTime
    }
}

