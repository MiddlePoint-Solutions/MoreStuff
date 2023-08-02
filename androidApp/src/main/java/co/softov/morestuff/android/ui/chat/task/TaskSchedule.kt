package co.softov.morestuff.android.ui.chat.task

import android.content.res.Configuration
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.sharp.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.softov.morestuff.android.R
import co.softov.morestuff.android.ui.home.PlanModel
import co.softov.morestuff.android.ui.priority.PriorityButton
import co.softov.morestuff.android.ui.priority.PriorityDatePicker
import co.softov.morestuff.android.ui.priority.PriorityTimePicker
import co.softov.morestuff.android.ui.theme.MoreStuffTheme
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskSchedule(
    planModel: PlanModel?,
    onTimeChange: (Int, Int) -> Unit,
    onDateChange: (Long) -> Unit,
    createPlanSchedule: () -> Unit,
    cancelActiveSchedule: () -> Unit,
) {

    var showDatePickerDialog by remember { mutableStateOf(false) }
    var showTimePickerDialog by remember { mutableStateOf(false) }

    AnimatedContent(
        targetState = planModel,
        label = "",
        contentKey = { it != null }
    ) { plan ->
        when (plan) {
            null -> {
                PriorityButton(
                    onClick = createPlanSchedule,
                    text = stringResource(id = R.string.task_chat_schedule_reminder),
                    shape = RoundedCornerShape(percent = 50),
                )
            }

            else -> {
                Row {
                    if (showDatePickerDialog) {
                        val datePickerState = rememberDatePickerState(
                            initialSelectedDateMillis = plan.epochMs
                        )

                        PriorityDatePicker(
                            dismissDialog = { showDatePickerDialog = false },
                            onDateChange = {
                                datePickerState.selectedDateMillis?.let {
                                    onDateChange(it)
                                }
                                showDatePickerDialog = false
                            },
                            state = datePickerState,
                        )
                    }

                    if (showTimePickerDialog) {
                        val timePickerState = rememberTimePickerState(
                            initialHour = plan.hour,
                            initialMinute = plan.minute
                        )
                        PriorityTimePicker(
                            dismissTimePicker = { showTimePickerDialog = false },
                            onTimeChange = {
                                onTimeChange(
                                    timePickerState.hour,
                                    timePickerState.minute
                                )
                                showTimePickerDialog = false
                            },
                            state = timePickerState
                        )
                    }

                    Button(
                        onClick = { showDatePickerDialog = true },
                        contentPadding = PaddingValues(horizontal = 15.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = plan.displayDate,
                            style = TextStyle(
                                fontSize = 14.sp,
                                lineHeight = 28.sp,
                                fontWeight = FontWeight(400),
                                color = Color(0xFFFFFFFF),
                            )
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Button(
                        onClick = { showTimePickerDialog = true },
                        contentPadding = PaddingValues(horizontal = 15.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = plan.displayTime,
                            style = TextStyle(
                                fontSize = 14.sp,
                                lineHeight = 28.sp,
                                fontWeight = FontWeight(400),
                                color = Color(0xFFFFFFFF),
                            )
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    IconButton(
                        onClick = { cancelActiveSchedule() },
                        modifier = Modifier
                    ) {
                        Icon(
                            Icons.Sharp.Close,
                            contentDescription = stringResource(R.string.cd_cancel_schedule),
                        )
                    }
                }
            }
        }
    }
}

@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    name = "DefaultPreviewDark"
)
@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    name = "DefaultPreviewLight"
)
@Composable
private fun TaskSchedulePreview() {
    MoreStuffTheme {
        TaskSchedule(
            planModel = PlanModel(
                localDateTime = Clock.System.now().toLocalDateTime(TimeZone.UTC),
                displayDate = "Saturday, July 29",
                displayTime = "15:30"
            ),
            onDateChange = {},
            onTimeChange = { _, _ -> },
            cancelActiveSchedule = {},
            createPlanSchedule = {}
        )
    }
}