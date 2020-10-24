package co.softov.morestuff.androidApp.presentation.list.schedule.tomorrow

import android.os.Bundle
import android.view.View
import co.softov.morestuff.androidApp.app.presentation.extension.observe
import co.softov.morestuff.androidApp.presentation.list.BaseListFragment
import co.softov.morestuff.androidApp.presentation.list.schedule.ScheduleListAdapter
import co.softov.morestuff.androidApp.presentation.list.schedule.ScheduleListViewState
import org.koin.androidx.viewmodel.ext.android.viewModel

class TomorrowScheduleFragment : BaseListFragment() {

    private val viewModel: TomorrowScheduleViewModel by viewModel()
    private val scheduleAdapter: ScheduleListAdapter = ScheduleListAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        observe(viewModel.stateLiveData, ::onStateChange)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.taskList.apply {
            registerForContextMenu(this)
            adapter = scheduleAdapter
        }
        viewModel.loadData()
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