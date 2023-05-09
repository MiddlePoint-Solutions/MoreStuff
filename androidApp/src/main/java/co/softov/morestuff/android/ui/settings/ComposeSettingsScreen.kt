package co.softov.morestuff.android.ui.settings

import android.app.Activity
import android.view.WindowManager
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ClearAll
import androidx.compose.material.icons.filled.NotificationAdd
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.SelectAll
import androidx.compose.material.icons.filled.SmartButton
import androidx.compose.material.icons.filled.SmartScreen
import androidx.compose.material.icons.filled.Snooze
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.softov.morestuff.android.BuildConfig
import co.softov.morestuff.android.data.Constants.KEY_DEBUG_MESSAGE
import co.softov.morestuff.android.data.Constants.KEY_ENABLED_STATE_SNOOZE_LIMIT
import co.softov.morestuff.android.data.Constants.KEY_KEEP_DEVICE_SCREEN_ON
import co.softov.morestuff.android.data.Constants.KEY_REMINDER_DEBUGGING_DELAY_STATE
import co.softov.morestuff.android.data.Constants.KEY_REMINDER_DEBUGGING_SWITCH_STATE
import co.softov.morestuff.android.data.Constants.KEY_USER_SMART_REMINDER_ENABLED
import co.softov.morestuff.android.data.Constants.KEY_USER_SMART_REMINDER_STORAGE
import co.softov.morestuff.android.data.Constants.KEY_USER_SNOOZE_LIMIT
import co.softov.morestuff.android.domain.DevTools
import com.alorma.compose.settings.storage.base.rememberBooleanSettingState
import com.alorma.compose.settings.ui.SettingsList
import com.alorma.compose.settings.ui.SettingsMenuLink
import com.alorma.compose.settings.ui.SettingsSlider
import com.alorma.compose.settings.ui.SettingsSwitch
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.rememberKoinInject


@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = koinViewModel(),
    devTools: DevTools = rememberKoinInject(),
) {
    SettingsContent(
        onBack = viewModel::navigateBackSettings,
        onSnoozeLimit = viewModel::onSnoozeLimitChanged,
        onClearActiveMessages = viewModel::clearPendingMessages,
        onTestReviewActivity = viewModel::testReviewActivity,
        onSmartReminder = viewModel::smartReminderEnabled,
        devTools = devTools // TODO: This is better moved into the settings ViewModel
    )
}

@Composable
private fun SettingsContent(
    scrollState: ScrollState = rememberScrollState(),
    onBack: () -> Unit,
    onSnoozeLimit: (Int) -> Unit,
    onClearActiveMessages: () -> Unit,
    onTestReviewActivity: () -> Unit,
    onSmartReminder: (Boolean) -> Unit,
    devTools: DevTools,
) {
    Column(modifier = Modifier.fillMaxSize()) {
        SettingsTopBar(navigateBackSettings = onBack)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface)
                .verticalScroll(scrollState)
        ) {
            SelectTheme()
            SelectSnoozeLimit(onSnoozeLimitChanged = onSnoozeLimit)
            Divider(
                color = Color.Gray, thickness = 1.dp, modifier = Modifier.fillMaxWidth()
            )
            DeveloperSettings(
                clearPendingMessages = onClearActiveMessages,
                testReviewActivity = onTestReviewActivity,
            )
            DebugMessageSwitch()
            Notification()
            KeepDeviceScreenOn(devTools = devTools)
            ReminderDebugging(devTools = devTools)
            SmartReminder(onSmartReminder = onSmartReminder)
            Divider(
                color = Color.Gray, thickness = 1.dp, modifier = Modifier.fillMaxWidth()
            )
            About()
        }
    }
}

@Composable
fun DebugMessageSwitch() {
    val memoryStorage =
        rememberMultiplatformBooleanSettingState(KEY_DEBUG_MESSAGE, false)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .background(color = Color(0xff2B3438)),
        verticalAlignment = Alignment.CenterVertically
    ) {
        SettingsSwitch(
            state = memoryStorage,
            title = {
                Text(
                    text = "Debug Messages",
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Left
                )
            },
            onCheckedChange = { newValue ->
                memoryStorage.value = newValue
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsTopBar(navigateBackSettings: () -> Unit) {
    TopAppBar(
        title = {
            Text(
                text = "Settings",
                fontSize = 25.sp,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(start = 105.dp)
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color(0xff2B3438)
        ),
        navigationIcon = {
            IconButton(onClick = navigateBackSettings) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        },
    )
}

@Composable
fun SelectTheme() {
    val enabledState = rememberBooleanSettingState(true)
    val themeOptions = listOf("Light", "Dark", "System")
    Row {
        SettingsList(
            enabled = enabledState.value,
            icon = {
                Icon(
                    imageVector = Icons.Default.SelectAll,
                    contentDescription = "Select Theme") },
            title = {
                Text(
                    text = "Select Theme",

                    )
            },
            items = themeOptions,
            onItemSelected = { index, _ ->
                when (index) {
                    0 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                    1 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                    2 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                }
            })
    }
}


@Composable
fun SelectSnoozeLimit(onSnoozeLimitChanged: (Int) -> Unit) {
    val settingSnoozeLimit = rememberMultiplatformPreferenceFloatSettingState(
        KEY_USER_SNOOZE_LIMIT,  0F
    )
    val enabledState = rememberMultiplatformBooleanSettingState(
        KEY_ENABLED_STATE_SNOOZE_LIMIT,  true
    )
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        SettingsSlider(enabled = enabledState.value,
            state = settingSnoozeLimit,
            icon = {
                Icon(
                    imageVector = Icons.Default.Snooze,
                    contentDescription = "Snooze Limit") },
            title = {
                Text(
                    text = "Snooze Limit   ${settingSnoozeLimit.value.toInt()}",
                )
            },
            steps = 9,
            valueRange = 0F..10F,
            modifier = Modifier.weight(1f),
            onValueChange = { newValue ->
                onSnoozeLimitChanged(newValue.toInt())
            })
    }
}

@Composable
fun DeveloperSettings(
    clearPendingMessages: () -> Unit,
    testReviewActivity: () -> Unit,
) {
    Column {
        Row(
            modifier = Modifier.padding(15.dp), verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Developer Settings",
                fontSize = 20.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        SettingsMenuLink(
            icon = {
                Icon(
                    imageVector = Icons.Default.ClearAll,
                    contentDescription = "Clear All Message replies"
                )
            },
            title = { Text(text = "Clear All Message replies") },
            subtitle = { Text(text = "This will clear all pending message replies") },
            onClick = clearPendingMessages,
        )

        val context = LocalContext.current
        val scope = rememberCoroutineScope()
        SettingsMenuLink(
            icon = {
                Icon(
                    imageVector = Icons.Default.NotificationAdd,
                    contentDescription = "Show priority review notification"
                )
            },
            title = { Text(text = "Test priority review notification") },
            onClick = testReviewActivity,
        )
    }
}

@Composable
fun Notification() {
    //val onClick = { TODO() }
    Row(
        modifier = Modifier.padding(15.dp),
        verticalAlignment = Alignment.CenterVertically

    ) {
        Icon(
            imageVector = Icons.Default.Notifications,
            contentDescription = "Notifications",
            modifier = Modifier
                .padding(end = 18.dp)
        )
        Text(

            text = "Notification",
            color = MaterialTheme.colorScheme.onSurface,
        )
    }

    Box(
        modifier = Modifier
            .padding(top = 8.dp)
            .fillMaxWidth()
        //.clickable(onClick = onClick)
    )
}

@Composable
fun KeepDeviceScreenOn(devTools: DevTools) {
    val memoryStorage = rememberMultiplatformBooleanSettingState(KEY_KEEP_DEVICE_SCREEN_ON, false)
    val enabledState = rememberBooleanSettingState(true)
    val context = LocalContext.current
    LaunchedEffect(memoryStorage.value) {
        devTools.keepScreenOn = memoryStorage.value
    }
    Column(
        horizontalAlignment = Alignment.Start
    ) {
        SettingsSwitch(enabled = enabledState.value,
            state = memoryStorage,
            modifier = Modifier.padding(end = 16.dp),
            icon = {
                Icon(
                    imageVector = Icons.Default.SmartScreen,
                    contentDescription = "Keep device screen on") },
            title = {
                Text(
                    text = "Keep device screen on",
                    color = MaterialTheme.colorScheme.onSurface,
                )
            },
            onCheckedChange = { newValue ->
                when (newValue) {
                    true -> (context as? Activity)?.window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                    false -> (context as? Activity)?.window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                }
                memoryStorage.value = newValue
                devTools.keepScreenOn = newValue
            })
    }
}


@Composable
fun ReminderDebugging(devTools: DevTools) {
    val switchState = rememberMultiplatformBooleanSettingState(KEY_REMINDER_DEBUGGING_SWITCH_STATE, false)
    val reminderDelayState = rememberMultiplatformPreferenceFloatSettingState(KEY_REMINDER_DEBUGGING_DELAY_STATE, 60f)
    LaunchedEffect(switchState.value) {
        devTools.debugReminders = switchState.value
    }
    LaunchedEffect(reminderDelayState.value) {
        devTools.todayDebugTime = reminderDelayState.value.toInt()
    }
    Column {
        SettingsSwitch(
            enabled = true,
            modifier = Modifier.padding(end = 16.dp),
            icon = {
                Icon(
                    imageVector = Icons.Default.Alarm,
                    contentDescription = "Reminder Debugging") },
            title = {
                Text(

                    text = "Reminder Debugging",
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Left
                )
            }, state = switchState, onCheckedChange = { isChecked ->
                devTools.debugReminders = isChecked
            })
        if (switchState.value) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Today reminder delay ${reminderDelayState.value.toInt()}",
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    SettingsSlider(enabled = switchState.value,
                        state = reminderDelayState,
                        title = {},
                        steps = 119,
                        valueRange = 1F..120F,
                        modifier = Modifier.weight(1f),
                        onValueChange = { newValue ->
                            devTools.todayDebugTime = newValue.toInt()
                        })
                }
            }
        }
    }
}

@Composable
fun SmartReminder(onSmartReminder: (Boolean) -> Unit) {
    val memoryStorage =
        rememberMultiplatformBooleanSettingState(KEY_USER_SMART_REMINDER_STORAGE, false)
    val enabledState = rememberMultiplatformBooleanSettingState(KEY_USER_SMART_REMINDER_ENABLED, true)

    Column(
        horizontalAlignment = Alignment.Start,
    ) {
        SettingsSwitch(
            enabled = enabledState.defaultValue,
            state = memoryStorage,
            modifier = Modifier.padding(end = 16.dp),
            icon = {
                Icon(
                    imageVector = Icons.Default.SmartButton,
                    contentDescription = "Smart Reminder") },
            title = {
                Text(
                    text = "Smart Reminder",
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Left
                )
            },
            onCheckedChange = { newValue ->
                onSmartReminder(newValue)
                memoryStorage.value = newValue
            },
        )
    }
}

@Composable
fun Reset() {
    //val onClick = { TODO() }
    Column(modifier = Modifier.padding(16.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Reset",
                fontSize = 20.sp,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f)
            )
        }
        Box(
            modifier = Modifier
                .padding(top = 8.dp)
                .fillMaxWidth()
            //.clickable(onClick = onClick)
        ) {
            Column(
                verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = "Reset All Notifications",
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Left
                )
                Text(
                    text = "Clear all pending notification messages",
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Left
                )
            }
        }
    }
}


@Composable
fun About() {
    Column(modifier = Modifier.padding(16.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "About",
                fontSize = 20.sp,
                color = MaterialTheme.colorScheme.onSurface,

                )
        }

        Row(
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text(
                text = "Version",
                color = MaterialTheme.colorScheme.onSurface,
            )

            Text(
                text = " ${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})",
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(start = 190.dp, end = 16.dp)
            )
        }

        Box(
            modifier = Modifier
                .padding(top = 16.dp)
                .fillMaxWidth()
            //.clickable(onClick = { TODO() })
        ) {
            Column(
                horizontalAlignment = Alignment.Start,
            ) {
                Text(
                    text = "Send feedback",
                    color = MaterialTheme.colorScheme.onSurface,
                )

                Text(
                    text = "Share your thoughts or whatever",
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = 30.dp)
                )
            }
        }
    }
}

/*
@Preview
@Composable
private fun SettingsPreview() {
    MoreStuffTheme(darkTheme = true) {
        SettingsScreen()
    }
}*/
