package io.middlepoint.morestuff.shared.ui.components.priority

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerLayoutType
import androidx.compose.material3.TimePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import io.middlepoint.morestuff.shared.ui.theme.surfaceContainerElevation
import morestuff.composeapp.generated.resources.Res
import morestuff.composeapp.generated.resources.cancel
import morestuff.composeapp.generated.resources.ok
import org.jetbrains.compose.resources.stringResource

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun PriorityTimePicker(
  dismissTimePicker: () -> Unit,
  onTimeChange: () -> Unit,
  state: TimePickerState = rememberTimePickerState(),
) {
  Dialog(onDismissRequest = { dismissTimePicker() }) {
    Surface(
      shape = RoundedCornerShape(10.dp),
      color = MaterialTheme.colorScheme.surfaceContainerElevation
    ) {
      Column(
        modifier = Modifier.padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        TimePicker(
          state = state,
          layoutType = TimePickerLayoutType.Vertical
        )

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.End
        ) {
          TextButton(
            onClick = {
              dismissTimePicker()
            }
          ) {
            Text(stringResource(Res.string.cancel))
          }

          TextButton(
            onClick = {
              onTimeChange()
              dismissTimePicker()
            }
          ) {
            Text(stringResource(Res.string.ok))
          }
        }
      }
    }
  }
}