package co.softov.morestuff.android.ui.schedule

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notes
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
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
import co.softov.morestuff.android.ui.theme.MoreStuffTheme
import co.softov.morestuff.android.ui.utils.ProfileColors

@Composable
fun PriorityItem(
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
        }

        TaskBadges(
            task = task,
            modifier = Modifier.align(Alignment.BottomEnd)
        )
    }
}

@Composable
private fun TaskBadges(
    task: TaskDomain,
    modifier: Modifier = Modifier
) {
    CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurface) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.align(Alignment.TopEnd),
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.spacedBy(5.dp),
            ) {
                task.activeSchedule?.let { activeSchedule ->
                    when (activeSchedule.scheduleType) {
                        ScheduleType.OneTime -> {

                            Icon(
                                imageVector = Icons.Default.Schedule,
                                contentDescription = stringResource(R.string.cd_scheduled_task_icon),
                                modifier = Modifier.size(16.dp),
                            )

                            Icon(
                                imageVector = Icons.Default.NotificationsActive,
                                contentDescription = stringResource(R.string.cd_task_reminder_icon),
                                modifier = Modifier.size(16.dp),
                            )

                            Icon(
                                imageVector = Icons.Default.Notes,
                                contentDescription = stringResource(R.string.cd_task_reminder_icon),
                                modifier = Modifier.size(16.dp),
                            )

                        }

                        ScheduleType.Reminder -> {
                            Icon(
                                imageVector = Icons.Default.NotificationsActive,
                                contentDescription = stringResource(R.string.cd_task_reminder_icon),
                                modifier = Modifier.size(16.dp),
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TaskProfile(task: TaskDomain) {

    val profileColor by remember {
        derivedStateOf { ProfileColors.getProfileColorsForTask(task.title) }
    }

    Box(
        modifier = Modifier
            .padding(start = 14.dp, top = 14.dp, bottom = 14.dp)
            .clip(CircleShape)
            .size(58.dp)
            .background(
                brush = Brush.verticalGradient(profileColor)
            )
    ) {
        Text(
            text = task.title[0].uppercase(),
            modifier = Modifier.align(Alignment.Center),
            style = TextStyle(
                fontSize = 24.sp,
                lineHeight = 23.8.sp,
                fontWeight = FontWeight(900),
                color = Color(0xFFFFFFFF),
            )
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