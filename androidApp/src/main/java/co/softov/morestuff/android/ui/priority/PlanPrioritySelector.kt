package co.softov.morestuff.android.ui.priority

import android.content.res.Configuration
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.softov.morestuff.android.ui.home.PriorityInputModel
import co.softov.morestuff.android.ui.home.PriorityModel
import co.softov.morestuff.android.ui.home.ScheduleUiModel
import co.softov.morestuff.android.ui.theme.MoreStuffTheme
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun PlanPrioritySelector(
    model: PriorityInputModel,
    modifier: Modifier = Modifier,
    onDateChange: (Long) -> Unit = {},
    onTimeChange: (Int, Int) -> Unit = { _, _ -> },
) {

    var showDatePickerDialog by remember { mutableStateOf(false) }
    var showTimePickerDialog by remember { mutableStateOf(false) }

    val planTime by remember(model.planTime) { mutableStateOf(model.planTime) }

    if (showDatePickerDialog) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = planTime.epochMs
        )

        PriorityDatePicker(
            dismissDialog = { showDatePickerDialog = false },
            onDateChange = { datePickerState.selectedDateMillis?.let { onDateChange(it) } },
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
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
        ) {
            SetSchedulePriorityButton(
                onClick = { showDatePickerDialog = true },
            ) {
                Text(
                    text = planTime.displayDate,
                    style = TextStyle(
                        fontSize = 11.sp,
                        lineHeight = 16.sp,
                        fontWeight = FontWeight(500),
                    )
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            SetSchedulePriorityButton(
                onClick = { showTimePickerDialog = true },
            ) {
                Text(
                    text = planTime.displayTime,
                    style = TextStyle(
                        fontSize = 11.sp,
                        lineHeight = 16.sp,
                        fontWeight = FontWeight(500),
                    )
                )
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
private fun Preview() {
    val time = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
    MoreStuffTheme {
        PlanPrioritySelector(
            PriorityInputModel(
                priority = PriorityModel.Now,
                planTime = ScheduleUiModel(time, "Jan, 31 2007", "00:00")
            )
        )
    }
}