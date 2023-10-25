package co.softov.morestuff.android.ui.onboarding

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.softov.morestuff.android.R
import co.softov.morestuff.android.ui.priority.PriorityTimePicker
import co.softov.morestuff.android.ui.settings.SettingsModel
import co.softov.morestuff.android.ui.settings.SettingsViewModel
import co.softov.morestuff.android.ui.settings.rememberAppSettingState
import co.softov.morestuff.android.ui.theme.MoreStuffTheme
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnBoardingReviewNotificationScreen(
    onNext: () -> Unit,
) {
    val viewModel: SettingsViewModel = koinViewModel()
    val defaultReviewTime = SettingsModel().reviewTime
    val selectedTimeState = rememberAppSettingState(
        defaultValue = { defaultReviewTime },
        valueChanged = { newTime ->
            viewModel.setReviewTime(newTime.first, newTime.second)
        }
    )
    val defaultHour = selectedTimeState.value.first
    val defaultMinute = selectedTimeState.value.second

    var selectedTime by remember { mutableStateOf(selectedTimeState.value) }
    var showTimePicker by remember { mutableStateOf(false) }
    val timePickerState = rememberTimePickerState(
        initialHour = defaultHour,
        initialMinute = defaultMinute
    )

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.daily_check_ins),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
            )
            Spacer(modifier = Modifier.padding(15.dp))
            Text(
                text = stringResource(R.string.daily_check_ins_description),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.secondary,
            )
            Spacer(modifier = Modifier.padding(20.dp))

            Box(modifier = Modifier.size(250.dp), contentAlignment = (Alignment.Center)) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Alarm,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(30.dp)
                    )
                    Text(
                        text = "${selectedTime.first}:${
                            String.format(
                                "%02d",
                                selectedTime.second
                            )
                        }",
                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 30.sp),
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                if (showTimePicker) {
                    PriorityTimePicker(
                        dismissTimePicker = { showTimePicker = false },
                        onTimeChange = {
                            selectedTime = Pair(timePickerState.hour, timePickerState.minute)
                            selectedTimeState.value = selectedTime
                            viewModel.setReviewTime(timePickerState.hour, timePickerState.minute)
                            showTimePicker = false
                        },
                        state = timePickerState
                    )
                }
            }

        }

        Column(
            Modifier
                .padding(bottom = 56.dp)
                .align(Alignment.CenterHorizontally),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {

            if (selectedTime == defaultReviewTime) {
                Button(
                    onClick = { showTimePicker = true },
                    modifier = Modifier
                        .width(187.dp)
                        .height(43.dp),
                    content = {
                        Text(
                            text = stringResource(R.string.add_reminder),
                            style = TextStyle(
                                fontSize = 16.sp,
                                lineHeight = 28.sp,
                                fontWeight = FontWeight(700),
                                color = MaterialTheme.colorScheme.onPrimary,
                            )
                        )
                    }
                )
            } else {
                Button(
                    onClick = onNext,
                    modifier = Modifier
                        .width(187.dp)
                        .height(43.dp),
                    content = {
                        Text(
                            text = stringResource(R.string.button_next),
                            style = TextStyle(
                                fontSize = 16.sp,
                                lineHeight = 28.sp,
                                fontWeight = FontWeight(700),
                                color = MaterialTheme.colorScheme.onPrimary,
                            )
                        )
                    }
                )
            }

            Button(
                onClick = onNext,
                modifier = Modifier
                    .width(187.dp)
                    .height(43.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                content = {
                    Text(
                        text = stringResource(R.string.button_skip),
                        style = TextStyle(
                            fontSize = 16.sp,
                            lineHeight = 28.sp,
                            fontWeight = FontWeight(700),
                            color = MaterialTheme.colorScheme.secondary,
                        )
                    )
                }
            )
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
    MoreStuffTheme {
        OnBoardingReviewNotificationScreen {}
    }
}