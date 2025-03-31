package io.middlepoint.morestuff.shared.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.sharp.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mohamedrejeb.calf.permissions.ExperimentalPermissionsApi
import com.mohamedrejeb.calf.permissions.Permission
import com.mohamedrejeb.calf.permissions.PermissionStatus
import com.mohamedrejeb.calf.permissions.isGranted
import com.mohamedrejeb.calf.permissions.rememberPermissionState
import com.mohamedrejeb.calf.permissions.shouldShowRationale
import io.middlepoint.morestuff.shared.ui.components.input.voice.VoiceToTextInput
import io.middlepoint.morestuff.shared.ui.components.priority.PriorityDatePicker
import io.middlepoint.morestuff.shared.ui.extension.clearFocusOnKeyboardDismiss
import io.middlepoint.morestuff.shared.ui.model.ScheduleUiModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import morestuff.composeapp.generated.resources.Res
import morestuff.composeapp.generated.resources.cd_cancel_schedule
import morestuff.composeapp.generated.resources.cd_schedule_icon
import morestuff.composeapp.generated.resources.ic_schedule
import morestuff.composeapp.generated.resources.main_input_hint
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource


@Composable
fun InputItem(
  modifier: Modifier = Modifier,
  onDone: (String) -> Unit = {},
  onCancel: () -> Unit = {},
  onTimeChange: (Int, Int) -> Unit,
  onDateChange: (Long) -> Unit,
  onSetPriority: () -> Unit,
  onClearSetPriority: () -> Unit,
  schedule: ScheduleUiModel?,
) {
  var inputValue by rememberSaveable(
    stateSaver = TextFieldValue.Saver,
    key = "inputValue"
  ) { mutableStateOf(TextFieldValue(text = "")) }

  val focusRequester = remember { FocusRequester() }
  val keyboardController = LocalSoftwareKeyboardController.current

  LaunchedEffect(Unit) { focusRequester.requestFocus() }

  LaunchedEffect(inputValue) {
    if (inputValue.text.isEmpty()) {
      focusRequester.requestFocus()
      keyboardController?.show()
    }
  }

  Column {
    Row(
      modifier = modifier
        .fillMaxWidth()
        .height(100.dp)
        .padding(start = 24.dp, end = 12.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.Start
    ) {
      BasicTextField(
        value = inputValue,
        onValueChange = { inputValue = it },
        modifier = Modifier
          .weight(1f)
          .clearFocusOnKeyboardDismiss()
          .focusRequester(focusRequester),
        keyboardOptions = KeyboardOptions(
          capitalization = KeyboardCapitalization.Sentences,
          keyboardType = KeyboardType.Text,
          imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(
          onDone = { onDone(inputValue.text) }
        ),
        maxLines = 2,
        cursorBrush = SolidColor(MaterialTheme.colorScheme.onSurface),
        textStyle = LocalTextStyle.current.copy(
          color = MaterialTheme.colorScheme.onSurface,
          fontSize = 16.sp,
          textAlign = TextAlign.Start
        ),
        decorationBox = { innerTextField ->
          Box(modifier = Modifier.padding(vertical = 6.dp)) {
            if (inputValue.text.isEmpty()) {
              Text(
                text = stringResource(Res.string.main_input_hint),
                style = LocalTextStyle.current.copy(
                  color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                  fontSize = 16.sp
                )
              )
            }
            innerTextField()
          }
        }
      )

      IconButton(onClick = onCancel) {
        Icon(
          Icons.Sharp.Close,
          contentDescription = stringResource(Res.string.cd_cancel_schedule),
          tint = MaterialTheme.colorScheme.secondary
        )
      }
    }

    ScheduleSelectorRow(
      schedule = schedule,
      onSetPriority = onSetPriority,
      onClearSetPriority = onClearSetPriority,
      onDateChange = { newDate ->
        onDateChange(newDate)
        focusRequester.requestFocus()
        keyboardController?.show()
      },
      onTimeChange = { hour, minute ->
        onTimeChange(hour, minute)
        focusRequester.requestFocus()
        keyboardController?.show()
      },
      onUpdateInputValue = { newText ->
        inputValue = TextFieldValue(
          text = newText,
          selection = androidx.compose.ui.text.TextRange(newText.length)
        )
        focusRequester.requestFocus()
        keyboardController?.show()
      },

    )
  }
}


@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun ScheduleSelectorRow(
  schedule: ScheduleUiModel?,
  onSetPriority: () -> Unit,
  onClearSetPriority: () -> Unit,
  onDateChange: (Long) -> Unit,
  onTimeChange: (Int, Int) -> Unit,
  onUpdateInputValue: (String) -> Unit = {},
) {
  var isVisible by remember { mutableStateOf(schedule != null) }
  var showDatePickerDialog by remember { mutableStateOf(false) }
  var showTimePickerDialog by remember { mutableStateOf(false) }
  var displaySchedule by remember { mutableStateOf(schedule) }
  val coroutineScope = rememberCoroutineScope()

  val permissionState = rememberPermissionState(Permission.Notification)
  var showRationaleDialog by remember { mutableStateOf(false) }
  var hasBeenDeniedBefore by remember { mutableStateOf(false) }

  var contentWidth by remember { mutableStateOf(0.dp) }
  var rowRef = remember { mutableStateOf<androidx.compose.ui.layout.LayoutCoordinates?>(null) }

  val buttonExpanded = isVisible
  val buttonSizeAnimation by animateDpAsState(
    targetValue = if (buttonExpanded) 22.dp else 16.dp,
    animationSpec = tween(400)
  )

  val buttonBackgroundAnimation by animateDpAsState(
    targetValue = if (buttonExpanded) 40.dp else 40.dp,
    animationSpec = tween(400)
  )

  val expandedWidth by animateDpAsState(
    targetValue = if (isVisible) (contentWidth + 60.dp).coerceAtLeast(220.dp) else 48.dp,
    animationSpec = tween(400, easing = FastOutSlowInEasing)
  )

  LaunchedEffect(schedule) {
    if (schedule != null) {
      displaySchedule = schedule
      isVisible = true
    } else {
      isVisible = false
      delay(400)
      displaySchedule = null
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
      .padding(horizontal = 12.dp, vertical = 8.dp)
      .padding(bottom = 18.dp)
      .clickable(
        enabled = true,
        indication = null,
        interactionSource = remember { MutableInteractionSource() },
        onClick = { }
      ),

    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.SpaceBetween
  ) {
    Box(
      contentAlignment = Alignment.CenterStart,
      modifier = Modifier.weight(1f)
    ) {
      // expandible Background
      Box(
        modifier = Modifier
          .width(expandedWidth)
          .height(48.dp)
          .background(
            color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.12F),
            shape = if (isVisible) RoundedCornerShape(24.dp) else CircleShape
          )
      )

      // Content ( schedule button + date/time selector)
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
              if (permissionState.status.isGranted) {
                if (isVisible) {
                  isVisible = false
                  coroutineScope.launch {
                    delay(400)
                    onClearSetPriority()
                  }
                } else {
                  onSetPriority()
                  isVisible = true
                }
              } else {
                showRationaleDialog = true
              }
            },
            modifier = Modifier
              .size(buttonBackgroundAnimation)
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
          visible = isVisible,
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
                Button(
                  onClick = { showDatePickerDialog = true },
                  contentPadding = PaddingValues(horizontal = 12.dp, vertical = 2.dp),
                  colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.12F),
                    contentColor = MaterialTheme.colorScheme.secondary,
                  )
                ) {
                  Text(
                    text = displaySchedule?.displayDate ?: "Select Date",
                    style = TextStyle(
                      fontSize = 12.sp,
                      lineHeight = 20.sp,
                      fontWeight = FontWeight(400),
                      color = MaterialTheme.colorScheme.secondary,
                    )
                  )
                }
                Button(
                  onClick = { showTimePickerDialog = true },
                  contentPadding = PaddingValues(horizontal = 12.dp, vertical = 2.dp),
                  colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.12F),
                    contentColor = MaterialTheme.colorScheme.secondary,
                  )
                ) {
                  Text(
                    text = displaySchedule?.displayTime ?: "Select Time",
                    style = TextStyle(
                      fontSize = 12.sp,
                      lineHeight = 20.sp,
                      fontWeight = FontWeight(400),
                      color = MaterialTheme.colorScheme.secondary,
                    )
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
    Box(
      modifier = Modifier.size(48.dp),
      contentAlignment = Alignment.Center
    ) {
      Surface(
        modifier = Modifier.fillMaxSize(),
        shape = CircleShape,
        color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.12F)
      ) {
        Box(
          contentAlignment = Alignment.Center,
          modifier = Modifier.fillMaxSize()
        ) {
          VoiceToTextInput(
            onUpdateValue = { newText ->
              onUpdateInputValue(newText)
            },
            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            iconSize = 18.dp,
            isHomeScreen = true
          )
        }
      }
    }
  }

  if (showDatePickerDialog && displaySchedule != null) {
    val datePickerState = rememberDatePickerState(
      initialSelectedDateMillis = displaySchedule?.scheduleUtcTimeMillis ?: 0L
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

  if (showTimePickerDialog && displaySchedule != null) {
    val timePickerState = rememberTimePickerState(
      initialHour = displaySchedule?.hour ?: 0,
      initialMinute = displaySchedule?.minute ?: 0
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

          delay(500)
          if (permissionState.status.isGranted && !isVisible) {
            onSetPriority()
            isVisible = true
          }
        }
      }
    },
    onSkip = {
      showRationaleDialog = false
    }
  )
}
