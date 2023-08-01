package co.softov.morestuff.android.ui.search

import android.content.res.Configuration
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.softov.morestuff.android.R
import co.softov.morestuff.android.domain.model.ScheduleDomain
import co.softov.morestuff.android.domain.model.ScheduleType
import co.softov.morestuff.android.domain.model.TaskDomain
import co.softov.morestuff.android.ui.schedule.PriorityItem
import co.softov.morestuff.android.ui.schedule.TaskBadges
import co.softov.morestuff.android.ui.schedule.TaskProfile
import co.softov.morestuff.android.ui.theme.MoreStuffTheme

@Composable
fun CompletePriorityItem(
    task: TaskDomain,
    onClick: (taskId: Long) -> Unit,
    modifier: Modifier = Modifier,
    isDragging: Boolean = false,
) {
    val elevation = animateDpAsState(if (isDragging) 16.dp else 0.dp, label = "").value
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = 90.dp)
            .shadow(elevation = elevation)
            .background(MaterialTheme.colorScheme.background)
    ) {
        Row(
            modifier = modifier
                .fillMaxSize()
                .clickable { onClick(task.id) },
            verticalAlignment = Alignment.CenterVertically,
        ) {

            TaskProfile(task)
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = task.title,
                    textAlign = TextAlign.Start,
                    modifier = Modifier
                        .fillMaxWidth(0.90f)
                        .padding(12.dp),
                    maxLines = 2,
                    color = MaterialTheme.colorScheme.onSurface,
                    overflow = TextOverflow.Ellipsis,
                    style = TextStyle(
                        fontSize = 16.sp,
                        lineHeight = 18.sp,
                        fontWeight = FontWeight(700),
                        color = Color(0xFFFFFFFF),
                    )
                )
                task.completeTime?.let { completeTime ->
                    if (task.isComplete) {
                        Text(
                            text = stringResource(R.string.completed) + " $completeTime",
                            textAlign = TextAlign.Start,
                            modifier = Modifier.padding(start = 25.dp, bottom = 8.dp)
                                .align(Alignment.Start),
                            style = TextStyle(
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 12.sp,
                            )
                        )
                    }
                }
            }

        }

        TaskBadges(
            task = task,
            modifier = Modifier.align(Alignment.BottomEnd)
        )
    }
}

@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    name = "DefaultPreviewDark"
)
@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    name = "DefaultPreviewLight"
)
@Composable
fun PriorityItemPreview() {
    MoreStuffTheme {
        PriorityItem(
            task = TaskDomain(
                title = "Buy milk & bread & cheese & wine & chocolate & ice-cream & something mmm",
                activeSchedule = ScheduleDomain(
                    scheduleType = ScheduleType.OneTime
                )
            ),
            onClick = {}
        )
    }
}