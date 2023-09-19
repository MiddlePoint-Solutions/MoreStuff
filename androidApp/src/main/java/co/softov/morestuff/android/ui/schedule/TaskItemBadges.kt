package co.softov.morestuff.android.ui.schedule

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notes
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import co.softov.morestuff.android.R
import co.softov.morestuff.android.domain.enums.ScheduleType
import co.softov.morestuff.android.domain.model.TaskDomain

@Composable
 fun TaskItemBadges(
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
                if (task.extraDetails) {
                    Icon(
                        imageVector = Icons.Default.Notes,
                        contentDescription = stringResource(R.string.cd_task_message_icon),
                        modifier = Modifier.size(16.dp)
                    )
                }
                task.schedule.forEach { schedule ->
                    when (schedule.scheduleType) {
                        ScheduleType.OneTime -> {
                            Icon(
                                imageVector = ImageVector.vectorResource(R.drawable.ic_schedule),
                                contentDescription = stringResource(R.string.cd_scheduled_task_icon),
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