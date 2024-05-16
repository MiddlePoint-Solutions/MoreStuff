package io.middlepoint.morestuff.android.ui.priority

import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun PriorityDatePicker(
    dismissDialog: () -> Unit,
    onDateChange: () -> Unit,
    state: DatePickerState = rememberDatePickerState(),
) {
    DatePickerDialog(
        onDismissRequest = { dismissDialog() },
        confirmButton = {
            TextButton(
                onClick = {
                    onDateChange()
                    dismissDialog()
                },
                enabled = true
            ) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    dismissDialog()
                }
            ) {
                Text("Cancel")
            }
        }
    ) {
        DatePicker(state = state)
    }
}