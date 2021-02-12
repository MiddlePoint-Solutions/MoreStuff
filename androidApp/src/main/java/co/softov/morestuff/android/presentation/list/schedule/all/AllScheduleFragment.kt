package co.softov.morestuff.android.presentation.list.schedule.all

import android.os.Bundle
import android.view.View
import co.softov.morestuff.android.app.presentation.extension.observe
import co.softov.morestuff.android.presentation.list.BaseListFragment
import co.softov.morestuff.android.presentation.list.schedule.ScheduleListAdapter
import co.softov.morestuff.android.presentation.list.schedule.ScheduleListViewState
import org.koin.androidx.viewmodel.ext.android.viewModel

class AllScheduleFragment : BaseListFragment() {

    private val viewModel: AllScheduleViewModel by viewModel()
    private val scheduleAdapter: ScheduleListAdapter = ScheduleListAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.loadData()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.taskList.apply {
            registerForContextMenu(this)
            adapter = scheduleAdapter
        }
        observe(viewModel.stateLiveData, ::onStateChange)
    }

    override fun onResume() {
        super.onResume()
        // registerForContextMenu(task_list)
    }

    override fun onPause() {
        super.onPause()
        // unregisterForContextMenu(task_list)
    }

    private fun onStateChange(state: ScheduleListViewState) {
        scheduleAdapter.submitList(state.data)
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