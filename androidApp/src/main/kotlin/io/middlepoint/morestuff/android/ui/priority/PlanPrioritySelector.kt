package io.middlepoint.morestuff.android.ui.priority

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.middlepoint.morestuff.android.ui.model.ScheduleUiModel
import io.middlepoint.morestuff.android.ui.theme.MoreStuffTheme
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun PlanPrioritySelector(
    scheduleModel: ScheduleUiModel,
    modifier: Modifier = Modifier,
    onDateChange: (Long) -> Unit = {},
    onTimeChange: (Int, Int) -> Unit = { _, _ -> },
) {

    var showDatePickerDialog by remember { mutableStateOf(false) }
    var showTimePickerDialog by remember { mutableStateOf(false) }

    val isPastTime by remember(scheduleModel) { derivedStateOf { scheduleModel.isTimeInPast } }

    if (showDatePickerDialog) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = scheduleModel.scheduleUtcTimeMillis,
            selectableDates = object : SelectableDates {
                override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                    return utcTimeMillis >= scheduleModel.dayStartUtcTimeMillis
                }

                override fun isSelectableYear(year: Int): Boolean {
                    return year >= scheduleModel.scheduleLocalDateTime.year
                }
            }
        )

        PriorityDatePicker(
            dismissDialog = { showDatePickerDialog = false },
            onDateChange = { datePickerState.selectedDateMillis?.let { onDateChange(it) } },
            state = datePickerState
        )
    }

    if (showTimePickerDialog) {
        val timePickerState = rememberTimePickerState(
            initialHour = scheduleModel.hour,
            initialMinute = scheduleModel.minute
        )
        PriorityTimePicker(
            dismissTimePicker = { showTimePickerDialog = false },
            onTimeChange = { onTimeChange(timePickerState.hour, timePickerState.minute) },
            state = timePickerState
        )
    }

    Surface(
        modifier = modifier.padding(horizontal = 20.dp),
        tonalElevation = 10.dp,
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            SetSchedulePriorityButton(
                onClick = { showDatePickerDialog = true },
            ) {
                Text(
                    text = scheduleModel.displayDate,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            SetSchedulePriorityButton(
                onClick = { showTimePickerDialog = true },
                backgroundColor = if (isPastTime) {
                    MaterialTheme.colorScheme.error
                } else {
                    MaterialTheme.colorScheme.primaryContainer
                }
            ) {
                Text(
                    text = scheduleModel.displayTime,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

        }
    }
}

@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    name = "Dark"
)
@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    name = "Light"
)
@Composable
private fun Preview() {
    val time = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
    MoreStuffTheme {
        PlanPrioritySelector(
            scheduleModel = ScheduleUiModel(
                time,
                "Jan, 31 2007",
                "00:00",
                dayStartUtcTimeMillis = 0,
                scheduleUtcTimeMillis = 1,
                currentUtcTimeMillis = 0
            )
        )
    }
}