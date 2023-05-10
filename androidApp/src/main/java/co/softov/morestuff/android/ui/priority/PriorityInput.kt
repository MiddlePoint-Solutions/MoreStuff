package co.softov.morestuff.android.ui.priority

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerState
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import co.softov.morestuff.android.domain.model.PriorityOption
import co.softov.morestuff.android.domain.model.PriorityOptionsModel
import co.softov.morestuff.android.domain.usecase.time.TimeFormatter
import co.softov.morestuff.android.ui.main.PriorityUI
import co.softov.morestuff.android.ui.main.PriorityUI.*
import kotlinx.datetime.LocalDateTime
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PriorityInput(
    priority: PriorityUI,
    planLocalTime: () -> LocalDateTime,
    onPriorityChange: (PriorityUI) -> Unit,
    modifier: Modifier = Modifier,
    timeFormatter: TimeFormatter = koinInject(),
) {

    Column(modifier = modifier) {

        var showDatePickerDialog by remember {
            mutableStateOf(false)
        }

        var showTimePickerDialog by remember {
            mutableStateOf(false)
        }

        if (showDatePickerDialog) {
            PriorityDatePicker {
                showDatePickerDialog = false
            }
        }

        if (showTimePickerDialog) {
            val timePickerState = rememberTimePickerState(
                10, 30
            )

            val dismissTimePicker: () -> Unit = {
                showTimePickerDialog = false
            }

            PriorityTimePickerDialog(dismissTimePicker, timePickerState)
        }

        AnimatedVisibility(priority == Plan) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
                        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
                    )
                    .padding(10.dp)
            ) {
                if (priority == Plan) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        PriorityButton(
                            onSelected = { showDatePickerDialog = true },
                            text = "Today",
                            shape = RoundedCornerShape(percent = 50)
                        )

                        Spacer(modifier = Modifier.width(16.dp))

                        val time by remember {
                            derivedStateOf {
                                timeFormatter.formatTimeOnly(planLocalTime().toString())
                            }
                        }

                        PriorityButton(
                            onSelected = { showTimePickerDialog = true },
                            text = time ?: "",
                            shape = RoundedCornerShape(percent = 50)
                        )
                    }
                }

            }
        }

        PrioritySelector(
            priority = priority,
            onPrioritySelected = onPriorityChange
        )
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun PriorityTimePickerDialog(
    dismissTimePicker: () -> Unit,
    timePickerState: TimePickerState = rememberTimePickerState()
) {
    DatePickerDialog(
        onDismissRequest = {
            // Dismiss the dialog when the user clicks outside the dialog or on the back
            // button. If you want to disable that functionality, simply use an empty
            // onDismissRequest.
            dismissTimePicker()

        },
        confirmButton = {
            TextButton(
                onClick = {
                    dismissTimePicker()
                },
                enabled = true
            ) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    dismissTimePicker()
                }
            ) {
                Text("Cancel")
            }
        }
    ) {
        TimePicker(state = timePickerState)
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun PriorityDatePicker(
    dismissDialog: () -> Unit
) {
    val datePickerState = rememberDatePickerState(
        yearRange = IntRange(2023, 2024) // TODO: this should be dynamic
    )
    DatePickerDialog(
        onDismissRequest = {
            // Dismiss the dialog when the user clicks outside the dialog or on the back
            // button. If you want to disable that functionality, simply use an empty
            // onDismissRequest.
            dismissDialog()
        },
        confirmButton = {
            TextButton(
                onClick = {
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
        DatePicker(state = datePickerState)
    }
}

@Composable
fun UserPriorityOptionsInput(
    model: PriorityOptionsModel,
    onOptionSelected: (PriorityOption) -> Unit = {}
) {
    Box(
        modifier = Modifier
            .background(
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
                shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
            )
            .padding(5.dp)
    ) {

    }
}

//
//@Composable
//fun TimePicker(
//    label: String,
//    value: String,
//    onValueChange: (String) -> Unit,
//    keyboardActions: KeyboardActions = KeyboardActions.Default,
//    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
//    pattern: String = "HH:mm",
//    is24HourView: Boolean = true,
//) {
//    val formatter = DateTimeFormatter.ofPattern(pattern)
//    val time = if (value.isNotBlank()) LocalTime.parse(value, formatter) else LocalTime.now()
//    val dialog = TimePickerDialog(
//        LocalContext.current,
//        { _, hour, minute -> onValueChange(LocalTime.of(hour, minute).toString()) },
//        time.hour,
//        time.minute,
//        is24HourView,
//    )
//
//    TextField(
//        value = value,
//        onValueChange = {},
//        enabled = false,
//        modifier = Modifier.clickable { dialog.show() },
//        keyboardOptions = keyboardOptions,
//        keyboardActions = keyboardActions,
//    )
//}