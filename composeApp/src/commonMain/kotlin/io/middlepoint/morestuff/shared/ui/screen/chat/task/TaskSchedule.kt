package io.middlepoint.morestuff.shared.ui.screen.chat.task

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.layout.onGloballyPositioned
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
import io.middlepoint.morestuff.shared.ui.components.priority.PriorityDatePicker
import io.middlepoint.morestuff.shared.ui.components.priority.PriorityTimePicker
import io.middlepoint.morestuff.shared.ui.model.ScheduleUiModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import morestuff.composeapp.generated.resources.Res
import morestuff.composeapp.generated.resources.cd_cancel_schedule
import morestuff.composeapp.generated.resources.cd_schedule_icon
import morestuff.composeapp.generated.resources.ic_schedule
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource


@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun TaskSchedule(
  model: ScheduleUiModel?,
  onTimeChange: (Int, Int) -> Unit,
  onDateChange: (Long) -> Unit,
  createSchedule: () -> Unit,
  cancelSchedule: () -> Unit,
) {
  var showDatePickerDialog by remember { mutableStateOf(false) }
  var showTimePickerDialog by remember { mutableStateOf(false) }
  var showRationaleDialog by remember { mutableStateOf(false) }
  var isVisible by remember { mutableStateOf(model != null) }
  var displayModel by remember { mutableStateOf(model) }
  var contentWidth by remember { mutableStateOf(0.dp) }

  val permissionState = rememberPermissionState(Permission.Notification)
  val coroutineScope = rememberCoroutineScope()
  var hasBeenDeniedBefore by remember { mutableStateOf(false) }

  val buttonExpanded = isVisible
  val buttonSizeAnimation by animateDpAsState(
    targetValue = if (buttonExpanded) 22.dp else 16.dp,
    animationSpec = tween(400)
  )

  val expandedWidth by animateDpAsState(
    targetValue = if (isVisible) (contentWidth + 60.dp).coerceAtLeast(220.dp) else 48.dp,
    animationSpec = tween(400, easing = FastOutSlowInEasing)
  )

  LaunchedEffect(model) {
    if (model != null) {
      displayModel = model
      isVisible = true
    } else {
      isVisible = false
      delay(400)
      displayModel = null
    }
  }

  LaunchedEffect(permissionState.status) {
    if (permissionState.status is PermissionStatus.Denied) {
      hasBeenDeniedBefore = true
    }
  }

  Row(
    modifier = Modifier
      .fillMaxWidth()
      .background(MaterialTheme.colorScheme.surfaceContainerHigh)
      .padding(start = 12.dp, top = 17.dp, bottom = 7.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Box(
      contentAlignment = Alignment.CenterStart,
      modifier = Modifier.weight(1f)
    ) {
      // Expandable Background
      Box(
        modifier = Modifier
          .width(expandedWidth)
          .height(48.dp)
          .background(
            color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.12F),
            shape = if (isVisible) RoundedCornerShape(24.dp) else CircleShape
          )
      )

      // Content (schedule button + date/time selector)
      Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.height(40.dp)
      ) {
        Box(
          modifier = Modifier.size(48.dp),
          contentAlignment = Alignment.Center
        ) {
          IconButton(
            onClick = {
              when {
                permissionState.status.isGranted -> {
                  if (isVisible && displayModel != null) {
                    isVisible = false
                    coroutineScope.launch {
                      delay(400)
                      cancelSchedule()
                    }
                  } else {
                    createSchedule()
                    isVisible = true
                  }
                }

                else -> {
                  showRationaleDialog = true
                }
              }
            },
            modifier = Modifier.size(40.dp)
          ) {
            Icon(
              imageVector = vectorResource(Res.drawable.ic_schedule),
              contentDescription = stringResource(Res.string.cd_schedule_icon),
              modifier = Modifier.size(buttonSizeAnimation),
              tint = if (isVisible) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(
                alpha = 0.6f
              ),
            )
          }
        }

        // Time/date animated buttons
        AnimatedVisibility(
          visible = isVisible && displayModel != null,
          enter = fadeIn(animationSpec = tween(300)) +
                  expandHorizontally(
                    animationSpec = tween(400, easing = FastOutSlowInEasing),
                    expandFrom = Alignment.Start
                  ),
          exit = fadeOut(animationSpec = tween(300)) +
                  shrinkHorizontally(
                    animationSpec = tween(400, easing = FastOutSlowInEasing),
                    shrinkTowards = Alignment.Start
                  ),
          modifier = Modifier.padding(end = 6.dp)
        ) {
          SubcomposeLayout { constraints ->
            val placeable = subcompose("content") {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.onGloballyPositioned { coordinates ->
                  contentWidth = coordinates.size.width.toDp()
                }
              ) {
                // Date button
                Button(
                  onClick = { showDatePickerDialog = true },
                  contentPadding = PaddingValues(horizontal = 15.dp, vertical = 8.dp),
                  colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.12F),
                    contentColor = MaterialTheme.colorScheme.secondary,
                  )
                ) {
                  Text(
                    text = displayModel?.displayDate ?: "Select Date",
                    style = TextStyle(
                      fontSize = 14.sp,
                      lineHeight = 28.sp,
                      fontWeight = FontWeight(400),
                      color = MaterialTheme.colorScheme.secondary,
                    )
                  )
                }

                // Time button
                Button(
                  onClick = { showTimePickerDialog = true },
                  contentPadding = PaddingValues(horizontal = 15.dp, vertical = 8.dp),
                  colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.12F),
                    contentColor = MaterialTheme.colorScheme.secondary,
                  )
                ) {
                  Text(
                    text = displayModel?.displayTime ?: "Select Time",
                    style = TextStyle(
                      fontSize = 14.sp,
                      lineHeight = 28.sp,
                      fontWeight = FontWeight(400),
                      color = MaterialTheme.colorScheme.secondary,
                    )
                  )
                }

                // Close button
                IconButton(
                  onClick = {
                    isVisible = false
                    coroutineScope.launch {
                      delay(400)
                      cancelSchedule()
                    }
                  },
                  modifier = Modifier
                ) {
                  Icon(
                    Icons.Sharp.Close,
                    contentDescription = stringResource(Res.string.cd_cancel_schedule),
                    tint = MaterialTheme.colorScheme.secondary
                  )
                }
              }
            }.map { it.measure(constraints) }

            layout(placeable[0].width, placeable[0].height) {
              placeable[0].place(0, 0)
            }
          }
        }
      }
    }
  }

  // Date picker dialog
  if (showDatePickerDialog && displayModel != null) {
    val datePickerState = rememberDatePickerState(
      initialSelectedDateMillis = displayModel?.scheduleUtcTimeMillis
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

  // Time picker dialog
  if (showTimePickerDialog && displayModel != null) {
    val timePickerState = rememberTimePickerState(
      initialHour = displayModel?.hour ?: 0,
      initialMinute = displayModel?.minute ?: 0
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

  // Permission dialog
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
          if (permissionState.status.isGranted) {
            createSchedule()
          }
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