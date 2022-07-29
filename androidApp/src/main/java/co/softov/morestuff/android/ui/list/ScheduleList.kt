package co.softov.morestuff.android.ui.list

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import co.softov.morestuff.android.R
import co.softov.morestuff.android.app.util.LifecycleViewModelStoreOwner
import co.softov.morestuff.android.ui.list.model.PageType
import co.softov.morestuff.android.ui.list.model.ScheduleListItemViewModel
import co.softov.morestuff.android.ui.list.model.TaskListItemViewModel
import org.koin.androidx.compose.getViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun ScheduleList(page: PageType) {
    // TODO: Remove LifecycleViewModelStoreOwner once Koin has a fix for multiple instances of the same ViewModel
    val lifecycleOwner = LocalView.current.findViewTreeLifecycleOwner()
    CompositionLocalProvider(
        LocalViewModelStoreOwner provides ViewModelStoreOwner {
            LifecycleViewModelStoreOwner(lifecycleOwner = lifecycleOwner).viewModelStore
        }
    ) {
        val viewModel: SchedulePageViewModel = getViewModel { parametersOf(page) }
        val state by viewModel.state.collectAsState()

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(8.dp)
        ) {
            when {
                state.schedules.isNotEmpty() -> items(state.schedules) { ScheduleListItem(it) }
                state.tasks.isNotEmpty() -> items(state.tasks) { TaskListItem(it) }
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ScheduleListItem(task: ScheduleListItemViewModel, modifier: Modifier = Modifier) {
    var expanded by remember { mutableStateOf(false) }
    Card(
        elevation = 6.dp,
        onClick = {
            expanded = !expanded
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Column {
            Text(
                text = task.taskTitle,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(4.dp)
            )
            Text(
                text = stringResource(
                    id = R.string.text_scheduled_time_placeholder,
                    task.scheduleTime
                ),
                modifier = Modifier.padding(4.dp)
            )

            AnimatedVisibility(visible = expanded) {
                Text(
                    text = "BOOOOOM!",
                    style = MaterialTheme.typography.h6,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun TaskListItem(task: TaskListItemViewModel, modifier: Modifier = Modifier) {
    var expanded by remember { mutableStateOf(false) }
    Card(
        elevation = 6.dp,
        onClick = {
            expanded = !expanded
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Column {
            Text(
                text = task.title,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(4.dp)
            )
            Text(
                text = stringResource(
                    id = R.string.text_created_time_placeholder,
                    task.createTime
                ),
                modifier = Modifier.padding(4.dp)
            )

            AnimatedVisibility(visible = expanded) {
                Text(
                    text = "BOOOOOM!",
                    style = MaterialTheme.typography.h6,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Preview
@Composable
fun PreviewTaskItem() {
    TaskListItem(
        task = TaskListItemViewModel(
            id = 0,
            createTime = "Today",
            completeTime = "",
            title = "Something to do!"
        )
    )
}

@Preview
@Composable
fun PreviewTaskHolder() {
    ScheduleListItem(
        task = ScheduleListItemViewModel(
            scheduleId = 0,
            taskId = 0,
            scheduleTime = "Today",
            taskTitle = "Something to do!"
        )
    )
}

