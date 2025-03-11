package io.middlepoint.morestuff.shared.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.sharp.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.middlepoint.morestuff.shared.ui.components.priority.PlanPrioritySelector
import io.middlepoint.morestuff.shared.ui.extension.clearFocusOnKeyboardDismiss
import io.middlepoint.morestuff.shared.ui.model.ScheduleUiModel
import morestuff.composeapp.generated.resources.Res
import morestuff.composeapp.generated.resources.cd_cancel_schedule
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
  schedule: ScheduleUiModel?,
) {
  var inputValue by rememberSaveable(
    stateSaver = TextFieldValue.Saver,
    key = "inputValue"
  ) { mutableStateOf(TextFieldValue(text = "")) }

  var showSchedulePickers by remember { mutableStateOf(false) }

  val focusRequester = remember { FocusRequester() }

  LaunchedEffect(Unit) { focusRequester.requestFocus() }

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
          .width(IntrinsicSize.Max)
          .clearFocusOnKeyboardDismiss()
          .focusRequester(focusRequester),
        keyboardOptions = KeyboardOptions(
          capitalization = KeyboardCapitalization.Sentences,
          keyboardType = KeyboardType.Text,
          imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(
          onDone = {
            onDone(inputValue.text)
          }
        ),
        maxLines = 2,
        cursorBrush = SolidColor(MaterialTheme.colorScheme.onSurface),
        textStyle = LocalTextStyle.current.copy(
          color = MaterialTheme.colorScheme.onSurface,
          fontSize = 16.sp,
          textDirection = TextDirection.Content,
          textAlign = TextAlign.Start
        ),
        decorationBox = { innerTextField ->
          Box(
            modifier = Modifier.padding(bottom = 6.dp, top = 6.dp),
          ) {
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
        })

      IconButton(
        onClick = onCancel,
      ) {
        Icon(
          Icons.Sharp.Close,
          contentDescription = stringResource(Res.string.cd_cancel_schedule),
          tint = MaterialTheme.colorScheme.secondary
        )
      }

      IconButton(onClick = {
        onSetPriority()
        showSchedulePickers = !showSchedulePickers }) {
        Icon(
          imageVector = vectorResource(Res.drawable.ic_schedule),
          contentDescription = stringResource(Res.string.cd_cancel_schedule),
          tint = MaterialTheme.colorScheme.secondary
        )
      }
    }

    if (showSchedulePickers) {
      if (schedule != null){
        PlanPrioritySelector(
          scheduleModel = schedule,
          modifier = Modifier.fillMaxWidth(),
          onDateChange = onDateChange,
          onTimeChange = onTimeChange
        )
      }
    }

  }
}


