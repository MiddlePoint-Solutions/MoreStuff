package io.middlepoint.morestuff.shared.ui.screen.onboarding

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContent
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import formatString
import io.middlepoint.morestuff.shared.ui.components.PriorityTimePicker
import io.middlepoint.morestuff.shared.ui.components.rememberAppSettingState
import morestuff.composeapp.generated.resources.Res
import morestuff.composeapp.generated.resources.button_next
import morestuff.composeapp.generated.resources.onboarding_schedule_review_subtitle
import morestuff.composeapp.generated.resources.onboarding_schedule_review_title
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class, ExperimentalResourceApi::class)
@Composable
fun OnBoardingReviewNotificationScreen(
    onNext: () -> Unit,
    currentReviewTime: () -> Pair<Int, Int>,
    onReviewTimeChange: (time: Pair<Int, Int>) -> Unit,
) {

    val selectedTimeState = rememberAppSettingState(
        defaultValue = currentReviewTime,
        valueChanged = onReviewTimeChange
    )
    val defaultHour = selectedTimeState.value.first
    val defaultMinute = selectedTimeState.value.second

    var selectedTime by remember { mutableStateOf(selectedTimeState.value) }
    var showTimePicker by remember { mutableStateOf(false) }
    val timePickerState = rememberTimePickerState(
        initialHour = defaultHour,
        initialMinute = defaultMinute
    )

    Box(
        modifier = Modifier
            .windowInsetsPadding(WindowInsets.safeContent)
            .fillMaxSize(),
    ) {
        ConstraintLayout(
            modifier = Modifier.fillMaxSize(),
        ) {

            val (image, title, subtitle) = createRefs()

            Text(
                text = stringResource(Res.string.onboarding_schedule_review_title),
                modifier = Modifier
                    .fillMaxWidth()
                    .constrainAs(title) { top.linkTo(parent.top, margin = 40.dp) },
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontSize = 40.sp,
                    lineHeight = 44.sp,
                    fontWeight = FontWeight.Black,
                    textAlign = TextAlign.Center
                ),
                color = MaterialTheme.colorScheme.primary,
            )

            Text(
                text = stringResource(Res.string.onboarding_schedule_review_subtitle),
                modifier = Modifier
                    .constrainAs(subtitle) { top.linkTo(title.bottom, margin = 20.dp) }
                    .padding(horizontal = 20.dp),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.secondary,
            )

            Button(
                onClick = { showTimePicker = true },
                modifier = Modifier
                    .constrainAs(image) { centerTo(parent) },
                shape = RoundedCornerShape(15),
                border = BorderStroke(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.primary,
                ),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent
                ),
            ) {
                Icon(
                    imageVector = Icons.Default.Alarm,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(30.dp)
                )

                val displayTime by remember {
                    derivedStateOf {
                        "${selectedTime.first}:${formatString("%02d", selectedTime.second)}"
                    }
                }

                Text(
                    text = displayTime,
                    modifier = Modifier.padding(horizontal = 10.dp),
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 30.sp),
                    color = MaterialTheme.colorScheme.primary
                )
            }

        }

        if (showTimePicker) {
            PriorityTimePicker(
                dismissTimePicker = { showTimePicker = false },
                onTimeChange = {
                    selectedTime = timePickerState.hour to timePickerState.minute
                    selectedTimeState.value = selectedTime
                    onReviewTimeChange(selectedTime)
                    showTimePicker = false
                },
                state = timePickerState
            )
        }

        OnboardingButton(
            onClick = onNext,
            title = stringResource(Res.string.button_next),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 100.dp)
        )
    }
}

//@Preview(
//    uiMode = Configuration.UI_MODE_NIGHT_YES,
//    name = "Dark"
//)
//@Preview(
//    uiMode = Configuration.UI_MODE_NIGHT_NO,
//    name = "Light"
//)
//@Composable
//private fun Preview() {
//    MoreStuffTheme {
//        OnBoardingReviewNotificationScreen(
//            onNext = {},
//            currentReviewTime = { 9 to 0 },
//            onReviewTimeChange = {}
//        )
//    }
//}