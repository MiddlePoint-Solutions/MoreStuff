package co.softov.morestuff.android.ui.onboarding

import android.content.res.Configuration
import android.os.Build
import androidx.compose.foundation.Image
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.softov.morestuff.android.R
import co.softov.morestuff.android.domain.nav.OnBoarding
import co.softov.morestuff.android.ui.navigation.ChildPages
import co.softov.morestuff.android.ui.priority.PriorityTimePicker
import co.softov.morestuff.android.ui.settings.SettingsModel
import co.softov.morestuff.android.ui.settings.SettingsViewModel
import co.softov.morestuff.android.ui.settings.rememberAppSettingState
import co.softov.morestuff.android.ui.theme.MoreStuffTheme
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.router.pages.Pages
import com.arkivanov.decompose.router.pages.PagesNavigation
import com.arkivanov.decompose.router.pages.selectNext
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel


@OptIn(ExperimentalDecomposeApi::class)
@Composable
fun OnBoardingScreen(
    onBoardingComplete: () -> Unit,
) {
    val navigation = remember { PagesNavigation<OnBoarding>() }
    val pages = remember {
        buildList {
            add(OnBoarding.Welcome)
            if (requiresNotificationsPermission()) {
                add(OnBoarding.NotificationPermission)
            }
            add(OnBoarding.DailyTaskReview)
            add(OnBoarding.ChatWithYourTask)
            add(OnBoarding.TaskReview)
            add(OnBoarding.WorkSpaceReady)
        }
    }

    ChildPages(
        source = navigation,
        modifier = Modifier.fillMaxSize(),
        initialPages = { Pages(items = pages, selectedIndex = 0) }
    ) { screen ->
        when (screen) {
            is OnBoarding.Welcome -> {
                WelcomeScreen(
                    nextButton = {
                        Button(
                            onClick = {
                                if (requiresNotificationsPermission()) {
                                    navigation.selectNext()
                                } else {
                                    onBoardingComplete()
                                }
                            },
                            modifier = Modifier
                                .width(287.dp)
                                .height(43.dp),
                            content = {
                                Text(
                                    text = stringResource(R.string.button_start),
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
                )
            }

            OnBoarding.NotificationPermission -> NotificationPermissionScreen(onNext = navigation::selectNext)
            OnBoarding.DailyTaskReview -> { DailyTaskPriorityReminders(onNext = navigation::selectNext)}
            OnBoarding.ChatWithYourTask -> ChatWithYourTaskScreen(onNext = navigation::selectNext)
            OnBoarding.TaskReview -> TaskPriorityReviewOnBoardingScreen(onNext = navigation::selectNext)
            OnBoarding.WorkSpaceReady -> ReadyScreen(onFinish = onBoardingComplete)

        }
    }
}


@Composable
private fun WelcomeScreen(
    nextButton: @Composable () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier
                .weight(1f),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "app welcome image",
                contentScale = ContentScale.None
            )

            Spacer(modifier = Modifier.height(30.dp))

            Text(
                text = stringResource(R.string.our_goal),
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.primary,
            )

            Text(
                text = stringResource(R.string.is_to_help_you_plan_and),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.secondary,
            )
        }

        Box(
            Modifier
                .padding(bottom = 56.dp)
                .align(Alignment.CenterHorizontally)
        ) {
            nextButton()
        }
    }
}


@OptIn(ExperimentalPermissionsApi::class)
@Composable
private fun NotificationPermissionScreen(onNext: () -> Unit) {

    val permissionState = if (requiresNotificationsPermission()) {
        rememberPermissionState(
            android.Manifest.permission.POST_NOTIFICATIONS
        ) { granted ->
            if (granted) {
                onNext()
            }
        }
    } else {
        error("VERSION.SDK_INT < TIRAMISU")
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier
                .weight(1f),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = stringResource(R.string.notification),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
            )
            Spacer(modifier = Modifier.padding(15.dp))
            Text(
                text = stringResource(R.string.more_stuff_requires_notification),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.secondary,
            )

            Spacer(modifier = Modifier.padding(20.dp))
            Image(
                painter = painterResource(id = R.drawable.notification_permission),
                contentDescription = "Notifications permission image",
            )
        }

        Row(
            Modifier
                .padding(bottom = 56.dp)
                .align(Alignment.CenterHorizontally)

        ) {
            Column {
                Button(
                    onClick = {
                        permissionState.launchPermissionRequest()
                    },
                    modifier = Modifier
                        .width(187.dp)
                        .height(43.dp),
                    content = {
                        Text(
                            text = stringResource(R.string.button_enable),
                            style = TextStyle(
                                fontSize = 16.sp,
                                lineHeight = 28.sp,
                                fontWeight = FontWeight(700),
                                color = MaterialTheme.colorScheme.onPrimary,
                            )
                        )
                    }
                )
                Spacer(modifier = Modifier.padding(5.dp))
                Button(
                    onClick = { onNext() },
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
}

private fun requiresNotificationsPermission() =
    Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU

@Composable
private fun ReadyScreen(onFinish: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier
                .weight(1f),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = stringResource(R.string.your_workspace),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
            )

            Spacer(modifier = Modifier.padding(20.dp))
            Image(
                painter = painterResource(id = R.drawable.onboarding_workspace),
                contentDescription = "notifications",
                contentScale = ContentScale.FillBounds
            )
        }

        Row(
            Modifier
                .padding(bottom = 56.dp)
                .align(Alignment.CenterHorizontally)

        ) {
            Column {
                Button(
                    onClick = { onFinish() },
                    modifier = Modifier
                        .width(187.dp)
                        .height(43.dp),
                    content = {
                        Text(
                            text = stringResource(R.string.button_start),
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
        }
    }
}

@Composable
private fun ChatWithYourTaskScreen(onNext: () -> Unit) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.animation_lncy8nut))
    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier
                .weight(1f),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = stringResource(R.string.chat_task),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
            )
            Text(
                text = stringResource(R.string.save_details_chat),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.secondary,
            )

            Spacer(modifier = Modifier.padding(20.dp))
            LottieAnimation(
                modifier = Modifier.size(380.dp),
                composition = composition,
                iterations = LottieConstants.IterateForever,
            )
        }

        Row(
            Modifier
                .padding(bottom = 56.dp)
                .align(Alignment.CenterHorizontally)

        ) {
            Column {
                Button(
                    onClick = { onNext() },
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
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DailyTaskPriorityReminders(
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
    val timePickerState = rememberTimePickerState(initialHour = defaultHour,
        initialMinute = defaultMinute)

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

            Box(modifier = Modifier.size(250.dp),contentAlignment = (Alignment.Center)) {
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
                            text = "${selectedTime.first}:${String.format("%02d", selectedTime.second)}",
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
                    onClick ={ showTimePicker = true },
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



@Composable
 fun TaskPriorityReviewOnBoardingScreen(
    onNext: () -> Unit,
) {
    val tasks = DefaultHintTask.tasks.map { task ->
        task.copy(title = stringResource(task.title.toInt()))
    }
    var reloadCards by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(9000)
            reloadCards++
        }

    }
    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier
                .weight(1f),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = stringResource(R.string.review_your_tasks),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
            )

            key(reloadCards) {
                ReviewCardsOnBoarding(tasks)
            }
        }

        Row(
            Modifier
                .padding(bottom = 56.dp)
                .align(Alignment.CenterHorizontally)

        ) {
            Column {
                Button(
                    onClick = { reloadCards++ },
                    modifier = Modifier
                        .width(187.dp)
                        .height(43.dp)
                        .padding(bottom = 6.dp),
                    content = {
                        Text(
                            text = stringResource(R.string.reload),
                            style = TextStyle(
                                fontSize = 16.sp,
                                lineHeight = 28.sp,
                                fontWeight = FontWeight(700),
                                color = MaterialTheme.colorScheme.onPrimary,
                            )
                        )
                    }
                )
                Button(
                    onClick = { onNext() },
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
    MoreStuffTheme {
        Column {
            WelcomeScreen {}
            NotificationPermissionScreen {}
            ReadyScreen {}
        }
    }
}
