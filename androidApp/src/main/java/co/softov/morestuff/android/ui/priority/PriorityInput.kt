package co.softov.morestuff.android.ui.priority

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
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
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerLayoutType
import androidx.compose.material3.TimePickerState
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import co.softov.morestuff.android.R
import co.softov.morestuff.android.domain.enums.RelativeDateDisplay
import co.softov.morestuff.android.domain.usecase.time.TimeFormatter
import co.softov.morestuff.android.ui.main.PlanModel
import co.softov.morestuff.android.ui.main.PriorityUI
import co.softov.morestuff.android.ui.main.PriorityUI.*
import org.koin.compose.koinInject
import kotlin.reflect.KFunction1

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PriorityInput(
    priority: PriorityUI,
    planModel: PlanModel,
    onPriorityChange: (PriorityUI) -> Unit,
    onTimeChange: (Int, Int) -> Unit,
    onDateChange: (Long) -> Unit,
    modifier: Modifier = Modifier,
    timeFormatter: TimeFormatter = koinInject(),
) {
    Column(modifier = modifier) {

        var showDatePickerDialog by remember { mutableStateOf(false) }
        var showTimePickerDialog by remember { mutableStateOf(false) }

        val showPlanInput by remember(priority) {
            derivedStateOf { priority == Plan }
        }

        AnimatedVisibility(
            showPlanInput,
            enter = expandVertically()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
                        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
                    )
                    .padding(10.dp)
            ) {
                if (showPlanInput) {
                    val displayTime by remember(planModel) { mutableStateOf(planModel.planTime) }

                    if (showDatePickerDialog) {
                        val datePickerState = rememberDatePickerState(
                            initialSelectedDateMillis = planModel.epochMs
                        )

                        PriorityDatePicker(
                            dismissDialog = { showDatePickerDialog = false },
                            onDateChange = {
                                datePickerState.selectedDateMillis?.let { onDateChange(it) }
                            },
                            state = datePickerState
                        )
                    }

                    if (showTimePickerDialog) {
                        val timePickerState = rememberTimePickerState(
                            initialHour = planModel.hour,
                            initialMinute = planModel.minute
                        )
                        PriorityTimePicker(
                            dismissTimePicker = { showTimePickerDialog = false },
                            onTimeChange = {
                                onTimeChange(
                                    timePickerState.hour,
                                    timePickerState.minute
                                )
                            },
                            state = timePickerState
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        PriorityButton(
                            onSelected = { showDatePickerDialog = true },
                            text = getRelativeDate(planModel, timeFormatter),
                            shape = RoundedCornerShape(percent = 50)
                        )

                        Spacer(modifier = Modifier.width(16.dp))

                        val time by remember(displayTime) {
                            derivedStateOf {
                                timeFormatter.formatTimeOnly(displayTime.toString())
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
private fun getRelativeDate(
    planModel: PlanModel,
    timeFormatter: TimeFormatter
) = when (planModel.relativeDisplay) {
    RelativeDateDisplay.Today -> stringResource(R.string.relative_today)
    RelativeDateDisplay.Tomorrow -> stringResource(R.string.relative_tomorrow)
    RelativeDateDisplay.Date -> {
        timeFormatter.formatTimeDayAndMonth(planModel.planTime.toString()) ?: "Error"
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun PriorityTimePicker(
    dismissTimePicker: () -> Unit,
    onTimeChange: () -> Unit,
    state: TimePickerState = rememberTimePickerState()
) {
    Dialog(onDismissRequest = { dismissTimePicker() }) {
        Surface(
            shape = RoundedCornerShape(10.dp)
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
                        Text(stringResource(R.string.cancel))
                    }

                    TextButton(
                        onClick = {
                            onTimeChange()
                            dismissTimePicker()
                        }
                    ) {
                        Text(stringResource(R.string.ok))
                    }
                }
            }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun PriorityDatePicker(
    dismissDialog: () -> Unit,
    onDateChange: () -> Unit,
    state: DatePickerState = rememberDatePickerState()
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
