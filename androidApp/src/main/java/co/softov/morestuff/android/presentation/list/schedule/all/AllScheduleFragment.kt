package co.softov.morestuff.android.presentation.list.schedule.all

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import co.softov.morestuff.android.R
import co.softov.morestuff.android.presentation.list.schedule.model.ScheduleListItemViewModel
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

                MaterialTheme {
                    LazyColumn(contentPadding = PaddingValues(8.dp)) {
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

@Composable
fun TaskViewHolder(task: ScheduleListItemViewModel) {
    Column {
        Text(
            text = task.taskTitle,
            color = Color.White,
            style = MaterialTheme.typography.h6,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.padding(4.dp))
        Text(
            text = stringResource(
                id = R.string.text_scheduled_time_placeholder,
                task.scheduleTime
            ),
            color = Color.White,
            fontSize = 12.sp
        )
    }
}

@Preview
@Composable
fun PreviewTaskHolder() {
    TaskViewHolder(
        task = ScheduleListItemViewModel(
            scheduleId = 0,
            taskId = 0,
            scheduleTime = "Today",
            taskTitle = "Something to do!"
        )
    )
}