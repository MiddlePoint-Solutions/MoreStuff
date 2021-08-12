package co.softov.morestuff.android.presentation.list.tasks.complete

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.SimpleItemAnimator
import co.softov.morestuff.android.app.presentation.extension.observe
import co.softov.morestuff.android.presentation.list.BaseListFragment
import co.softov.morestuff.android.presentation.list.tasks.TaskListAdapter
import co.softov.morestuff.android.presentation.list.tasks.TaskListViewState
import org.koin.androidx.viewmodel.ext.android.viewModel

class CompleteTasksFragment : BaseListFragment() {

    private val viewModel: CompleteTasksViewModel by viewModel()
    private val taskListAdapter: TaskListAdapter = TaskListAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        observe(viewModel.uiState, ::onStateChange)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.taskList.apply {
            registerForContextMenu(this)
            adapter = taskListAdapter
            (itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
            (itemAnimator as SimpleItemAnimator).moveDuration = 80
        }
        viewModel.loadData()
    }

    private fun onStateChange(state: TaskListViewState) {
        taskListAdapter.submitList(state.data)
    }

    override fun rescheduleTaskOneHour(taskId: Long) {
        viewModel.rescheduleTaskOneHour(taskId)
    }

    override fun rescheduleTaskTomorrow(taskId: Long) {
        viewModel.rescheduleTaskTomorrow(taskId)
    }

    override fun rescheduleTaskLater(taskId: Long) {
        viewModel.rescheduleTaskLater(taskId)
    }

    override fun rescheduleTaskComplete(taskId: Long) {
        viewModel.rescheduleTaskComplete(taskId)
    }
}