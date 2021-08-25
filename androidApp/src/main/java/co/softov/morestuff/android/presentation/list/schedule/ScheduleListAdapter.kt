package co.softov.morestuff.android.presentation.list.schedule

import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.recyclerview.widget.DiffUtil
import co.softov.morestuff.android.R
import co.softov.morestuff.android.app.ui.ComposeListAdapter
import co.softov.morestuff.android.app.ui.ComposeViewHolder
import co.softov.morestuff.android.presentation.list.schedule.model.ScheduleListItemViewModel

class
ScheduleListAdapter :
    ComposeListAdapter<ScheduleListItemViewModel, ScheduleListAdapter.ScheduleListViewHolder>(
        ScheduleDiffCallback()
    ) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScheduleListViewHolder {
        return ScheduleListViewHolder(ComposeView(parent.context))
    }

    override fun onBindViewHolder(holder: ScheduleListViewHolder, position: Int) {
        val item = getItem(position)
        holder.bindViewHolder(item)
    }

    override fun getItemId(position: Int): Long {
        return getItem(position).taskId
    }

    inner class ScheduleListViewHolder(
        composeView: ComposeView
    ) : ComposeViewHolder<ScheduleListItemViewModel>(composeView) {

        init {
            composeView.isLongClickable = true
        }

        @Composable
        override fun ViewHolder(input: ScheduleListItemViewModel) {
            MaterialTheme {
                TaskViewHolder(input)
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

