package io.middlepoint.morestuff.shared.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
  var showSchedulePickers by remember { mutableStateOf(false) }
  var showDatePickerDialog by remember { mutableStateOf(false) }
  var showTimePickerDialog by remember { mutableStateOf(false) }

  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(start = 24.dp, end = 12.dp).padding(vertical = 8.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Icon(
      imageVector = vectorResource(Res.drawable.ic_schedule),
      contentDescription = stringResource(Res.string.cd_schedule_icon),
      tint = MaterialTheme.colorScheme.inverseSurface
    )
    Spacer(modifier = Modifier.width(10.dp))

    AnimatedContent(
      targetState = showSchedulePickers,
      transitionSpec = {
        fadeIn(animationSpec = tween(300)) /*+ slideInVertically { it }*/ togetherWith
            fadeOut(animationSpec = tween(300)) /*+ slideOutVertically { -it }*/
      },
      label = "Schedule Picker Transition"
    ) { showPickers ->
      if (!showPickers) {
        Button(
          onClick = { onSetPriority()
            showSchedulePickers = true },
          contentPadding = PaddingValues(horizontal = 15.dp, vertical = 8.dp),
          colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.12F),
            contentColor = MaterialTheme.colorScheme.secondary,
          )
        ) {
          Text(
            text = "Schedule",
            style = TextStyle(
              fontSize = 14.sp,
              lineHeight = 28.sp,
              fontWeight = FontWeight(400),
              color = MaterialTheme.colorScheme.secondary,
            )
          )
        }
      } else {
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
              text = schedule?.displayDate ?: "Select Date",
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
              text = schedule?.displayTime ?: "Select Time",
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
              onClearSetPriority()
              showSchedulePickers = false
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
    }

    if (showDatePickerDialog) {
      val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = schedule?.scheduleUtcTimeMillis ?: 0L
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
        initialHour = schedule?.hour ?: 0,
        initialMinute = schedule?.minute ?: 0
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


