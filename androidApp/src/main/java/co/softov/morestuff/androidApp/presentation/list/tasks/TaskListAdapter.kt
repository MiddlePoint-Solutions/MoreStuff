package co.softov.morestuff.androidApp.presentation.list.tasks

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.task_list_item_expandable.view.*
import co.softov.morestuff.android.R
import co.softov.morestuff.androidApp.app.presentation.extension.setOnDebouncedClickListener
import co.softov.morestuff.androidApp.presentation.list.tasks.model.TaskListItemViewModel

class
TaskListAdapter :
    ListAdapter<TaskListItemViewModel, TaskListAdapter.ViewHolder>(TaskDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ViewHolder(
            layoutInflater.inflate(R.layout.task_list_item_expandable, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun getItemId(position: Int): Long {
        return getItem(position).id
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        fun bind(task: TaskListItemViewModel) {
            itemView.apply {
                taskItemTitleText.text = task.title
                isLongClickable = true

                layoutItemExpandedInfo.apply {
                    textTaskItemCreateDate.text =
                        resources.getString(R.string.text_created_time_placeholder, task.createTime)
                    textListItemCompleteDate.text =
                        resources.getString(R.string.text_completed_time_placeholder, task.completeTime)

                    isVisible = task.expanded
                    textListItemCompleteDate.isVisible = task.completeTime.isNotEmpty()
                }

                setOnDebouncedClickListener {
                    task.expanded = task.expanded.not()
                    notifyItemChanged(adapterPosition)
                }
            }
        }
    }
}

class TaskDiffCallback : DiffUtil.ItemCallback<TaskListItemViewModel>() {

    override fun areItemsTheSame(
        oldItem: TaskListItemViewModel,
        newItem: TaskListItemViewModel
    ): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(
        oldItem: TaskListItemViewModel,
        newItem: TaskListItemViewModel
    ): Boolean {
        return oldItem.title == newItem.title
    }
}