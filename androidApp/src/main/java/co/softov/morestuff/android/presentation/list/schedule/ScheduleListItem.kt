package co.softov.morestuff.android.presentation.list.schedule

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import co.softov.morestuff.android.R
import co.softov.morestuff.android.presentation.list.schedule.model.ScheduleListItemViewModel

@OptIn(ExperimentalMaterialApi::class, ExperimentalAnimationApi::class)
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
