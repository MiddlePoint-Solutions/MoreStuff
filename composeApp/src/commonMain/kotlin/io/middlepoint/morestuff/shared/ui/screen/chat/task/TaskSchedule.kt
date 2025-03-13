package io.middlepoint.morestuff.shared.ui.screen.chat.task

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.sharp.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mohamedrejeb.calf.permissions.ExperimentalPermissionsApi
import com.mohamedrejeb.calf.permissions.Permission
import com.mohamedrejeb.calf.permissions.PermissionStatus
import com.mohamedrejeb.calf.permissions.isGranted
import com.mohamedrejeb.calf.permissions.rememberPermissionState
import com.mohamedrejeb.calf.permissions.shouldShowRationale
import io.middlepoint.morestuff.shared.ui.components.NotificationPermissionDialog
import io.middlepoint.morestuff.shared.ui.components.priority.PriorityButton
import io.middlepoint.morestuff.shared.ui.components.priority.PriorityDatePicker
import io.middlepoint.morestuff.shared.ui.components.priority.PriorityTimePicker
import io.middlepoint.morestuff.shared.ui.model.ScheduleUiModel
import kotlinx.coroutines.launch
import morestuff.composeapp.generated.resources.Res
import morestuff.composeapp.generated.resources.cd_cancel_schedule
import org.jetbrains.compose.resources.stringResource


@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun TaskSchedule(
    model: ScheduleUiModel?,
    actionText: String,
    onTimeChange: (Int, Int) -> Unit,
    onDateChange: (Long) -> Unit,
    createSchedule: () -> Unit,
    cancelSchedule: () -> Unit,
    icon: @Composable () -> Unit = {},
) {
    var showDatePickerDialog by remember { mutableStateOf(false) }
    var showTimePickerDialog by remember { mutableStateOf(false) }
    var showRationaleDialog by remember { mutableStateOf(false) }

    val permissionState = rememberPermissionState(Permission.Notification)
    val coroutineScope = rememberCoroutineScope()
    var hasBeenDeniedBefore by remember { mutableStateOf(false) }


    LaunchedEffect(permissionState.status) {
        if (permissionState.status.isGranted) {
            createSchedule()
        } else if (permissionState.status is PermissionStatus.Denied) {
            hasBeenDeniedBefore = true
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primaryContainer)
            .padding(start = 12.dp, top = 17.dp, bottom = 7.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        icon()

        Spacer(modifier = Modifier.width(13.dp))

        when (model) {
            null -> {
                PriorityButton(
                    onClick = {
                        when {
                            permissionState.status.isGranted -> {
                                createSchedule()
                            }
                            else -> {
                                showRationaleDialog = true
                            }
                        }
                    },
                    text = actionText,
                    shape = RoundedCornerShape(percent = 50),
                )
            }
            else -> {
                if (showDatePickerDialog) {
                    val datePickerState = rememberDatePickerState(
                        initialSelectedDateMillis = model.scheduleUtcTimeMillis
                    )

                    PriorityDatePicker(
                        dismissDialog = { showDatePickerDialog = false },
                        onDateChange = {
                            datePickerState.selectedDateMillis?.let { onDateChange(it) }
                            showDatePickerDialog = false
                        },
                        state = datePickerState,
                    )
                }

                if (showTimePickerDialog) {
                    val timePickerState = rememberTimePickerState(
                        initialHour = model.hour,
                        initialMinute = model.minute
                    )
                    PriorityTimePicker(
                        dismissTimePicker = { showTimePickerDialog = false },
                        onTimeChange = {
                            onTimeChange(timePickerState.hour, timePickerState.minute)
                            showTimePickerDialog = false
                        },
                        state = timePickerState
                    )
                }

                Button(
                    onClick = { showDatePickerDialog = true },
                    contentPadding = PaddingValues(horizontal = 15.dp, vertical = 8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.12F),
                        contentColor = MaterialTheme.colorScheme.secondary,
                    )
                ) {
                    Text(
                        text = model.displayDate,
                        style = TextStyle(
                            fontSize = 14.sp,
                            lineHeight = 28.sp,
                            fontWeight = FontWeight(400),
                            color = MaterialTheme.colorScheme.secondary,
                        )
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Button(
                    onClick = { showTimePickerDialog = true },
                    contentPadding = PaddingValues(horizontal = 15.dp, vertical = 8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.12F),
                        contentColor = MaterialTheme.colorScheme.secondary,
                    )
                ) {
                    Text(
                        text = model.displayTime,
                        style = TextStyle(
                            fontSize = 14.sp,
                            lineHeight = 28.sp,
                            fontWeight = FontWeight(400),
                            color = MaterialTheme.colorScheme.secondary,
                        )
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(
                    onClick = { cancelSchedule() },
                    modifier = Modifier
                ) {
                    Icon(
                        Icons.Sharp.Close,
                        contentDescription = stringResource(Res.string.cd_cancel_schedule),
                        tint = MaterialTheme.colorScheme.secondary
                    )
                }
            }
        }
    }

    NotificationPermissionDialog(
        showDialog = showRationaleDialog,
        onDismiss = {
            showRationaleDialog = false
        },
        onGrantPermission = {
            showRationaleDialog = false

            coroutineScope.launch {
                if (hasBeenDeniedBefore && !permissionState.status.shouldShowRationale) {
                    permissionState.openAppSettings()
                } else {
                    permissionState.launchPermissionRequest()
                }
            }
        },
        onSkip = {
            showRationaleDialog = false
        }
    )
}




//@Preview(
//    uiMode = Configuration.UI_MODE_NIGHT_YES,
//    name = "Dark"
//)
//@Preview(
//    uiMode = Configuration.UI_MODE_NIGHT_NO,
//    name = "Light"
//)
//@Composable
//private fun TaskSchedulePreview() {
//    MoreStuffTheme {
//        TaskSchedule(
//            model = ScheduleUiModel(
//                scheduleLocalDateTime = Clock.System.now().toLocalDateTime(TimeZone.UTC),
//                displayDate = "Saturday, July 29",
//                displayTime = "15:30",
//                dayStartUtcTimeMillis = 0,
//                scheduleUtcTimeMillis = 0,
//                currentUtcTimeMillis = 0
//            ),
//            actionText = "Schedule",
//            onDateChange = {},
//            onTimeChange = { _, _ -> },
//            cancelSchedule = {},
//            createSchedule = {},
//        )
//    }
//}