package co.softov.morestuff.android.ui.list

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
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
fun ScheduleList(
    page: PageType,
    itemAction: () -> Unit
) {
    val lifecycleOwner = LocalView.current.findViewTreeLifecycleOwner()
    val viewModel: SchedulePageViewModel = getViewModel(
        viewModelStoreOwner = LifecycleViewModelStoreOwner(lifecycleOwner)
    ) { parametersOf(page) }
    val state by viewModel.state.collectAsState()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(8.dp)
    ) {
        when {
            state.schedules.isNotEmpty() -> items(state.schedules) {
                ScheduleListItem(it, itemAction = itemAction)
            }
            state.tasks.isNotEmpty() -> items(state.tasks) { TaskListItem(it) }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleListItem(
    task: ScheduleListItemViewModel,
    modifier: Modifier = Modifier,
    itemAction: () -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    Card(
        onClick = itemAction,
        elevation = CardDefaults.cardElevation(),
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
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskListItem(task: TaskListItemViewModel, modifier: Modifier = Modifier) {
    var expanded by remember { mutableStateOf(false) }
    Card(
        onClick = {
            expanded = !expanded
        },
        elevation = CardDefaults.cardElevation(),
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
                    style = MaterialTheme.typography.titleLarge,
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
        ),
        itemAction = {

        }
    )
}

