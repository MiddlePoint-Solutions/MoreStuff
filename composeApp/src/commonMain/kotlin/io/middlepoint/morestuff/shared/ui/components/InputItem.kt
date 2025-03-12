package io.middlepoint.morestuff.shared.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
        .height(78.dp)
        .padding(start = 24.dp, end = 12.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.Start
    ) {
      BasicTextField(
        value = inputValue,
        onValueChange = { inputValue = it },
        modifier = Modifier
          .weight(0.95f)
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
      }
    )
  }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleSelectorRow(
  schedule: ScheduleUiModel?,
  onSetPriority: () -> Unit,
  onClearSetPriority: () -> Unit,
  onDateChange: (Long) -> Unit,
  onTimeChange: (Int, Int) -> Unit,
) {
  var isVisible by remember { mutableStateOf(schedule != null) }
  var showDatePickerDialog by remember { mutableStateOf(false) }
  var showTimePickerDialog by remember { mutableStateOf(false) }
  var displaySchedule by remember { mutableStateOf(schedule) }
  val coroutineScope = rememberCoroutineScope()

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

  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(start = 12.dp, end = 12.dp).padding(vertical = 8.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    IconButton(
      onClick = {
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
      },
      modifier = Modifier.size(52.dp)
    ) {
      Icon(
        imageVector = vectorResource(Res.drawable.ic_schedule),
        contentDescription = stringResource(Res.string.cd_schedule_icon),
        tint = MaterialTheme.colorScheme.inverseSurface,
      )
    }
    Spacer(modifier = Modifier.width(10.dp))

    AnimatedVisibility(
      visible = isVisible,
      enter = slideInHorizontally(initialOffsetX = { -it }, animationSpec = tween(400)) +
          fadeIn(animationSpec = tween(300)),
      exit = slideOutHorizontally(targetOffsetX = { -it }, animationSpec = tween(400)) +
          fadeOut(animationSpec = tween(300))
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Button(
          onClick = { showDatePickerDialog = true },
          contentPadding = PaddingValues(horizontal = 15.dp, vertical = 8.dp),
          colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.12F),
            contentColor = MaterialTheme.colorScheme.secondary,
          )
        ) {
          Text(
            text = displaySchedule?.displayDate ?: "Select Date",
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
            text = displaySchedule?.displayTime ?: "Select Time",
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
          onClick = {
            isVisible = false
            coroutineScope.launch {
              delay(400)
              onClearSetPriority()
            }
          }
        ) {
          Icon(
            Icons.Sharp.Close,
            contentDescription = stringResource(Res.string.cd_cancel_schedule),
            tint = MaterialTheme.colorScheme.secondary
          )
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
  }
}


