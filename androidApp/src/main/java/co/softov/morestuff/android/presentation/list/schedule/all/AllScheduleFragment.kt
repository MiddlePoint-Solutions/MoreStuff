package co.softov.morestuff.android.presentation.list.schedule.all

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import co.softov.morestuff.android.presentation.list.schedule.TaskViewHolder
import co.softov.morestuff.android.presentation.theme.MoreStuffTheme
import org.koin.androidx.viewmodel.ext.android.getViewModel

class AllScheduleFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(DisposeOnViewTreeLifecycleDestroyed)
            setContent {

                val viewModel = getViewModel<AllScheduleViewModel>()
                val tasks by viewModel.uiState.collectAsState()

                MoreStuffTheme {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(8.dp)
                    ) {
                        items(tasks.data) { task ->
                            TaskViewHolder(task)
                        }
                    }
                }
            }
        }
    }


    /*fun rescheduleTaskOneHour(taskId: Long) {
        viewModel.rescheduleTaskOneHour(taskId)
    }

    fun rescheduleTaskTomorrow(taskId: Long) {
        viewModel.rescheduleTaskTomorrow(taskId)
    }

    fun rescheduleTaskLater(taskId: Long) {
        viewModel.rescheduleTaskLater(taskId)
    }

    fun rescheduleTaskComplete(taskId: Long) {
        viewModel.rescheduleTaskComplete(taskId)
    }*/
}

