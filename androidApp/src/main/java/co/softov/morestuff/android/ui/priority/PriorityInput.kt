package co.softov.morestuff.android.ui.priority

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import co.softov.morestuff.android.R
import co.softov.morestuff.android.domain.enums.RelativeDateDisplay
import co.softov.morestuff.android.domain.usecase.time.TimeFormatter
import co.softov.morestuff.android.ui.home.PriorityModel
import org.koin.compose.koinInject

@Composable
fun PriorityInput(
    model: PriorityModel,
    onNowSelected: () -> Unit,
    onLaterSelected: () -> Unit,
    onPlanSelected: () -> Unit,
    onTimeChange: (Int, Int) -> Unit,
    onDateChange: (Long) -> Unit,
    modifier: Modifier = Modifier,
    timeFormatter: TimeFormatter = koinInject(),
) {

    PriorityInputContent(
        priority = model,
        modifier = modifier,
        onNowSelected = onNowSelected,
        onLaterSelected = onLaterSelected,
        onPlanSelected = onPlanSelected,
        onTimeChange = onTimeChange,
        onDateChange = onDateChange,
    )

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PriorityInputContent(
    priority: PriorityModel,
    onNowSelected: () -> Unit = {},
    onLaterSelected: () -> Unit = {},
    onPlanSelected: () -> Unit = {},
    onTimeChange: (Int, Int) -> Unit = { _, _ -> },
    onDateChange: (Long) -> Unit = {},
    modifier: Modifier = Modifier,
    timeFormatter: TimeFormatter = koinInject(),
) {
    Column(modifier = modifier) {

        var showDatePickerDialog by remember { mutableStateOf(false) }
        var showTimePickerDialog by remember { mutableStateOf(false) }

        val showPlanInput by remember(priority) {
            derivedStateOf { priority is PriorityModel.Plan }
        }

        AnimatedVisibility(
            showPlanInput,
            enter = expandVertically(),
            exit = shrinkVertically()
        ) {

            Surface {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                ) {

                    (priority as? PriorityModel.Plan)?.let { planPriority ->
                        val displayTime by remember(planPriority) { mutableStateOf(planPriority.planTime) }

                        if (showDatePickerDialog) {
                            val datePickerState = rememberDatePickerState(
                                initialSelectedDateMillis = planPriority.epochMs
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
                                initialHour = planPriority.hour,
                                initialMinute = planPriority.minute
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
                                text = getRelativeDate(planPriority, timeFormatter),
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

                            Spacer(modifier = Modifier.width(16.dp))

                            SchedulePermissionRequester()
                        }
                    }


                }
            }
        }

        PrioritySelector(
            priority = priority,
            onNowSelected = onNowSelected,
            onLaterSelected = onLaterSelected,
            onPlanSelected = onPlanSelected,
        )
    }
}

@Composable
fun getRelativeDate(
    priorityModel: PriorityModel.Plan,
    timeFormatter: TimeFormatter,
) = when (priorityModel.relativeDisplay) {
    RelativeDateDisplay.Today -> stringResource(R.string.relative_today)
    RelativeDateDisplay.Tomorrow -> stringResource(R.string.relative_tomorrow)
    RelativeDateDisplay.Date -> {
        timeFormatter.formatTimeDayAndMonth(priorityModel.planTime.toString()) ?: "Error"
    }
}

