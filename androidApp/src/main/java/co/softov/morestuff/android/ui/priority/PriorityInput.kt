package co.softov.morestuff.android.ui.priority

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import co.softov.morestuff.android.domain.usecase.time.TimeFormatter
import co.softov.morestuff.android.ui.home.PriorityInputModel
import co.softov.morestuff.android.ui.home.PriorityModel
import org.koin.compose.koinInject

@Composable
fun PriorityInput(
    model: PriorityInputModel,
    onNowSelected: () -> Unit,
    onLaterSelected: () -> Unit,
    onPlanSelected: () -> Unit,
    onTimeChange: (Int, Int) -> Unit,
    onDateChange: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    PriorityInputContent(
        model = model,
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
    model: PriorityInputModel,
    onNowSelected: () -> Unit = {},
    onLaterSelected: () -> Unit = {},
    onPlanSelected: () -> Unit = {},
    onTimeChange: (Int, Int) -> Unit = { _, _ -> },
    onDateChange: (Long) -> Unit = {},
    modifier: Modifier = Modifier,
    timeFormatter: TimeFormatter = koinInject(),
) {
    Column(
        modifier = modifier.background(color = Color.Transparent)
    ) {

        var showDatePickerDialog by remember { mutableStateOf(false) }
        var showTimePickerDialog by remember { mutableStateOf(false) }

        val showPlanInput by remember(model.priority) {
            derivedStateOf { model.priority is PriorityModel.Plan }
        }

        Surface(
            modifier = Modifier
                .align(Alignment.End)
                .padding(horizontal = 20.dp),
            tonalElevation = 5.dp,
            shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        ) {
            AnimatedVisibility(
                showPlanInput,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {


                Box(
                    modifier = Modifier
                        .wrapContentSize()
                        .padding(10.dp)
                ) {

                    val planTime by remember(model.planTime) { mutableStateOf(model.planTime) }

                    if (showDatePickerDialog) {
                        val datePickerState = rememberDatePickerState(
                            initialSelectedDateMillis = planTime.epochMs
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
                            initialHour = planTime.hour,
                            initialMinute = planTime.minute
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
                        horizontalArrangement = Arrangement.Center
                    ) {
                        PriorityButton(
                            onSelected = { showDatePickerDialog = true },
                            text = planTime.displayDate,
                            shape = RoundedCornerShape(percent = 50)
                        )

                        Spacer(modifier = Modifier.width(16.dp))

                        val time by remember(planTime) {
                            derivedStateOf {
                                timeFormatter.formatTimeOnly(planTime.toString())
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

    Surface(
        shadowElevation = 12.dp,
        tonalElevation = 5.dp,
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
    ) {
        PrioritySelector(
            priority = model.priority,
            onNowSelected = onNowSelected,
            onLaterSelected = onLaterSelected,
            onPlanSelected = onPlanSelected,
        )
    }
}
