package co.softov.morestuff.androidApp.presentation.list.schedule.today

import android.os.Bundle
import android.view.View
import co.softov.morestuff.androidApp.app.presentation.extension.observe
import co.softov.morestuff.androidApp.presentation.list.BaseListFragment
import co.softov.morestuff.androidApp.presentation.list.schedule.ScheduleListAdapter
import co.softov.morestuff.androidApp.presentation.list.schedule.ScheduleListViewState
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class TodayScheduleFragment : BaseListFragment() {

    private val viewModel: TodayScheduleViewModel by viewModel()
    private val adapter: ScheduleListAdapter by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        observe(viewModel.stateLiveData, ::onStateChange)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.taskList.apply {
            registerForContextMenu(this)
            this.adapter = adapter
        }

        viewModel.loadData()
    }

    private fun onStateChange(state: ScheduleListViewState) {
        adapter.submitList(state.data)
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